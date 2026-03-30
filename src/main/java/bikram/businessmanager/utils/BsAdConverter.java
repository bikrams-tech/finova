package bikram.businessmanager.utils;

import java.time.LocalDate;
import java.time.Period;

public class BsAdConverter {

    private static final LocalDate BASE_AD = LocalDate.of(1943, 4, 14);
    private static final int BASE_BS_YEAR = 2000;

    public static LocalDate convertBsToAd(int year, int month, int day) {
        int totalDays = calculateDaysFromBase(year, month, day);
        return BASE_AD.plusDays(totalDays);
    }

    private static int calculateDaysFromBase(int year, int month, int day) {
        int days = 0;

        for (int y = BASE_BS_YEAR; y < year; y++) {
            days += getTotalDaysInYear(y);
        }

        for (int m = 1; m < month; m++) {
            days += getDaysInMonth(year, m);
        }

        days += (day - 1);
        return days;
    }

    private static int getTotalDaysInYear(int year) {
        int total = 0;
        for (int i = 1; i <= 12; i++) {
            total += getDaysInMonth(year, i);
        }
        return total;
    }

    private static int getDaysInMonth(int year, int month) {
        // Load from predefined array/table
        Period BsCalendarData = null;
        return BsCalendarData.getDays();// heare is mistake
    }
}