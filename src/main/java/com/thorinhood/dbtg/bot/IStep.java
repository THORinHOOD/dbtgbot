package com.thorinhood.dbtg.bot;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface IStep {
    DialogStep step(Update update, Object paramFromPrev) throws TelegramApiException;
}
