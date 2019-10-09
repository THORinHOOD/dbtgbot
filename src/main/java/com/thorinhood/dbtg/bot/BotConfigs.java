package com.thorinhood.dbtg.bot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.ApiContext;

@Configuration
public class BotConfigs {

    @Value("${telegram.bot.token}")
    private String token;

    @Value("${telegram.bot.proxy.host}")
    private String proxyHost;

    @Value("${telegram.bot.proxy.port}")
    private int proxyPort;

    @Bean
    public DefaultBotOptions proxyBotOptions() {
        DefaultBotOptions botOptions = ApiContext.getInstance(DefaultBotOptions.class);
        botOptions.setMaxThreads(7);
       // botOptions.setProxyHost(proxyHost);
       // botOptions.setProxyPort(proxyPort);
       // botOptions.setProxyType(DefaultBotOptions.ProxyType.SOCKS5);
        return botOptions;
    }

    @Bean
    public String token() {
        return token;
    }

}
