package com.thorinhood.dbtg.bot.keyboard;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

public abstract class CustomKeyBoard {

    private ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

    public CustomKeyBoard() {
        init(keyboardMarkup);
    }

    public ReplyKeyboardMarkup getKeyboard() {
        return keyboardMarkup;
    }

    protected abstract void init(ReplyKeyboardMarkup keyboardMarkup);

}
