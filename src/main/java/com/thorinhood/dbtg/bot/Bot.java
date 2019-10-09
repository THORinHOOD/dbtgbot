package com.thorinhood.dbtg.bot;

import com.thorinhood.dbtg.bot.keyboard.Keyboards;
import com.thorinhood.dbtg.bot.keyboard.StartKeyBoard;
import com.thorinhood.dbtg.models.PracticeTask;
import com.thorinhood.dbtg.models.Solution;
import com.thorinhood.dbtg.models.Student;
import com.thorinhood.dbtg.repositories.PracticeTasksRepository;
import com.thorinhood.dbtg.repositories.SolutionsRepository;
import com.thorinhood.dbtg.repositories.StudentsRepository;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Date;
import java.util.Optional;

public class Bot extends AbstractBot {

    private static String PROFILE_INFO = "telegram id : %s\nemail : %s\nfirst name : %s\n" +
        "last name : %s\ngroup : %s\nsub group : %s";

    private StudentsRepository studentsRepository;
    private PracticeTasksRepository practiceTasksRepository;
    private SolutionsRepository solutionsRepository;

    public Bot(String token, DefaultBotOptions options, StudentsRepository studentsRepository,
               PracticeTasksRepository practiceTasksRepository,
               SolutionsRepository solutionsRepository) {
        super("database_course_bot", token, options);
        this.studentsRepository = studentsRepository;
        this.practiceTasksRepository = practiceTasksRepository;
        this.solutionsRepository = solutionsRepository;
    }

    @Override
    protected void processMessage(Message message) throws TelegramApiException {
        switch (message.getText()) {
            case "/start":
                createDefaultKeyBoard(message.getChatId(), true);
                break;
            case StartKeyBoard.PROFILE:
                getProfile(message.getChatId(), message.getFrom());
                break;
            case StartKeyBoard.TASKS:
                waitTaskNumber(message.getChatId());
                break;
            case StartKeyBoard.UPLOAD_SOLUTION:
                waitTaskNumberToUpload(message.getChatId());
                break;
        }
    }

    private void waitTaskNumberToUpload(Long chatId) throws TelegramApiException {
        startDialog(chatId, DialogStep.NEXT(null, this::getSolutionId));
        makeCurrentKeyBoard(chatId, "Какое задание?", true,
            practiceTasksRepository.getAllIds().stream().map(String::valueOf).toArray(String[]::new));
    }

    private void waitTaskNumber(Long chatId) throws TelegramApiException {
        startDialog(chatId, DialogStep.NEXT(null, this::getPdfOfTask));
        makeCurrentKeyBoard(chatId, "Какое задание?", true,
            practiceTasksRepository.getAllIds().stream().map(String::valueOf).toArray(String[]::new));
    }

    private DialogStep uploadSolutionFile(Update update, Object param) throws TelegramApiException {
        Long chatId = update.getMessage().getChatId();
        Integer taskId = (Integer) param;
        Date currentDate = new Date();

        if (update.getMessage().hasText() && update.getMessage().getText().equals(Keyboards.BACK)) {
            return DialogStep.END();
        }

        if (!update.getMessage().hasDocument()) {
            sendMessage(chatId, "Пришли файл в формате pdf.");
            return DialogStep.REPEAT();
        }

        Optional<byte[]> file = downloadFile(update.getMessage().getDocument());

        if (file.isEmpty()) {
            sendMessage(chatId, "Не удалось загрузить решение. Попробуйте снова.");
            return DialogStep.REPEAT();
        } else {
            try {
                solutionsRepository.save(Solution.builder()
                    .dateOfCompletion(currentDate)
                    .solution(file.get())
                    .student(Long.valueOf(update.getMessage().getFrom().getId()))
                    .task(taskId)
                    .build());
                sendMessage(chatId, "Решение успешно загружено.");
                return DialogStep.END();
            } catch (Exception ex) {
                sendMessage(chatId, "Не удалось загрузить решение. Попробуйте снова.");
                return DialogStep.REPEAT();
            }
        }
    }

    private DialogStep getSolutionId(Update update, Object param) throws TelegramApiException {
        Long chatId = update.getMessage().getChatId();
        Integer id;

        if (update.getMessage().hasDocument() && update.getMessage().getText().equals(Keyboards.BACK)) {
            return DialogStep.END();
        }

        try {
            id = Integer.valueOf(update.getMessage().getText().trim());
        } catch (NumberFormatException exception) {
            sendMessage(chatId, "Введи номер задания.");
            return DialogStep.REPEAT();
        }

        Optional<PracticeTask> practiceTask = practiceTasksRepository.findById(id);
        if (practiceTask.isEmpty()) {
            sendMessage(chatId, "Задание с таким номером не найдено. Попробуй снова.");
            return DialogStep.REPEAT();
        } else {
            makeCurrentKeyBoard(chatId, "Ок. Пришли файл в формате pdf.", true);
            return DialogStep.NEXT(id, this::uploadSolutionFile);
        }
    }

    private DialogStep getPdfOfTask(Update update, Object param) throws TelegramApiException {
        Long chatId = update.getMessage().getChatId();
        Integer id;

        if (update.getMessage().hasText() && update.getMessage().getText().equals(Keyboards.BACK)) {
            return DialogStep.END();
        }

        try {
            id = Integer.valueOf(update.getMessage().getText().trim());
        } catch (NumberFormatException exception) {
            sendMessage(chatId, "Введи номер задания.");
            return DialogStep.REPEAT();
        }

        Optional<PracticeTask> practiceTask = practiceTasksRepository.findById(id);
        if (practiceTask.isEmpty()) {
            sendMessage(chatId, "Задание с таким номером не найдено. Попробуй снова.");
            return DialogStep.REPEAT();
        } else {
            sendPdf(chatId, "Practice №" + id, practiceTask.get().getTask());
            return DialogStep.REPEAT();
        }
    }

    private void getProfile(Long chatId, User user) throws TelegramApiException {
        Optional<Student> students = studentsRepository.findById(Long.valueOf(user.getId()));
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
