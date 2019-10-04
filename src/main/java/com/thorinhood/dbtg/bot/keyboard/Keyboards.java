package com.thorinhood.dbtg.bot.keyboard;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public enum Keyboards {
    START(new StartKeyBoard());

    public static final String BACK = "Назад";

    private CustomKeyBoard customKeyBoard;

    Keyboards(CustomKeyBoard customKeyBoard) {
        this.customKeyBoard = customKeyBoard;
    }

    public ReplyKeyboardMarkup getKeyboard() {
        return customKeyBoard.getKeyboard();
    }

}
