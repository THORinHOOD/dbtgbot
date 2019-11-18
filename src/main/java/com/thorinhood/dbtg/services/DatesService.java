package com.thorinhood.dbtg.services;

import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.function.BiFunction;

@Service
public class DatesService {

    public boolean eq(Date origin, Date second) {
        return template(origin, second, (org, snd) -> org.compareTo(snd) == 0);
    }

    public boolean lessOrEqOnlyDates(Date origin, Date second) {
        return lessOrEq(getZeroTimeDate(origin), getZeroTimeDate(second));
    }

    public boolean greaterOrEqOnlyDates(Date origin, Date second) {
        return greaterOrEq(getZeroTimeDate(origin), getZeroTimeDate(second));
    }

    public boolean lessOrEq(Date origin, Date second) {
        return template(origin, second, (org, snd) -> org.compareTo(snd) >= 0);
    }

    public boolean greaterOrEq(Date origin, Date second) {
        return template(origin, second, (org, snd) -> org.compareTo(snd) <= 0);
    }

    private Date getZeroTimeDate(Date date) {
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        date = calendar.getTime();
        return date;
    }

    public Date parseDate(String date, String pattern) {
        if (date == null) {
            return null;
        }
        try {
            return new SimpleDateFormat(pattern).parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    private boolean template(Date origin, Date second, BiFunction<Date, Date, Boolean> cmp) {
        if (origin == null) {
            return false;
        }
        if (second == null) {
            return true;
        }
        return cmp.apply(origin, second);
    }

}
