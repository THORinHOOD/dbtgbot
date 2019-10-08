package com.thorinhood.dbtg.bot.keyboard;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;

public abstract class AbstractCustomKeyBoard {

    private ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

    public AbstractCustomKeyBoard() {
        initTamplate();
    }

    public ReplyKeyboardMarkup getKeyboard() {
        return keyboardMarkup;
    }

    private void initTamplate() {
        List<KeyboardRow> keyboard = init();
        keyboardMarkup.setKeyboard(keyboard);
    }

    protected abstract List<KeyboardRow> init();

}
