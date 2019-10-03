package com.thorinhood.dbtg.bot;

import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface DialogStep<T> {

    /**
     *
     * @param parameter
     * @return true - если конец диалога, false - иначе
     * @throws TelegramApiException
     */
    boolean step(T parameter) throws TelegramApiException;

}
