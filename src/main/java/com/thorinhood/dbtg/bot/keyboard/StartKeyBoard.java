package com.thorinhood.dbtg.bot.keyboard;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class StartKeyBoard extends AbstractCustomKeyBoard {

    public static final String PROFILE = "Профиль";
    public static final String TASKS = "Условия заданий";
    public static final String UPLOAD_SOLUTION = "Сдать задание";
    public static final String MARKS = "Оценки";

    @Override
    protected List<KeyboardRow> init() {
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add(PROFILE);
        row.add(TASKS);
        keyboard.add(row);
        row = new KeyboardRow();
        row.add(UPLOAD_SOLUTION);
        row.add(MARKS);
        keyboard.add(row);
        return keyboard;
    }

}
