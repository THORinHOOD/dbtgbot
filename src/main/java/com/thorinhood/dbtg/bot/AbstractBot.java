package com.thorinhood.dbtg.bot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractBot extends TelegramLongPollingBot {

    private String token;
    private Map<Long, DialogStep<Update>> dialog;

    public AbstractBot(String token) {
        this.token = token;
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

    protected void startDialog(Long chatId, DialogStep<Update> step) {
        if (dialog.containsKey(chatId)) {
            throw new RuntimeException("Dialog already exists!!!");
        }
        dialog.put(chatId, step);
    }

    protected void sendPdf(Long chatId, String title, byte[] pdf) throws TelegramApiException {
        InputFile inputFile = new InputFile();
        inputFile.setMedia(new ByteArrayInputStream(pdf), title);
        SendDocument sendDocument = new SendDocument()
            .setDocument(inputFile)
            .setChatId(chatId);
        execute(sendDocument);
    }

    protected void sendMessage(Long chatId, String text) throws TelegramApiException {
        SendMessage message = new SendMessage()
            .setChatId(chatId)
            .setText(text);
        execute(message);
    }

    protected void createDefaultKeyBoard(Long chatId, boolean greeting) throws TelegramApiException {
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

    protected void makeCurrentKeyBoard(Long chatId, String text, boolean backBtn, String... options) throws TelegramApiException {
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

    protected abstract void processMessage(Message message) throws TelegramApiException;

}
