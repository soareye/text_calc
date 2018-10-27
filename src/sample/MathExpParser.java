package sample;

import java.util.Arrays;
import java.util.function.UnaryOperator;

public class MathExpParser {

    private static String curOp = "";

    public static double calculate(String expression) {
        curOp = "";
        // Remove whitespace and put individual chars in a String array:
        String symbols[] = Arrays.stream(expression.split(""))
                .filter(x->!x.matches("\\s"))
                .toArray(String[]::new);

        return calcIter(symbols, 0);
    }

    public static double calcIter(String symbols[], int from) {

        double result = 0;
        double product = 0;
        double curNum = 0;
        double prevNum = 0;
        String prevSym = "";
        boolean power = false;

        StringBuilder numStrBuilder = new StringBuilder();

        for (int i = from; i < symbols.length; i++) {

            if (symbols[i].matches("[\\d.]")) {
                numStrBuilder.append(symbols[i]);
                curNum = Double.parseDouble(numStrBuilder.toString());

            } else if (symbols[i].matches("[*/+\\-]")) {
                if (power) {
                    curNum = Math.pow(prevNum, curNum);
                    power = false;
                }

                product = binaryOps(prevSym, product, curNum);
                numStrBuilder = new StringBuilder();
                prevSym = symbols[i];

                if (symbols[i].matches("[+\\-]")) {
                    result += product;
                    product = 0;
                }

            } else if (symbols[i].matches("\\^")) {
                prevNum = curNum;
                power = true;
                numStrBuilder = new StringBuilder();

            } else if (symbols[i].matches("\\(")) {
                curNum = calcIter(symbols, i + 1);
                i = traverseParentheses(symbols, i + 1);

            } else if (symbols[i].matches("\\)")) {
                product = binaryOps(prevSym, product, curNum);
                return result + product;

            } else if (matchesUnaryOps(symbols, i)) {
                curNum = additionalUnaryOps(symbols, i);
                i = traverseParentheses(symbols, i + curOp.length() + 1);

            } else {
                throw new IllegalArgumentException("Undefined mathematical expression!");
            }
        }

        // Finish up by combining product and curNum, adding it to result and returning:
        if (power) curNum = Math.pow(prevNum, curNum);
        product = binaryOps(prevSym, product, curNum);
        return result + product;
    }

    public static double binaryOps(String op, double firstArg, double secArg) {
        if (op.matches("-")) {
            return -secArg;

        } else if (op.matches("\\*")) {
            return firstArg * secArg;

        } else if (op.matches("/")) {
            return firstArg / secArg;

        } else {
            return secArg;
        }
    }

    public static boolean matchesUnaryOps(String symbols[], int index) {
        String opNames[] = {"sin", "cos", "tan", "asin", "acos", "atan"};
        String longest = Arrays.stream(opNames)
                .reduce("", (x, y)-> x.length() > y.length() ? x : y);

        if (symbols.length - index < longest.length())
            return false;

        for (String opName : opNames) {
            String opNameChars[] = opName.split("");
            boolean isOp = true;

            for (int i = 0; i < opNameChars.length; i++) {
                if (!symbols[index + i].matches(opNameChars[i]))
                    isOp = false;
            }

            if (isOp)
                return true;
        }

        return false;
    }

    public static double additionalUnaryOps(String symbols[], int index) {

        double result = 0;
        result += unaryOp("sin", Math::sin, symbols, index);
        result += unaryOp("cos", Math::cos, symbols, index);
        result += unaryOp("tan", Math::tan, symbols, index);
        result += unaryOp("asin", Math::asin, symbols, index);
        result += unaryOp("acos", Math::acos, symbols, index);
        result += unaryOp("acos", Math::atan, symbols, index);

        return result;
    }

    public static double unaryOp(String opName, UnaryOperator<Double> op, String symbols[], int index) {

        String opSymbols[] = opName.split("");

        for (int i = 0; i < opSymbols.length; i++) {
            if (!opSymbols[i].matches(symbols[i + index])) {
                return 0;
            }
        }

        double arg = calcIter(symbols, index + opName.length() + 1);
        curOp = opName;

        return op.apply(arg);
    }

    public static int traverseParentheses(String symbols[], int i) {
        int depth = 0;
        while (!(symbols[i].matches("\\)") && depth == 0)) {
            if (symbols[i].matches("\\(")) depth++;
            if (symbols[i].matches("\\)")) depth--;
            i++;
        }

        return i;
    }

    public static double unaryPostfixOps(String nextSym, double curNum) {
        if (nextSym.matches("!"))
            return factorial(curNum);

        return curNum;
    }

    public static double factorial(double n) {
        if (n < 0)
            throw new IllegalArgumentException("n less than 0.");

        return fact(n);
    }

    public static double fact(double n) {
        if (n > 0) {
            return n * fact(n - 1);

        } else {
            return 1;
        }
    }
}
