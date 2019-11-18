package com.thorinhood.dbtg.bot;

import com.thorinhood.dbtg.repositories.TasksRepository;
import com.thorinhood.dbtg.repositories.SolutionsRepository;
import com.thorinhood.dbtg.repositories.StudentsRepository;
import com.thorinhood.dbtg.services.MarksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultBotOptions;

@Component
public class BotBeans {

    @Autowired
    private StudentsRepository studentsRepository;

    @Autowired
    private TasksRepository tasksRepository;

    @Autowired
    private SolutionsRepository solutionsRepository;

    @Autowired
    private MarksService marksService;

    @Bean
    public Bot telegramBot(String token, DefaultBotOptions botOptions) {
        return new Bot(token, botOptions, studentsRepository, tasksRepository, solutionsRepository, marksService);
    }

}
