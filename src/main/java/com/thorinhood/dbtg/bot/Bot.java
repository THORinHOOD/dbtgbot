package com.thorinhood.dbtg.bot;

import com.thorinhood.dbtg.bot.keyboard.Keyboards;
import com.thorinhood.dbtg.bot.keyboard.StartKeyBoard;
import com.thorinhood.dbtg.models.Solution;
import com.thorinhood.dbtg.models.Student;
import com.thorinhood.dbtg.models.Task;
import com.thorinhood.dbtg.repositories.SolutionsRepository;
import com.thorinhood.dbtg.repositories.StudentsRepository;
import com.thorinhood.dbtg.repositories.TasksRepository;
import com.thorinhood.dbtg.services.MarksService;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Date;
import java.util.Optional;

public class Bot extends AbstractBot {

    private static String PROFILE_INFO = "telegram id : %s\nemail : %s\nимя : %s\n" +
            "фамилия : %s\nгруппа : %s\nподгруппа : %s";

    private StudentsRepository studentsRepository;
    private TasksRepository tasksRepository;
    private SolutionsRepository solutionsRepository;
    private MarksService marksService;

    public Bot(String token, DefaultBotOptions options, StudentsRepository studentsRepository,
               TasksRepository tasksRepository,
               SolutionsRepository solutionsRepository,
               MarksService marksService) {
        super("database_course_bot", token, options);
        this.studentsRepository = studentsRepository;
        this.tasksRepository = tasksRepository;
        this.solutionsRepository = solutionsRepository;
        this.marksService = marksService;
    }

    @Override
    protected void processMessage(Message message) throws TelegramApiException {
        if (message == null) {
            return;
        }

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
            case StartKeyBoard.MARKS:
                getMarks(message.getChatId(), message.getFrom());
                break;
        }
    }

    private void waitTaskNumberToUpload(Long chatId) throws TelegramApiException {
        startDialog(chatId, DialogStep.NEXT(null, this::getSolutionId));

        makeCurrentKeyBoard(chatId, "Какое задание?", true,
                tasksRepository.findAll().stream()
                        .map(task -> task.getId() + ". " + task.getTitle())
                        .toArray(String[]::new));
    }

    private void waitTaskNumber(Long chatId) throws TelegramApiException {
        startDialog(chatId, DialogStep.NEXT(null, this::getPdfOfTask));

        makeCurrentKeyBoard(chatId, "Какое задание?", true,
                tasksRepository.findAll().stream()
                        .map(task -> task.getId() + ". " + task.getTitle())
                        .toArray(String[]::new));
    }

    private DialogStep uploadSolutionFile(Update update, Object param) throws TelegramApiException {
        Long chatId = update.getMessage().getChatId();
        Long taskId = (Long) param;
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
                Long studentId = Long.valueOf(update.getMessage().getFrom().getId());
                Optional<Solution> solution = solutionsRepository.studentAndTask(studentId, taskId);
                if (solution.isPresent()) {
                    Solution solutionToUpdate = solution.get();
                    solutionToUpdate.setSolution(file.get());
                    solutionToUpdate.setDateOfCompletion(currentDate);
                    solutionsRepository.save(solutionToUpdate);
                } else {
                    solutionsRepository.save(Solution.builder()
                            .dateOfCompletion(currentDate)
                            .solution(file.get())
                            .solutionPK(new Solution.SolutionPK(studentId, taskId))
                            .build());
                }
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
        Long id;

        if (!update.getMessage().hasDocument() && update.getMessage().getText().equals(Keyboards.BACK)) {
            return DialogStep.END();
        }

        try {
            String message = update.getMessage().getText().trim();
            id = Long.valueOf(message.substring(0, message.indexOf(".")));
        } catch (NumberFormatException exception) {
            sendMessage(chatId, "Введи номер задания.");
            return DialogStep.REPEAT();
        }

        Optional<Task> practiceTask = tasksRepository.findById(id);
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
        Long id;

        if (update.getMessage().hasText() && update.getMessage().getText().equals(Keyboards.BACK)) {
            return DialogStep.END();
        }

        try {
            String message = update.getMessage().getText().trim();
            id = Long.valueOf(message.substring(0, message.indexOf(".")));
        } catch (NumberFormatException exception) {
            sendMessage(chatId, "Введи номер задания.");
            return DialogStep.REPEAT();
        }

        Optional<Task> practiceTask = tasksRepository.findById(id);
        if (practiceTask.isEmpty()) {
            sendMessage(chatId, "Задание с таким номером не найдено. Попробуй снова.");
            return DialogStep.REPEAT();
        } else {
            sendPdf(chatId, "Practice №" + id, practiceTask.get().getTask());
            return DialogStep.REPEAT();
        }
    }

    private void getMarks(Long chatId, User user) throws TelegramApiException {
        Long studentId = Long.valueOf(user.getId());
        String result = marksService.getStudentMarks(studentId).stream()
                .map(markInfo -> markInfo.getTask().getTitle() + " : " + markInfo.getSolution().getMark())
                .reduce("Оценки : \n", (s1, s2) -> s1 + "\n" + s2);
        sendMessage(chatId, result);
    }

    private void getProfile(Long chatId, User user) throws TelegramApiException {
        Long studentId = Long.valueOf(user.getId());
        Optional<Student> students = studentsRepository.findById(studentId);
        String text = students.isEmpty() ? String.format("Не найден. Твой id : %d", studentId) :
                String.format(PROFILE_INFO,
                    students.get().getTelegramId(),
                    students.get().getEmail(),
                    students.get().getFirstName(),
                    students.get().getLastName(),
                    students.get().getGroup(),
                    students.get().getSubGroup());
        sendMessage(chatId, text);
    }

}
