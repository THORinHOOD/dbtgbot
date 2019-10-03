package com.thorinhood.dbtg.bot;

import com.thorinhood.dbtg.models.PracticeTask;
import com.thorinhood.dbtg.models.Student;
import com.thorinhood.dbtg.repositories.PracticeTasksRepository;
import com.thorinhood.dbtg.repositories.StudentsRepository;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Optional;

public class Bot extends AbstractBot {

    private static String PROFILE_INFO = "telegram id : %s\nemail : %s\nfirst name : %s\n" +
        "last name : %s\ngroup : %s\nsub group : %s";

    private StudentsRepository studentsRepository;
    private PracticeTasksRepository practiceTasksRepository;

    public Bot(String token, StudentsRepository studentsRepository, PracticeTasksRepository practiceTasksRepository) {
        super(token);
        this.studentsRepository = studentsRepository;
        this.practiceTasksRepository = practiceTasksRepository;
    }

    @Override
    protected void processMessage(Message message) throws TelegramApiException {
        switch (message.getText()) {
            case "/start":
                createDefaultKeyBoard(message.getChatId(), true);
                break;
            case "Профиль":
                getProfile(message.getChatId(), message.getFrom());
                break;
            case "Задания":
                waitTaskNumber(message.getChatId());
                break;
        }
    }

    private void waitTaskNumber(Long chatId) throws TelegramApiException {
        startDialog(chatId, this::getPdfOfTask);
        makeCurrentKeyBoard(chatId, "Какое задание?", true,
            practiceTasksRepository.getAllIds().stream().map(String::valueOf).toArray(String[]::new));
    }

    private boolean getPdfOfTask(Update update) throws TelegramApiException {
        Long chatId = update.getMessage().getChatId();
        Integer id = null;

        if (update.getMessage().getText().equals("Назад")) {
            createDefaultKeyBoard(chatId, false);
            return true;
        }

        try {
            id = Integer.valueOf(update.getMessage().getText().trim());
        } catch (NumberFormatException exception) {
            sendMessage(chatId, "Введи номер задания.");
            return false;
        }

        Optional<PracticeTask> practiceTask = practiceTasksRepository.findById(id);
        if (practiceTask.isEmpty()) {
            sendMessage(chatId, "Задание с таким номером не найдено. Попробуй снова.");
            return false;
        } else {
            sendPdf(chatId, "Practice №" + id, practiceTask.get().getTask());
            createDefaultKeyBoard(chatId, false);
            return true;
        }
    }

    private void getProfile(Long chatId, User user) throws TelegramApiException {
        Optional<Student> students = studentsRepository.findById(String.valueOf(user.getId()));
        String text = students.isEmpty() ? "Не найден." : String.format(PROFILE_INFO,
            students.get().getTelegramId(),
            students.get().getEmail(),
            students.get().getFirstName(),
            students.get().getLastName(),
            students.get().getGroup(),
            students.get().getSubGroup());
        sendMessage(chatId, text);
    }

}
