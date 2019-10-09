package com.thorinhood.dbtg.bot;

import com.thorinhood.dbtg.repositories.PracticeTasksRepository;
import com.thorinhood.dbtg.repositories.SolutionsRepository;
import com.thorinhood.dbtg.repositories.StudentsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.ApiContext;

@Configuration
public class BotBeans {

    @Value("${telegram.bot.token}")
    private String token;

    @Value("${telegram.bot.proxy.host}")
    private String proxyHost;

    @Value("${telegram.bot.proxy.port}")
    private int proxyPort;

    @Autowired
    private StudentsRepository studentsRepository;

    @Autowired
    private PracticeTasksRepository practiceTasksRepository;

    @Autowired
    private SolutionsRepository solutionsRepository;

    @Bean
    public DefaultBotOptions proxyBotOptions() {
        DefaultBotOptions botOptions = ApiContext.getInstance(DefaultBotOptions.class);
       // botOptions.setProxyHost(proxyHost);
       // botOptions.setProxyPort(proxyPort);
       // botOptions.setProxyType(DefaultBotOptions.ProxyType.SOCKS5);
        return botOptions;
    }

    @Bean
    public Bot bot(DefaultBotOptions proxyBotOptions) {
        return new Bot(token, proxyBotOptions, studentsRepository, practiceTasksRepository, solutionsRepository);
    }

}
