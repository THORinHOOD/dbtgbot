package com.thorinhood.dbtg.bot;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class DialogStep {

    public enum Status {
        HasNext, End, Repeat;
    }

    private Object prevResult;
    private IStep step;
    private Status status;

    public static DialogStep END() {
        DialogStep dialogStep = new DialogStep();
        dialogStep.status = Status.End;
        return dialogStep;
    }

    public static DialogStep REPEAT() {
        DialogStep dialogStep = new DialogStep();
        dialogStep.status = Status.Repeat;
        return dialogStep;
    }

    public static DialogStep NEXT(Object prevResult, IStep step) {
        DialogStep dialogStep = new DialogStep();
        dialogStep.status = Status.HasNext;
        dialogStep.prevResult = prevResult;
        dialogStep.step = step;
        return dialogStep;
    }

    private DialogStep() {
    }

    public DialogStep step(Update update) throws TelegramApiException {
        return step.step(update, prevResult);
    }

    public Status getStatus() {
        return status;
    }

}
