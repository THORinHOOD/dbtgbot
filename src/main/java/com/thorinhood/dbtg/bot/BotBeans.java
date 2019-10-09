package com.thorinhood.dbtg.bot;

import com.thorinhood.dbtg.repositories.PracticeTasksRepository;
import com.thorinhood.dbtg.repositories.SolutionsRepository;
import com.thorinhood.dbtg.repositories.StudentsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultBotOptions;

@Component
public class BotBeans {

    @Autowired
    private StudentsRepository studentsRepository;

    @Autowired
    private PracticeTasksRepository practiceTasksRepository;

    @Autowired
    private SolutionsRepository solutionsRepository;

    @Bean
    public Bot telegramBot(String token, DefaultBotOptions botOptions) {
        return new Bot(token, botOptions, studentsRepository, practiceTasksRepository, solutionsRepository);
    }

}
