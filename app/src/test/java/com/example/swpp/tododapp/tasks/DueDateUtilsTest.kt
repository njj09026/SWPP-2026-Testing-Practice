package com.example.swpp.tododapp.tasks

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class DueDateUtilsTest {
    private fun assertLabel(dueDate: String?, today: String, completed: Boolean, expected: String) {
        // Given the exact due date, comparison date, and completion state
        val date = dueDate?.let(LocalDate::parse)
        // When the label is calculated
        val actual = dueDateLabel(date, LocalDate.parse(today), completed)
        // Then it matches the specified label
        assertEquals(expected, actual)
    }

    @Test fun dueDateLabel_completedFuture_returnsClear() =
        assertLabel("2026-10-25", "2026-10-20", true, "CLEAR")

    @Test fun dueDateLabel_completedWithoutDate_returnsClear() =
        assertLabel(null, "2026-10-20", true, "CLEAR")

    @Test fun dueDateLabel_completedOverdue_returnsClear() =
        assertLabel("2026-10-15", "2026-10-20", true, "CLEAR")

    @Test fun dueDateLabel_activeWithoutDate_returnsEmpty() =
        assertLabel(null, "2026-10-20", false, "")

    @Test fun dueDateLabel_dueToday_returnsDDay() =
        assertLabel("2026-10-20", "2026-10-20", false, "D-Day")

    @Test fun dueDateLabel_fiveDaysFuture_returnsDMinus5() =
        assertLabel("2026-10-25", "2026-10-20", false, "D-5")

    @Test fun dueDateLabel_fiveDaysOverdue_returnsDPlus5() =
        assertLabel("2026-10-15", "2026-10-20", false, "D+5")

    @Test fun dueDateLabel_oneDayOverdue_returnsDPlus1() =
        assertLabel("2026-10-19", "2026-10-20", false, "D+1")

    @Test fun dueDateLabel_oneDayFuture_returnsDMinus1() =
        assertLabel("2026-10-21", "2026-10-20", false, "D-1")

    @Test fun dueDateLabel_completedToday_returnsClear() =
        assertLabel("2026-10-20", "2026-10-20", true, "CLEAR")

    @Test fun dueDateLabel_thirtyDaysFuture_returnsDMinus30() =
        assertLabel("2026-11-19", "2026-10-20", false, "D-30")

    @Test fun dueDateLabel_oneMonthFuture_returnsDMinus31() =
        assertLabel("2026-11-20", "2026-10-20", false, "D-31")

    @Test fun dueDateLabel_thirtyTwoDaysFuture_returnsDMinus32() =
        assertLabel("2026-11-21", "2026-10-20", false, "D-32")

    @Test fun dueDateLabel_twentyNineDaysOverdue_returnsDPlus29() =
        assertLabel("2026-09-21", "2026-10-20", false, "D+29")

    @Test fun dueDateLabel_oneMonthOverdue_returnsDPlus30() =
        assertLabel("2026-09-20", "2026-10-20", false, "D+30")

    @Test fun dueDateLabel_thirtyOneDaysOverdue_returnsDPlus31() =
        assertLabel("2026-09-19", "2026-10-20", false, "D+31")

    @Test fun dueDateLabel_acrossMonthEnd_returnsDMinus1() =
        assertLabel("2026-11-01", "2026-10-31", false, "D-1")

    @Test fun dueDateLabel_acrossYearEnd_returnsDMinus1() =
        assertLabel("2027-01-01", "2026-12-31", false, "D-1")

    @Test fun dueDateLabel_dayBeforeOneYear_returnsDMinus364() =
        assertLabel("2027-10-19", "2026-10-20", false, "D-364")

    @Test fun dueDateLabel_oneYearFuture_returnsDMinus365() =
        assertLabel("2027-10-20", "2026-10-20", false, "D-365")

    @Test fun dueDateLabel_dayAfterOneYear_returnsDMinus366() =
        assertLabel("2027-10-21", "2026-10-20", false, "D-366")

    @Test fun dueDateLabel_acrossLeapDay_returnsDMinus2() =
        assertLabel("2028-03-01", "2028-02-28", false, "D-2")

    @Test fun dueDateLabel_leapFebruary_returnsDMinus29() =
        assertLabel("2028-03-01", "2028-02-01", false, "D-29")

    @Test fun dueDateLabel_nonLeapFebruary_returnsDMinus28() =
        assertLabel("2027-03-01", "2027-02-01", false, "D-28")

    @Test fun dueDateLabel_multiMonthFuture_returnsDMinus100() =
        assertLabel("2027-01-28", "2026-10-20", false, "D-100")
}
