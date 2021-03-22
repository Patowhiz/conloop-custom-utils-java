package conloop;

import java.util.ArrayList;

/**
 *
 * @author PatoWhiz 27/09/2019 04:01 PM
 */
public class StringsUtil {

    public static boolean isNotEmpty(String input) {
        return isNotNullOrEmpty(input);
    }

    public static boolean isEmpty(String input) {
        return isNullOrEmpty(input);
    }

    public static boolean isNotNullOrEmpty(String input) {
        return !isNullOrEmpty(input);
    }//end method

    public static boolean isNullOrEmpty(String input) {
        if (input == null) {
            return true;
        } else {
            return (input.trim().isEmpty());
        }

    }//end method

    public static String formatToCommaNumber(double amount) {
        java.text.DecimalFormat formatter = (java.text.DecimalFormat) java.text.NumberFormat.getCurrencyInstance(java.util.Locale.US);
        java.text.DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        symbols.setCurrencySymbol(""); // Don't use null.
        formatter.setDecimalFormatSymbols(symbols);
        formatter.setMinimumFractionDigits(2);
        //formatter.setNegativePrefix("-"); // or "-"+symbol if that's what you need
        //formatter.setNegativeSuffix("");
        return formatter.format(amount); // 12.35
    }

    public static String formatToKenyaCurrency(double amount) {
        java.util.Locale locale = new java.util.Locale("en", "KE");
        java.text.NumberFormat formatter = java.text.NumberFormat.getCurrencyInstance(locale);
        return formatter.format(amount);
    }

    public static String removeAllCommas(String input) {
        if (input != null) {
            return input.replaceAll(",", "");
        } else {
            return null;
        }

    }

    //This is the method to eventually use
    public static boolean isNumeric(String input) {

        // If its not null or empty
        if (isNullOrEmpty(input)) {
            return false;
        } else {
            try {
                Double.parseDouble(input);
                return true;
            } catch (Exception ex) {
                return false;
            } // end try

            //return str != null && str.matches("[-+]?\\d*\\.?\\d+");  
            //return  input.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
        } // end if

    }//end method

    // This is the method to eventually use
    public static boolean isNotNumeric(String input) {
        return !isNumeric(input);
    }// end method

    public static boolean isString(Object objInput) {
        return objInput != null && objInput instanceof String;
    }

    /**
     * if both x and y are null, false will be returned
     *
     * @param x
     * @param y
     * @return
     */
    public static boolean isEqual(String x, String y) {
        //check for x and y nulls. 
        if (x == null) {
            return false;
        }
        if (y == null) {
            return false;
        }
        return x.equals(y);
    }

    /**
     * if both x and y are null, false will be returned
     *
     * @param x
     * @param y
     * @return
     */
    public static boolean isEqualIgnoreCase(String x, String y) {
        //check for x and y nulls. 
        if (x == null) {
            return false;
        }
        if (y == null) {
            return false;
        }
        return x.equalsIgnoreCase(y);
    }

    /**
     * Returns empty string if phone number is not valid else returns a
     * validated and correctly formatted phone number based on
     * toPhoneNumberStartingWith e.g +254712749472
     *
     * @param unFormattedPhoneNumber
     * @return correctly formatted phone number
     */
    public static String formatToValidPhoneNumberKenya(String unFormattedPhoneNumber) {
        return formatToValidPhoneNumberKenya(unFormattedPhoneNumber, "+254");
    }

    /**
     * Returns empty string if phone number is not valid else returns a
     * validated and correctly formatted phone number based on
     * toPhoneNumberStartingWith e.g +254712749472
     *
     * @param unFormattedPhoneNumber
     * @param toPhoneNumberStartingWith
     * @return correctly formatted phone number
     */
    public static String formatToValidPhoneNumberKenya(String unFormattedPhoneNumber, String toPhoneNumberStartingWith) {

        try {
            //remove all the spaces. Both Internal and external spaces
            String phoneNum = unFormattedPhoneNumber.replaceAll("\\s", "");
            if (isEqual(toPhoneNumberStartingWith, "+254") && phoneNum.startsWith("+254")) {
                if (phoneNum.length() == 13) {
                    java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("\\d{9}").matcher(phoneNum.substring(4));
                    return matcher.matches() ? phoneNum : "";
                } else {
                    return "";//if phone number length != 13 then return error
                }//end inner if

            } else {

                if (phoneNum.startsWith("0")) {
                    if (isEqual(toPhoneNumberStartingWith, "0")) {
                        return java.util.regex.Pattern.compile("\\d{9}").matcher(phoneNum.substring(1)).matches() ? phoneNum : "";
                    } else {
                        return formatToValidPhoneNumberKenya(toPhoneNumberStartingWith + phoneNum.substring(1), toPhoneNumberStartingWith);
                    }

                } else if (phoneNum.startsWith("7")) {
                    if (isEqual(toPhoneNumberStartingWith, "7")) {
                        return java.util.regex.Pattern.compile("\\d{9}").matcher(phoneNum).matches() ? phoneNum : "";
                    } else {
                        return formatToValidPhoneNumberKenya(toPhoneNumberStartingWith + phoneNum, toPhoneNumberStartingWith);
                    }
                } else if (phoneNum.startsWith("254")) {
                    if (isEqual(toPhoneNumberStartingWith, "254")) {
                        return java.util.regex.Pattern.compile("\\d{12}").matcher(phoneNum).matches() ? phoneNum : "";
                    } else {
                        return formatToValidPhoneNumberKenya(toPhoneNumberStartingWith + phoneNum, toPhoneNumberStartingWith);
                    }
                } else {
                    return "";
                }//end inner if

            }//end outer if     
        } catch (Exception ex) {
            return "";
        }//end try

    }//end method

    public static boolean isValidPhoneNumber(String strPhoneNo) {
        return !formatToValidPhoneNumberKenya(strPhoneNo).isEmpty();
    }

    public static boolean isNotValidPhoneNumber(String strPhoneNo) {
        return !isValidPhoneNumber(strPhoneNo);
    }

    /**
     * for returning shortened names
     *
     * @param longName
     * @return
     */
    public static String getShortenedName(String longName) {
        String shortenedName = longName;
        String[] shortened;
        try {
            shortened = longName.split("\\s");
            switch (shortened.length) {
                case 2:
                    shortenedName = shortened[0] + " " + shortened[1];
                    break;
                case 3:
                    if (shortened[1].isEmpty()) {
                        shortenedName = shortened[0] + " " + shortened[2];
                    } else {
                        shortenedName = shortened[0] + " " + shortened[1].substring(0, 1) + ". " + shortened[2];
                    }
                    break;
                default:
                    shortenedName = longName;
                    break;
            }
        } catch (Exception ex) {
            //put here for any unknown names that won't be split accordingly
        }

        return shortenedName;
    }

    /**
     *
     * @param lettersToSearchFor . String of What to search
     * @param wordContents . The contents to search in
     * @return
     */
    public static boolean containsLetters(String lettersToSearchFor, String wordContents) {
        int strLength;
        strLength = lettersToSearchFor.length();
        java.util.List<Character> lstCharsToSearch = new ArrayList<>();
        for (int i = 0; i < strLength; i++) {
            lstCharsToSearch.add(lettersToSearchFor.charAt(i));
        }//end inner for loop

        int numOfLettersFound = 0;
        char aChar;
        strLength = wordContents.length();
        for (int indexName = 0; indexName < strLength; indexName++) {

            aChar = wordContents.charAt(indexName);
            for (int indexToSearch = 0; indexToSearch < lstCharsToSearch.size(); indexToSearch++) {
                if (lstCharsToSearch.get(indexToSearch) == aChar) {
                    numOfLettersFound++;
                    lstCharsToSearch.remove(indexToSearch);
                    break;//breaks inner loop
                }
            }//end inner for loop
        }//end for loop

        return numOfLettersFound == lettersToSearchFor.length();
    }

}//end class
