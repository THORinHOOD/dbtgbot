package com.thorinhood.dbtg.bot;

import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface DialogStep<T> {

    boolean step(T parameter) throws TelegramApiException;

}
