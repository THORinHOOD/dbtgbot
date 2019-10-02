package com.thorinhood.dbtg.bot;

import com.thorinhood.dbtg.models.Student;
import com.thorinhood.dbtg.repositories.StudentsRepository;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Bot extends TelegramLongPollingBot {

    private static String PROFILE_INFO = "telegram id : %s\nemail : %s\nfirst name : %s\n" +
        "last name : %s\ngroup : %s\nsub group : %s";

    private String token;
    private StudentsRepository studentsRepository;

    public Bot(String token, StudentsRepository studentsRepository) {
        this.token = token;
        this.studentsRepository = studentsRepository;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText() && update.getMessage().isUserMessage()) {
            try {
                if (update.getMessage().getText().equals("/start")) {
                    createKeyBoard(update.getMessage().getChatId());
                } else {
                    processMessage(update.getMessage());
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
        }
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
