package com.thorinhood.dbtg.bot.keyboard;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

public enum Keyboards {
    START(new StartKeyBoard());

    public static final String BACK = "Назад";

    private AbstractCustomKeyBoard abstractCustomKeyBoard;

    Keyboards(AbstractCustomKeyBoard abstractCustomKeyBoard) {
        this.abstractCustomKeyBoard = abstractCustomKeyBoard;
    }

    public ReplyKeyboardMarkup getKeyboard() {
        return abstractCustomKeyBoard.getKeyboard();
    }

}
