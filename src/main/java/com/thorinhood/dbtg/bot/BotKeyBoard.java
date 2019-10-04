package com.thorinhood.dbtg.bot;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public enum BotKeyBoard {
    START(BotKeyBoard::initStart);

    public static final String BACK = "Назад";
    public static final String START_PROFILE = "Профиль";
    public static final String START_TASKS = "Условия заданий";
    public static final String START_UPLOAD_SOLUTION = "Сдать задание";

    private ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

    BotKeyBoard(Consumer<ReplyKeyboardMarkup> init) {
        init.accept(keyboardMarkup);
    }

    public ReplyKeyboardMarkup getKeyboard() {
        return keyboardMarkup;
    }

    private static void initStart(ReplyKeyboardMarkup keyboardMarkup) {
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add(START_PROFILE);
        row.add(START_TASKS);
        keyboard.add(row);
        row = new KeyboardRow();
        row.add(START_UPLOAD_SOLUTION);
        keyboard.add(row);
        keyboardMarkup.setKeyboard(keyboard);
    }

}
