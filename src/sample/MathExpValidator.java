package sample;

import java.text.ParseException;

public class MathExpValidator {

    // Searches an expression for various cases of illegal math syntax, such as consecutive operators:
    public static void validateExp(String expression) throws ParseException {

        if (expression.length() == 0)
            throw new ParseException(expression, 0);

        validateDotCount(expression);

        validateFact(expression);

        String firstSymbol = String.valueOf(expression.charAt(0));

        if (firstSymbol.matches("[+*/^!.]"))
            throw new ParseException(expression, 0);

        String lastSymbol = String.valueOf(expression.charAt(expression.length() - 1));

        if (lastSymbol.matches("[+\\-*/^.]"))
            throw new ParseException(expression, expression.length());

        for (int i = 1; i < expression.length(); i++) {

            String currentSymbol = String.valueOf(expression.charAt(i));
            String prevSymbol = String.valueOf(expression.charAt(i - 1));

            if ((currentSymbol.matches("[+*/^!.]") && prevSymbol.matches("[+*/^.]")) ||
                    (currentSymbol.matches("-") && prevSymbol.matches("[+*/!]")) ||
                    (currentSymbol.matches("[+*/^!]") && prevSymbol.matches("\\(")) ||
                    (currentSymbol.matches("\\)") && prevSymbol.matches("[+\\-*/^!.]"))) {

                throw new ParseException(expression, i);
            }
        }
    }

    // Checks if any numbers in an expression has multiple dots:
    private static void validateDotCount(String expression) throws ParseException {
        int dotCount = 0;

        for (int i = 0; i < expression.length(); i++) {

            String currentSymbol = String.valueOf(expression.charAt(i));

            if (currentSymbol.matches("[\\d.]")) {
                if (currentSymbol.matches("\\."))
                    dotCount++;

            } else {
                dotCount = 0;
            }

            if (dotCount > 1)
                throw new ParseException(expression, i);
        }
    }

    // Checks that all factorials are positive integers:
    private static void validateFact(String expression) throws ParseException {
        double curNum = 0;
        StringBuilder numStrBuilder = new StringBuilder();

        for (int i = 0; i < expression.length(); i++) {
            String currentSymbol = String.valueOf(expression.charAt(i));

            if (currentSymbol.matches("[\\d.]")) {
                numStrBuilder.append(currentSymbol);
                curNum = Double.parseDouble(numStrBuilder.toString());

            } else if (currentSymbol.matches("!")) {
                if (curNum < 0 || !isInt(curNum)) {
                    throw new ParseException(expression, i);
                }
            }
        }
    }

    private static boolean isInt(double n) {
        return (int)(n) - n == 0;
    }
}
