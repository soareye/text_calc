package sample;

import java.text.ParseException;
import java.util.Arrays;

public class MathExpParser {

    // Calculates a mathematical expression in the form of a string:
    public static double calculate(String expression) throws ParseException {
        // Remove whitespace:
        expression = Arrays.stream(expression.split(""))
                .filter(x->x.matches("\\S"))
                .reduce("", (x, y) -> x+y);

        return parse(expression, 0, false);
    }

    /* Parses a mathematical expression in the form of a string
    * from a given index, changing behavior if it's parsing the
    * argument of a function: */
    private static double parse(String expression, int from, boolean isArg) throws ParseException {

        MathExpValidator.validateExp(expression);

        double result = 0;

        /* "product" is the result of recent multiplications and divisions
        * because we need to finish multiplying and dividing before adding
        * it to result: */
        double product = 0;

        // "curNum" is the most recent number we've read:
        double curNum = 0;

        // "prevOp" is the latest normal binary operator (+, -, *, /):
        String prevOp = "";

        /* Because we read numbers one character at a time, we need a StringBuilder to
        * build the number as we go: */
        StringBuilder numStrBuilder = new StringBuilder();

        int i = from;
        while (i < expression.length()) {

            String currentSymbol = String.valueOf(expression.charAt(i));
            String currentString = getText(expression, i);

            if (currentSymbol.matches("[\\d.]")) {
                numStrBuilder.append(currentSymbol);
                curNum = Double.parseDouble(numStrBuilder.toString());

            } else if (currentSymbol.matches("[*/+\\-]")) {

                product = binaryOps(prevOp, product, curNum);

                if (isArg && !(currentSymbol.matches("-") && i == from))
                    return result + product;

                numStrBuilder = new StringBuilder();
                prevOp = currentSymbol;

                // If the current symbol is + or -, we no longer need any memory
                // of previous products and we can add product to result:
                if (currentSymbol.matches("[+\\-]")) {
                    result += product;
                    product = 0;
                }

            } else if (currentSymbol.matches("\\^")) {
                double exponent = parse(expression, i + 1, true);
                curNum = Math.pow(curNum, exponent);
                i = findIndexAfterArg(expression, i + 1);

            } else if (currentSymbol.matches("!")) {
                curNum = factorial(curNum);

            } else if (currentSymbol.matches("\\(")) {
                curNum = parse(expression, i + 1, false);
                i = findIndexAfterParentheses(expression, i + 1);

            } else if (currentSymbol.matches("\\)")) {
                // If it's the end of a parenthesized expression, perform the
                // last binary operation on product and curNum, add it to the
                // result and return:
                product = binaryOps(prevOp, product, curNum);
                return result + product;

            } else if (matchesUnaryOps(currentString)) {
                i += currentString.length();
                double arg = parse(expression, i, true);
                curNum = additionalUnaryOps(currentString, arg);
                i = findIndexAfterArg(expression, i);

            } else if (matchesNumSymbol(currentString)) {
                curNum = numSyms(currentString);
                i += currentString.length() - 1;

            } else {
                throw new ParseException(expression, i);
            }

            i++;
        }

        // Finish up by combining product and curNum, adding it to result and returning:
        product = binaryOps(prevOp, product, curNum);
        return result + product;
    }

    private static double binaryOps(String op, double firstArg, double secArg) {
        if (op.matches("-")) {
            return firstArg - secArg;

        } else if (op.matches("\\*")) {
            return firstArg * secArg;

        } else if (op.matches("/")) {
            return firstArg / secArg;

        } else {
            return firstArg + secArg;
        }
    }

    private static boolean matchesUnaryOps(String curOp) {
        String opNames[] = {"sin", "cos", "tan", "asin", "acos", "atan", "sqrt", "log"};
        return Arrays.stream(opNames).anyMatch(str->str.matches(curOp));
    }

    private static boolean matchesNumSymbol(String numSymbol) {
        String numSymbols[] = {"e", "pi"};
        return Arrays.stream(numSymbols).anyMatch(str->str.matches(numSymbol));
    }

    /* Gets a string of letters, starting at the current index if there are any.
    * This is used to get operators, like "cos", or values with names, like "pi", in
    * an expression. */
    private static String getText(String expression, int index) {
        StringBuilder stringBuilder = new StringBuilder();

        while (index < expression.length() &&
                String.valueOf(expression.charAt(index)).matches("[a-z]")) {
            stringBuilder.append(expression.charAt(index));
            index++;
        }

        return stringBuilder.toString();
    }

    private static double additionalUnaryOps(String curOp, double arg) {
        if (curOp.matches("sin")) {
            return Math.sin(arg);

        } else if (curOp.matches("cos")) {
            return Math.cos(arg);

        } else if (curOp.matches("tan")) {
            return Math.tan(arg);

        } else if (curOp.matches("asin")) {
            return Math.asin(arg);

        } else if (curOp.matches("acos")) {
            return Math.acos(arg);

        } else if (curOp.matches("atan")) {
            return Math.atan(arg);

        } else if (curOp.matches("sqrt")) {
            return Math.sqrt(arg);

        } else if (curOp.matches("log")) {
            return Math.log(arg);
        } else {
            return 0;
        }
    }

    private static double numSyms(String curSym) {
        if (curSym.matches("e")) {
            return Math.exp(1);
        } else if (curSym.matches("pi")) {
            return Math.PI;
        } else {
            return 0;
        }
    }

    private static int findIndexAfterParentheses(String expression, int i) {
        int depth = 0;
        while (i < expression.length() && !(expression.charAt(i) == ')' && depth == 0)) {
            if (expression.charAt(i) == '(') depth++;
            if (expression.charAt(i) == ')') depth--;
            i++;
        }

        return i;
    }

    private static int findIndexAfterArg(String expression, int i) {
        int start = i;

        /* Arguments are done when the index is at the end of the expression or when
        * the current char matches '+', '-', '*' or '/' and current char is
        * not first char */
        while (i < expression.length() &&
                !(String.valueOf(expression.charAt(i)).matches("[+\\-*/]") && i > start)) {

            if (expression.charAt(i) == '(')
                i = findIndexAfterParentheses(expression, i + 1);

            i++;
        }

        return i - 1;
    }

    // Recursion 101
    private static double factorial(double n) {
        if (n > 0) {
            return n * factorial(n - 1);

        } else {
            return 1;
        }
    }
}
