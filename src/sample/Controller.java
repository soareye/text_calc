package sample;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class Controller {

    @FXML private TextField inputField;
    @FXML private Text result;

    @FXML
    private void calculate() {
        String input = inputField.getText();
        String calcResult = String.valueOf(MathExpParser.calculate(input));
        inputField.setText("");
        result.setText(input + " = " + calcResult);
    }
}
