package org.jgrapht.ext;

class CSVUtils
{

    private static final char CSV_QUOTE = '"';
    private static final char CSV_LF = '\n';
    private static final char CSV_CR = '\r';
    private static final String CSV_QUOTE_AS_STRING = String.valueOf(CSV_QUOTE);

    public static String escapeCSV(String input, char delimiter)
    {
        char[] specialChars = new char[] {
            delimiter,
            CSV_QUOTE,
            CSV_LF,
            CSV_CR };

        boolean containsSpecial = false;
        for (int i = 0; i < specialChars.length; i++) {
            if (input.contains(String.valueOf(specialChars[i]))) {
                containsSpecial = true;
                break;
            }
        }

        if (containsSpecial) {
            return CSV_QUOTE_AS_STRING + input.replaceAll(
                CSV_QUOTE_AS_STRING,
                CSV_QUOTE_AS_STRING + CSV_QUOTE_AS_STRING)
                + CSV_QUOTE_AS_STRING;
        }

        return input;
    }

    public static String unescapeCSV(String input, char delimiter)
    {
        char[] specialChars = new char[] {
            delimiter,
            CSV_QUOTE,
            CSV_LF,
            CSV_CR };

        if (input.charAt(0) != CSV_QUOTE
            || input.charAt(input.length() - 1) != CSV_QUOTE)
        {
            return input;
        }

        String noQuotes = input.subSequence(1, input.length() - 1).toString();

        boolean containsSpecial = false;
        for (int i = 0; i < specialChars.length; i++) {
            if (noQuotes.contains(String.valueOf(specialChars[i]))) {
                containsSpecial = true;
                break;
            }
        }

        if (containsSpecial) {
            return noQuotes.replaceAll(
                CSV_QUOTE_AS_STRING + CSV_QUOTE_AS_STRING,
                CSV_QUOTE_AS_STRING);
        }

        return input;
    }
}