package conloop;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import javafx.util.StringConverter;

/**
 *
 * @author PatoWhiz 24/04/2018 02:37 AM
 */
public class CalendarDateUtil {

    public static String getMonthName(YearMonth yearMonth) {
        return getMonthName(yearMonth.getMonthValue());
    }

    public static String getMonthName(int monthValue) {
        switch (monthValue) {
            case 1:
                return "January";
            case 2:
                return "February";
            case 3:
                return "March";
            case 4:
                return "April";
            case 5:
                return "May";
            case 6:
                return "June";
            case 7:
                return "July";
            case 8:
                return "August";
            case 9:
                return "September";
            case 10:
                return "October";
            case 11:
                return "November";
            case 12:
                return "December";
            default:
                return "";
        }

    }

    public static java.sql.Date getTodayDateAsSql() {
        return getSqlDate(LocalDate.now());
    }

    public static java.time.LocalDate getTodayDate() {
        return LocalDate.now();
    }

    public static java.time.LocalDate getYesterdayDate() {
        return getTodayDate().minusDays(1);
    }

    public static java.time.LocalDate getCurrentWeekMondayDate() {
        return LocalDate.now().with(TemporalAdjusters.previous(DayOfWeek.MONDAY));
    }

    public static java.time.LocalDate getLastWeekMondayDate() {
        return getCurrentWeekMondayDate().minusWeeks(1);
    }

    public static java.time.LocalDate getCurrentWeekSaturdayDate() {
        return LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.SATURDAY));
    }

    public static java.time.LocalDate getLastWeekSaturdayDate() {
        return getCurrentWeekSaturdayDate().minusWeeks(1);
    }

    public static java.time.LocalDate getCurrentMonthStartDate() {
        return LocalDate.now().withDayOfMonth(1);
    }

    public static java.time.LocalDate getLastMonthStartDate() {
        return getCurrentMonthStartDate().minusMonths(1);
    }

    public static java.time.LocalDate getCurrentMonthEndDate() {
        return getMonthEndDate(LocalDate.now());
    }

    public static java.time.LocalDate getLastMonthEndDate() {
        return getCurrentMonthEndDate().minusMonths(1);
    }

    public static java.time.LocalDate getMonthStartDate(java.time.LocalDate date) {
        return date.withDayOfMonth(1);
    }

    public static java.time.LocalDate getMonthEndDate(java.time.LocalDate date) {
        return date.plusMonths(1).withDayOfMonth(1).minusDays(1);
    }

    public static java.time.LocalDate getCurrentYearStartDate() {
        return getYearStartDate(LocalDate.now());
    }

    public static java.time.LocalDate getLastYearStartDate() {
        return getCurrentYearStartDate().minusYears(1);
    }

    public static java.time.LocalDate getYearStartDate(java.time.LocalDate date) {
        return date.with(TemporalAdjusters.firstDayOfYear());
    }

    public static java.time.LocalDate getCurrentYearEndDate() {
        return LocalDate.now().with(TemporalAdjusters.lastDayOfYear());
    }

    public static java.time.LocalDate getLastYearEndDate() {
        return getCurrentYearEndDate().minusYears(1);
    }

    public static java.time.LocalDate getCurrentTermStartDate() {
        return getTermStartDate(getCurrentTerm());
    }

    public static java.time.LocalDate getTermStartDate(int termId) {
        switch (termId) {
            case 1:
                return getDate(getCurrentYear(), 1, 1);
            case 2:
                return getDate(getCurrentYear(), 5, 1);
            default:
                return getDate(getCurrentYear(), 9, 1);
        }
    }

    public static java.time.LocalDate getCurrentTermEndDate() {
        return getTermEndDate(getCurrentTerm());
    }

    public static java.time.LocalDate getTermEndDate(int termId) {
        switch (termId) {
            case 1:
                return getMonthEndDate(getDate(getCurrentYear(), 4, 1));
            case 2:
                return getMonthEndDate(getDate(getCurrentYear(), 8, 1));
            default:
                return getMonthEndDate(getDate(getCurrentYear(), 12, 1));
        }
    }

    public static int getCurrentDay() {
        return LocalDate.now().getDayOfMonth();
    }

    public static int getCurrentMonth() {
        return YearMonth.now().getMonthValue();
    }

    public static int getCurrentYear() {
        return YearMonth.now().getYear();
    }

    public static int getCurrentQuarter() {
        return getMonthQuarter(CalendarDateUtil.getCurrentMonth());
    }

    public static int getYear(java.sql.Date date) {
        return getYear(getDate(date));
    }

    public static int getMonthValue(java.sql.Date date) {
        return getMonthValue(getDate(date));
    }

    public static int getYear(java.time.LocalDate date) {
        return date.getYear();
    }

    public static int getMonthValue(java.time.LocalDate date) {
        return date.getMonthValue();
    }

    public static int getTerm(java.sql.Date date) {
        return getTerm(getDate(date));
    }

    public static int getTerm(java.time.LocalDate date) {
        return getTerm(date.getMonthValue());
    }

    public static int getTerm(int monthValue) {
        if (monthValue < 5) {
            return 1;
        } else if (monthValue < 9) {
            return 2;
        } else {
            //(monthValue <= 12
            return 3;
        }//end if
    }

    public static int getCurrentTerm() {
        return getTerm(getCurrentMonth());
    }

    public static int getMonthQuarter(int monthValue) {
        if (monthValue > 0 && monthValue <= 3) {
            return 1;//Quarter 1
        } else if (monthValue > 3 && monthValue <= 6) {
            return 2; //Quarter 2 
        } else if (monthValue > 6 && monthValue <= 9) {
            return 3;//Quarter 3
        } else if (monthValue > 9 && monthValue <= 12) {
            return 4;//Quarter 4
        } else {
            return 0;//Quarter 4
        }//end if
    }

    public static int getLengthOfMonth(int iYear, int iMonth) {
        return YearMonth.of(iYear, iMonth).lengthOfMonth();
    }

    public static List<YearMonth> getListofLastYearMonths(int iYear, int iMonth, int iMonthsNum, boolean bIncludePassedParameterYearMonth) {
        List<YearMonth> yrMonths = new ArrayList<>();

        YearMonth stop = YearMonth.of(iYear, iMonth);
        YearMonth start = YearMonth.of(iYear, iMonth).minusMonths(iMonthsNum);

        while (start.isBefore(stop)) {
            yrMonths.add(YearMonth.of(start.getYear(), start.getMonthValue()));
            start = start.plusMonths(1);
        }

        if (bIncludePassedParameterYearMonth) {
            yrMonths.add(YearMonth.of(iYear, iMonth));
        }

        return yrMonths;
    }

    public static List<YearMonth> getListofNextYearMonths(int iYear, int iMonth, int iMonthsNum) {
        List<YearMonth> yrMonths = new ArrayList<>();
        YearMonth start = YearMonth.of(iYear, iMonth);
        YearMonth stop = YearMonth.of(iYear, iMonth).plusMonths(iMonthsNum);

        while (start.isBefore(stop)) {
            yrMonths.add(YearMonth.of(start.getYear(), start.getMonthValue()));
            start = start.plusMonths(1);
        }

        //include the last year month also
        yrMonths.add(stop);

        return yrMonths;
    }

    public static List<Integer> getListofLastYears(int iYear, int iYearsNum, boolean bIncludePassedParameterYear) {
        List<Integer> yrMonths = new ArrayList<>();
        YearMonth stop = YearMonth.of(iYear, getCurrentMonth());
        YearMonth start = YearMonth.of(iYear, getCurrentMonth()).minusYears(iYearsNum);

        while (start.isBefore(stop)) {
            yrMonths.add(start.getYear());
            start = start.plusYears(1);
        }

        if (bIncludePassedParameterYear) {
            yrMonths.add(iYear);
        }

        return yrMonths;
    }

    /**
     * the startDate is inclusive and the endDate is exclusive
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static long getDaysBetween(java.time.LocalDate startDate, java.time.LocalDate endDate) {
        //return startDate.until(endDate, java.time.temporal.ChronoUnit.DAYS); //same. Left here as reference
        return java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate);
    }

    public static long getMonthsBetween(java.time.LocalDate startDate, java.time.LocalDate endDate) {
        //return startDate.until(endDate, java.time.temporal.ChronoUnit.MONTHS); //same. Left here as reference
        return java.time.temporal.ChronoUnit.MONTHS.between(startDate, endDate);
    }

    public static long getYearsBetween(java.time.LocalDate startDate, java.time.LocalDate endDate) {
        //return startDate.until(endDate, java.time.temporal.ChronoUnit.MONTHS); //same. Left here as reference
        return java.time.temporal.ChronoUnit.YEARS.between(startDate, endDate);
    }

    public static java.time.LocalDate getDate(int iYear, int iMonth, int iDay) {
        return LocalDate.of(iYear, iMonth, iDay);
    }

    /**
     * Obtains an instance of LocalDate from a text string such as 2018-09-30.
     * The string must represent a valid date and is parsed using
     * DateTimeFormatter.ISO_LOCAL_DATE.
     *
     * @param strDate
     * @return
     */
    public static java.time.LocalDate getDate(String strDate) {
        if (StringsUtil.isNullOrEmpty(strDate)) {
            return null;
        } else {
            return LocalDate.parse(strDate);
        }
    }

    public static java.time.LocalDate getDate(java.sql.Date dateValue) {
        return LocalDate.parse(dateValue.toString());
    }

    public static java.sql.Date getSqlDate(java.time.LocalDate dateValue) {
        return java.sql.Date.valueOf(dateValue);
    }

    /**
     * Obtains an instance of LocalDate from a text string such as 2018-09-30.
     * The string must represent a valid date and is parsed using
     * DateTimeFormatter.ISO_LOCAL_DATE.
     *
     * @param strDate
     * @return
     */
    public static java.sql.Date getSqlDate(String strDate) {
        if (StringsUtil.isNullOrEmpty(strDate)) {
            return null;
        } else {
            return getSqlDate(getDate(strDate));
        }
    }

    public static java.sql.Date getSqlDate(int iYearValue, int iMonthValue, int iDayValue) {
        return getSqlDate(getDate(iYearValue, iMonthValue, iDayValue));
    }

    public static boolean isWeekend(java.time.LocalDate dateValue) {
        return (dateValue.getDayOfWeek() == java.time.DayOfWeek.SATURDAY || dateValue.getDayOfWeek() == java.time.DayOfWeek.SUNDAY);
    }

    public static String format(java.time.LocalDate dateValue, java.time.format.FormatStyle formatStyle) {
        return dateValue.format(java.time.format.DateTimeFormatter.ofLocalizedDate(formatStyle));
    }

    public static String constructDateFilterSql(String columnName, int iYear, int iMonth, boolean bIncludePrevYearNMonth) {
        String strSqlFilter = null;
        java.sql.Date startDate;
        java.sql.Date endDate;

        if (iYear > 0 && iMonth > 0) {
            startDate = getSqlDate(iYear, iMonth, 1);
            endDate = getSqlDate(iYear, iMonth, getLengthOfMonth(iYear, iMonth));
            if (bIncludePrevYearNMonth) {
                strSqlFilter = columnName + " <= '" + endDate + "'";
            } else {
                strSqlFilter = columnName + " >= '" + startDate + "' AND " + columnName + " <= '" + endDate + "'";
            }

        } else if (iYear > 0) {
            startDate = getSqlDate(iYear, 1, 1);
            endDate = getSqlDate(iYear, 12, 31);
            if (bIncludePrevYearNMonth) {
                strSqlFilter = columnName + " <= '" + endDate + "'";
            } else {
                strSqlFilter = columnName + " >= '" + startDate + "' AND " + columnName + " <= '" + endDate + "'";
            }

        }
        return "(" + strSqlFilter + ")";
    }//end method

    public static String constructDateFilterSql(String columnName, int iYear, int iQuarter, int iMonth, boolean bIncludePrevYearNMonth) {
        String strSqlFilter = null;
        java.sql.Date startDate;
        java.sql.Date endDate;

        if (iYear > 0 && iMonth > 0) {
            startDate = getSqlDate(iYear, iMonth, 1);
            endDate = getSqlDate(iYear, iMonth, getLengthOfMonth(iYear, iMonth));
            if (bIncludePrevYearNMonth) {
                strSqlFilter = columnName + " <= '" + endDate + "'";
            } else {
                strSqlFilter = columnName + " >= '" + startDate + "' AND " + columnName + " <= '" + endDate + "'";
            }
        } else if (iYear > 0 && iQuarter > 0) {
            switch (iQuarter) {
                case 1:
                    startDate = getSqlDate(iYear, 1, 1);
                    endDate = getSqlDate(iYear, 3, getLengthOfMonth(iYear, 3));
                    break;
                case 2:
                    startDate = getSqlDate(iYear, 4, 1);
                    endDate = getSqlDate(iYear, 6, getLengthOfMonth(iYear, 6));
                    break;
                case 3:
                    startDate = getSqlDate(iYear, 7, 1);
                    endDate = getSqlDate(iYear, 9, getLengthOfMonth(iYear, 9));
                    break;
                default:
                    startDate = getSqlDate(iYear, 10, 1);
                    endDate = getSqlDate(iYear, 12, getLengthOfMonth(iYear, 12));
                    break;
            }

            if (bIncludePrevYearNMonth) {
                strSqlFilter = columnName + " <= '" + endDate + "'";
            } else {
                strSqlFilter = columnName + " >= '" + startDate + "' AND " + columnName + " <= '" + endDate + "'";
            }

        } else if (iYear > 0) {
            startDate = getSqlDate(iYear, 1, 1);
            endDate = getSqlDate(iYear, 12, 31);
            if (bIncludePrevYearNMonth) {
                strSqlFilter = columnName + " <= '" + endDate + "'";
            } else {
                strSqlFilter = columnName + " >= '" + startDate + "' AND " + columnName + " <= '" + endDate + "'";
            }

        }
        return "(" + strSqlFilter + ")";
    }//end method

    public static String constructDateFilterSql(String columnName, java.sql.Date startDate, java.sql.Date endDate) {
        String strSqlFilter;
        if (startDate == null) {
            strSqlFilter = columnName + " <= '" + endDate + "'";
        } else if (endDate == null) {
            strSqlFilter = columnName + " >= '" + startDate + "'";
        } else {
            strSqlFilter = columnName + " >= '" + startDate + "' AND " + columnName + " <= '" + endDate + "'";
        }

        return "(" + strSqlFilter + ")";
    }//end method

    /**
     * This function gets the string which is in the format dd/mm/yyyy or
     * yyyy-mm-dd and returns a java.util.Date from it
     *
     * @param theDateString
     * @return java.util.Date
     */
    public static java.util.Date getFormattedUtilDate(String theDateString) {
        java.text.DateFormat dtf;
        java.util.Date dt = null;

        try {
            //if format is yyyy-mm-dd then restructure it back to dd/mm/yyy
            String[] arrStr = theDateString.split("-");
            if (arrStr.length > 1 && arrStr.length == 3) {
                theDateString = arrStr[2] + "/" + arrStr[1] + "/" + arrStr[0];
            }
            dtf = java.text.DateFormat.getDateInstance(java.text.DateFormat.SHORT, java.util.Locale.UK);
            dt = dtf.parse(theDateString);

        } catch (java.text.ParseException ex) {
            System.err.println("Error in date format : " + ex.getMessage());
        }

        return dt;

    }

    /**
     * To be DEPRECATED This function gets a string in the form of dd/mm/yyyy
     * and returns a java.sql.date from it
     *
     * @param theDateString
     * @return java.sql.Date
     */
    public static java.sql.Date getFormattedSqlDate(String theDateString) {
        return new java.sql.Date(getFormattedUtilDate(theDateString).getTime());
    }

    /**
     * This function converts the format of dates for datetime picker into the
     * format dd/mm/yyyy
     *
     * @return StringConverter.
     */
    public static StringConverter configureDateFormats() {

        final String pattern = "dd/MM/YYYY"; //declared final so that it is not changed
        StringConverter converter = new StringConverter<java.time.LocalDate>() {
            java.time.format.DateTimeFormatter dateFormatter = java.time.format.DateTimeFormatter.ofPattern(pattern);

            @Override
            public String toString(java.time.LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }

            @Override
            public java.time.LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return java.time.LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }
        };

        return converter;
    }

    public static int getKenyaAcademicYear(java.sql.Date date) {
        return getKenyaAcademicYear(getDate(date));
    }

    public static int getKenyaAcademicYear(java.time.LocalDate date) {
        return getKenyaAcademicYear(date.getYear(), date.getMonthValue());
    }

    public static int getCurrentKenyaAcademicYear() {
        return getKenyaAcademicYear(getCurrentYear(), getCurrentMonth());
    }

    public static int getKenyaFinancialYearFromTerm(int yearValue, int termId) {
        if (yearValue == 2020) {
            if (termId > 1) {
                return 2021;
            }
        } else if (yearValue == 2021) {
            if (termId > 2) {
                return 2022;
            }
        }
        return yearValue;
    }

    public static int getKenyaAcademicYear(int yearValue, int monthValue) {

        if (yearValue == 2021) {
            //code added on 08/07/2021 by Patowhiz at Kivani
            //follows academic calendar for 2021 and 2022 cause by covid-19
            if (monthValue > 0 && monthValue <= 6) {
                yearValue = 2020;//term 1 & 2
            }
        } else if (yearValue == 2022) {
            if (monthValue > 0 && monthValue <= 4) {
                yearValue = 2021;//term 3
            }
        }

        return yearValue;
    }

    public static int getKenyaAcademicTerm(java.sql.Date date) {
        return getKenyaAcademicTerm(getDate(date));
    }

    public static int getKenyaAcademicTerm(java.time.LocalDate date) {
        return getKenyaAcademicTerm(date.getYear(), date.getMonthValue());
    }

    public static int getCurrentKenyaAcademicTerm() {
        return getKenyaAcademicTerm(getCurrentYear(), getCurrentMonth());
    }

    public static int getKenyaAcademicTerm(int yearValue, int monthValue) {
        int termId = getCurrentTerm();

        if (yearValue == 2021) {
            //code added on 08/07/2021 by Patowhiz at Kivani
            //follows academic calendar for 2021 and 2022 cause by covid-19
            if (monthValue > 0 && monthValue <= 4) {
                termId = 2;//term 2
            } else if (monthValue >= 5 && monthValue <= 6) {
                termId = 3; //term 3 
            } else if (monthValue >= 7 && monthValue <= 9) {
                termId = 1;//term 1
            } else if (monthValue >= 10 && monthValue <= 12) {
                termId = 2;//term 2
            }//end if
        } else if (yearValue == 2022) {
            if (monthValue > 0 && monthValue <= 4) {
                termId = 3;//term 3
            } else if (monthValue >= 5 && monthValue <= 6) {
                termId = 1; //term 1 
            } else if (monthValue >= 7 && monthValue <= 9) {
                termId = 2;//term 2
            } else if (monthValue >= 10 && monthValue <= 12) {
                termId = 3;//term 3
            }//end if
        }

        return termId;
    }//end method

}//end class
