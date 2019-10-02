package com.thorinhood.dbtg.bot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import javax.annotation.PostConstruct;

@Component
public class TelegramService {

    @Autowired
    private Bot bot;

    private TelegramBotsApi telegramBotsApi;

    public TelegramService() {
        ApiContextInitializer.init();
        telegramBotsApi = new TelegramBotsApi();
    }

    @PostConstruct
    public void postContruct() throws TelegramApiRequestException {
        telegramBotsApi.registerBot(bot);
    }

}
