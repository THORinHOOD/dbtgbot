package com.thorinhood.dbtg.bot;

import com.thorinhood.dbtg.bot.keyboard.Keyboards;
import org.apache.commons.io.IOUtils;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractBot extends TelegramLongPollingBot {

    private String token;
    private String name;
    private Map<Long, DialogStep> dialog;

    public AbstractBot(String name, String token, DefaultBotOptions options) {
        super(options);
        this.token = token;
        this.name = name;
        dialog = new ConcurrentHashMap<>();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.getMessage().isUserMessage()) {
            try {
                Long chatId = update.getMessage().getChatId();
                if (!dialog.containsKey(chatId)) {
                    processMessage(update.getMessage());
                } else {
                    DialogStep current = dialog.get(chatId);
                    DialogStep result = current.step(update);

                    switch(result.getStatus()) {
                        case End:
                            createDefaultKeyBoard(chatId, false);
                            dialog.remove(chatId);
                            break;
                        case Repeat:
                            dialog.put(chatId, current);
                            break;
                        case HasNext:
                            dialog.put(chatId, result);
                            break;
                    }
                }
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    protected void startDialog(Long chatId, DialogStep step) {
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
        message.setReplyMarkup(Keyboards.START.getKeyboard());
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

    protected Optional<byte[]> downloadFile(Document document) throws TelegramApiException {
        GetFile getFile = new GetFile().setFileId(document.getFileId());
        File file = execute(getFile);
        try {
            URL fileUrl = new URL(file.getFileUrl(getBotToken()));
            HttpURLConnection httpConn = (HttpURLConnection) fileUrl.openConnection();
            InputStream inputStream = httpConn.getInputStream();
            return Optional.of(IOUtils.toByteArray(inputStream));
        } catch(IOException ex) {
            return Optional.empty();
        }
    }

    @Override
    public String getBotUsername() {
        return name;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    protected abstract void processMessage(Message message) throws TelegramApiException;

}
