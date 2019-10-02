package com.thorinhood.dbtg.bot;

import com.thorinhood.dbtg.repositories.StudentsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BotBeans {

    @Value("${telegram.bot.token}")
    private String token;

    @Autowired
    private StudentsRepository studentsRepository;

    @Bean
    public Bot bot() {
        return new Bot(token, studentsRepository);
    }

}
