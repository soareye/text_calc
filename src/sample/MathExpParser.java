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
    * from a given index and returning earlier if we're parsing
    * an exponent: */
    private static double parse(String expression, int from, boolean isExponent) throws ParseException {
        if (expression.length() == 0)
            throw new ParseException(expression, 0);

        double result = 0;

        /* "product" holds the result of recent multiplications and divisions
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

        for (int i = from; i < expression.length(); i++) {
            String currentSymbol = String.valueOf(expression.charAt(i));

            if (currentSymbol.matches("[\\d.]")) {
                numStrBuilder.append(currentSymbol);
                curNum = Double.parseDouble(numStrBuilder.toString());

            } else if (currentSymbol.matches("[*/+\\-]")) {
                /* Sequential operation-symbols or starting with an operation that's not minus or
                * ending with any operation-symbol is illegal syntax: */
                if ((i > 0 && String.valueOf(expression.charAt(i - 1)).matches("[*/+\\-]")) ||
                        (i == 0 && !currentSymbol.matches("-")) ||
                        i == expression.length() - 1) {

                    throw new ParseException(expression, i);
                }

                product = binaryOps(prevOp, product, curNum);

                if (isExponent && !(currentSymbol.matches("-") && i == from))
                    return result + product;

                numStrBuilder = new StringBuilder();
                prevOp = currentSymbol;

                // If the current symbol is + or -, we no longer need any memory
                // of what came previously and we can add product to result:
                if (currentSymbol.matches("[+\\-]")) {
                    result += product;
                    product = 0;
                }

            } else if (currentSymbol.matches("\\^")) {
                double exponent = parse(expression, i + 1, true);
                curNum = Math.pow(curNum, exponent);
                i = traversePow(expression, i + 1);

            } else if (currentSymbol.matches("\\(")) {
                curNum = parse(expression, i + 1, false);
                i = traverseParentheses(expression, i + 1);

            } else if (currentSymbol.matches("\\)")) {
                // If it's the end of a parenthesized expression, perform the
                // last binary operation on product and curNum, add it to the
                // result and return:
                product = binaryOps(prevOp, product, curNum);
                return result + product;

            } else if (matchesUnaryOps(expression, i)) {
                String curOp = getCurOp(expression, i);
                double arg = parse(expression, i + curOp.length() + 1, false);

                curNum = additionalUnaryOps(curOp, arg);
                i = traverseParentheses(expression, i + curOp.length() + 1);

            } else if (currentSymbol.matches("!")) {
                if (isInt(curNum)) {
                    curNum = factorial(curNum);

                } else {
                    throw new ParseException(expression, i);
                }

            } else {
                throw new ParseException(expression, i);
            }
        }

        // Finish up by combining product and curNum, adding it to result and returning:
        product = binaryOps(prevOp, product, curNum);
        return result + product;
    }

    private static double binaryOps(String op, double product, double secArg) {
        if (op.matches("-")) {
            return -secArg;

        } else if (op.matches("\\*")) {
            return product * secArg;

        } else if (op.matches("/")) {
            return product / secArg;

        } else {
            return secArg;
        }
    }

    private static boolean matchesUnaryOps(String expression, int index) {
        String opNames[] = {"sin", "cos", "tan", "asin", "acos", "atan", "sqrt"};
        String longest = Arrays.stream(opNames)
                .reduce("", (x, y)-> x.length() > y.length() ? x : y);

        if (index + longest.length() > expression.length())
            return false;

        for (String opName : opNames) {
            String op = expression.substring(index, index + opName.length());
            if (opName.equals(op)) return true;
        }

        return false;
    }

    private static String getCurOp(String expression, int index) {
        StringBuilder stringBuilder = new StringBuilder();

        while (index < expression.length() && expression.charAt(index) != '(') {
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

        } else {
            return 0;
        }
    }

    private static int traverseParentheses(String expression, int i) {
        int depth = 0;
        while (i < expression.length() && !(expression.charAt(i) == ')' && depth == 0)) {
            if (expression.charAt(i) == '(') depth++;
            if (expression.charAt(i) == ')') depth--;
            i++;
        }

        return i;
    }

    private static int traversePow(String expression, int i) {
        int start = i;

        while (i < expression.length() &&
                (!String.valueOf(expression.charAt(i)).matches("[+*/]") &&
                !(String.valueOf(expression.charAt(i)).matches("-") && i > start))) {

            if (expression.charAt(i) == '(')
                i = traverseParentheses(expression, i + 1);

            i++;
        }

        return i - 1;
    }

    private static double unaryPostfixOps(String nextSym, double curNum) {
        if (nextSym.matches("!"))
            return factorial(curNum);

        return curNum;
    }

    private static double factorial(double n) {
        if (n < 0)
            throw new IllegalArgumentException("n less than 0.");

        return fact(n);
    }

    private static double fact(double n) {
        if (n > 0) {
            return n * fact(n - 1);

        } else {
            return 1;
        }
    }

    private static boolean isInt(double n) {
        return (int)(n) - n == 0;
    }
}
