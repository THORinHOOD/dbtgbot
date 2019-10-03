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

    private enum State {
        Normal, WaitingForTaskNumber;
    }

    private String token;
    private StudentsRepository studentsRepository;
    private PracticeTasksRepository practiceTasksRepository;
    private Map<Long, State> states;

    public Bot(String token, StudentsRepository studentsRepository, PracticeTasksRepository practiceTasksRepository) {
        this.token = token;
        this.studentsRepository = studentsRepository;
        this.practiceTasksRepository = practiceTasksRepository;
        states = new ConcurrentHashMap<>();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText() && update.getMessage().isUserMessage()) {
            try {
                Long chatId = update.getMessage().getChatId();
                if (!states.containsKey(chatId) || states.get(chatId) == State.Normal) {
                    if (update.getMessage().getText().equals("/start")) {
                        createKeyBoard(update.getMessage().getChatId());
                    } else {
                        processMessage(update.getMessage());
                    }
                } else if (states.containsKey(chatId) && states.get(chatId) == State.WaitingForTaskNumber) {
                    try {
                        Integer id = Integer.valueOf(update.getMessage().getText());
                        Optional<PracticeTask> practiceTask = practiceTasksRepository.findById(id);
                        if (practiceTask.isEmpty()) {
                            SendMessage message = new SendMessage()
                                .setChatId(chatId)
                                .setText("Задание с таким номером не найдено. Попробуй снова.");
                            execute(message);
                        } else {
                            InputFile inputFile = new InputFile();
                            inputFile.setMedia(new ByteArrayInputStream(practiceTask.get().getTask()), "Practice №" + id);
                            SendDocument sendDocument = new SendDocument()
                                .setDocument(inputFile)
                                .setChatId(chatId);
                            states.put(chatId, State.Normal);
                            execute(sendDocument);
                        }
                    } catch (NumberFormatException exception) {
                        SendMessage message = new SendMessage()
                            .setChatId(chatId)
                            .setText("Я тебя не понял. Какой номер?");
                        execute(message);
                    }
                }
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    private void createKeyBoard(Long chatId) throws TelegramApiException {
        SendMessage message = new SendMessage()
            .setChatId(chatId)
            .setText("Hi!");
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add("Профиль");
        row.add("Задание");
        keyboard.add(row);
        keyboardMarkup.setKeyboard(keyboard);
        message.setReplyMarkup(keyboardMarkup);
        execute(message);
    }

    private void processMessage(Message message) throws TelegramApiException {
        switch (message.getText()) {
            case "Профиль":
                getProfile(message.getChatId(), message.getFrom());
                break;
            case "Задание":
                waitTaskNumber(message.getChatId());
                break;
        }
    }

    private void waitTaskNumber(Long chatId) throws TelegramApiException {
        SendMessage message = new SendMessage()
            .setChatId(chatId)
            .setText("Какой номер задания?");
        states.put(chatId, State.WaitingForTaskNumber);
        execute(message);
    }

    private void getProfile(Long chatId, User user) throws TelegramApiException {
        Optional<Student> students = studentsRepository.findById(String.valueOf(user.getId()));
        SendMessage message = new SendMessage()
            .setChatId(chatId);
        if (students.isEmpty()) {
            message = message.setText("Не найден.");
        } else {
            Student student = students.get();
            message = message.setText(String.format(PROFILE_INFO,
                student.getTelegramId(),
                student.getEmail(),
                student.getFirstName(),
                student.getLastName(),
                student.getGroup(),
                student.getSubGroup()));
        }
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
