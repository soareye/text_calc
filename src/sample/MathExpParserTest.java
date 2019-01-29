package sample;

import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.*;

class MathExpParserTest {

    @org.junit.jupiter.api.Test
    void calculate() {

        double actual = 0;

        try {
            actual = MathExpParser.calculate("(2+5/(1+4))^(2*2)");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        assertEquals(81.0, actual);

        assertThrows(ParseException.class, ()->MathExpParser.calculate("+-"));
        assertThrows(ParseException.class, ()->MathExpParser.calculate("-+"));
        assertThrows(ParseException.class, ()->MathExpParser.calculate("abc"));
        assertThrows(ParseException.class, ()->MathExpParser.calculate("2^+2"));
        assertThrows(ParseException.class, ()->MathExpParser.calculate("coscos"));
        assertThrows(ParseException.class, ()->MathExpParser.calculate("0..1"));
        assertThrows(ParseException.class, ()->MathExpParser.calculate("0.0.1"));
        assertThrows(ParseException.class, ()->MathExpParser.calculate(""));
    }
}