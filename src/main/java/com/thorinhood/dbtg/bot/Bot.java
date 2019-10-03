package com.thorinhood.dbtg.bot;

import com.thorinhood.dbtg.models.PracticeTask;
import com.thorinhood.dbtg.models.Student;
import com.thorinhood.dbtg.repositories.PracticeTasksRepository;
import com.thorinhood.dbtg.repositories.StudentsRepository;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class Bot extends TelegramLongPollingBot {

    private static String PROFILE_INFO = "telegram id : %s\nemail : %s\nfirst name : %s\n" +
        "last name : %s\ngroup : %s\nsub group : %s";

    private String token;
    private StudentsRepository studentsRepository;
    private PracticeTasksRepository practiceTasksRepository;
    private Map<Long, DialogStep<Update>> dialog;

    public Bot(String token, StudentsRepository studentsRepository, PracticeTasksRepository practiceTasksRepository) {
        this.token = token;
        this.studentsRepository = studentsRepository;
        this.practiceTasksRepository = practiceTasksRepository;
        dialog = new ConcurrentHashMap<>();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText() && update.getMessage().isUserMessage()) {
            try {
                Long chatId = update.getMessage().getChatId();
                if (!dialog.containsKey(chatId)) {
                    processMessage(update.getMessage());
                } else {
                    DialogStep<Update> current = dialog.get(chatId);
                    if (current.step(update)) {
                        dialog.remove(chatId);
                    }
                }
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    private void processMessage(Message message) throws TelegramApiException {
        switch (message.getText()) {
            case "/start":
                createDefaultKeyBoard(message.getChatId(), true);
                break;
            case "Профиль":
                getProfile(message.getChatId(), message.getFrom());
                break;
            case "Задания":
                waitTaskNumber(message.getChatId());
                break;
        }
    }

    private void waitTaskNumber(Long chatId) throws TelegramApiException {
        dialog.put(chatId, this::getPdfOfTask);
        makeCurrentKeyBoard(chatId, "Какое задание?", true,
            practiceTasksRepository.getAllIds().stream().map(String::valueOf).toArray(String[]::new));
    }

    private boolean getPdfOfTask(Update update) throws TelegramApiException {
        Long chatId = update.getMessage().getChatId();
        Integer id = null;

        if (update.getMessage().getText().equals("Назад")) {
            createDefaultKeyBoard(chatId, false);
            return true;
        }

        try {
            id = Integer.valueOf(update.getMessage().getText().trim());
        } catch (NumberFormatException exception) {
            sendMessage(chatId, "Введи номер задания.");
            return false;
        }

        Optional<PracticeTask> practiceTask = practiceTasksRepository.findById(id);
        if (practiceTask.isEmpty()) {
            sendMessage(chatId, "Задание с таким номером не найдено. Попробуй снова.");
            return false;
        } else {
            sendPdf(chatId, "Practice №" + id, practiceTask.get().getTask());
            createDefaultKeyBoard(chatId, false);
            return true;
        }
    }

    private void getProfile(Long chatId, User user) throws TelegramApiException {
        Optional<Student> students = studentsRepository.findById(String.valueOf(user.getId()));
        String text = students.isEmpty() ? "Не найден." : String.format(PROFILE_INFO,
            students.get().getTelegramId(),
            students.get().getEmail(),
            students.get().getFirstName(),
            students.get().getLastName(),
            students.get().getGroup(),
            students.get().getSubGroup());
        sendMessage(chatId, text);
    }

    private void sendPdf(Long chatId, String title, byte[] pdf) throws TelegramApiException {
        InputFile inputFile = new InputFile();
        inputFile.setMedia(new ByteArrayInputStream(pdf), title);
        SendDocument sendDocument = new SendDocument()
            .setDocument(inputFile)
            .setChatId(chatId);
        execute(sendDocument);
    }

    private void sendMessage(Long chatId, String text) throws TelegramApiException {
        SendMessage message = new SendMessage()
            .setChatId(chatId)
            .setText(text);
        execute(message);
    }

    private void createDefaultKeyBoard(Long chatId, boolean greeting) throws TelegramApiException {
        SendMessage message = new SendMessage()
            .setChatId(chatId);
        if (greeting) {
            message.setText("Привет!");
        } else {
            message.setText("Чем помочь?");
        }
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add("Профиль");
        row.add("Задания");
        keyboard.add(row);
        keyboardMarkup.setKeyboard(keyboard);
        message.setReplyMarkup(keyboardMarkup);
        execute(message);
    }

    private void makeCurrentKeyBoard(Long chatId, String text, boolean backBtn, String... options) throws TelegramApiException {
        SendMessage message = new SendMessage()
            .setChatId(chatId)
            .setText(text);
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        List.of(options).forEach(row::add);
        if (backBtn) {
            row.add("Назад");
        }
        keyboard.add(row);
        keyboardMarkup.setKeyboard(keyboard);
        message.setReplyMarkup(keyboardMarkup);
        execute(message);
    }

    @Override
    public String getBotUsername() {
        return "database_course_bot";
    }

    @Override
    public String getBotToken() {
        return token;
    }

}
