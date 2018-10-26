package sample;

import java.util.function.UnaryOperator;

public class MathExpParser {

    private static String curOp = "";

    public static void main(String args[]) {

        System.out.println(calculate("1000*asin(sin(1))/1000*1000+(100+(100+500+(((100)+100)+100)))"));
        System.out.println();

        System.out.println(calculate("sin(" + String.valueOf(Math.PI) + ")"));
        System.out.println();

        System.out.println(calculate("5 + 5 + (10 - 5)/(2 + 3)"));
    }

    public static boolean valid(String expression) {
        return true;
    }

    public static double calculate(String expression) {

        String splitExp[] = expression.split("");

        int whitespacecount = 0;
        for (int i = 0; i < splitExp.length; i++) {
            if (splitExp[i].matches("\\s")) {
                whitespacecount++;
            }
        }

        String symbols[] = new String[splitExp.length - whitespacecount];

        int j = 0;
        for (int i = 0; i < splitExp.length; i++) {
            if (!splitExp[i].matches("\\s")) {
                symbols[j] = splitExp[i];
                j++;
            }
        }

        return calcIter(symbols, 0);
    }

    public static double calcIter(String symbols[], int from) {

        double result = 0;
        double product = 0;
        double curNum = 0;
        String prevSym = "";

        StringBuilder numStrBuilder = new StringBuilder();

        for (int i = from; i < symbols.length; i++) {

            if (symbols[i].matches("[\\d.]")) {
                numStrBuilder.append(symbols[i]);
                curNum = Double.parseDouble(numStrBuilder.toString());

            } else if (symbols[i].matches("[*/+\\-]")) {
                product = binaryOps(prevSym, product, curNum);
                numStrBuilder = new StringBuilder();
                prevSym = symbols[i];

                if (symbols[i].matches("[+\\-]")) {
                    result += product;
                    product = 0;
                }

            } else if (symbols[i].matches("\\(")) {
                curNum = calcIter(symbols, i + 1);

                i++;
                i = traverseParentheses(symbols, i);

            } else if (symbols[i].matches("\\)")) {
                product = binaryOps(prevSym, product, curNum);
                return result + product;

            } else {

                curNum = additionalUnaryOps(symbols, i);

                i += (curOp.length() + 1);
                i = traverseParentheses(symbols, i);
            }
        }

        product = binaryOps(prevSym, product, curNum);
        return result + product;
    }

    public static double binaryOps(String op, double product, double curNum) {
        if (op.matches("-")) {
            return -curNum;

        } else if (op.matches("\\*")) {
            return product * curNum;

        } else if (op.matches("/")) {
            return product / curNum;

        } else {
            return curNum;
        }
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
