package sample;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.BoundingBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import java.net.URL;
import java.text.ParseException;
import java.util.Arrays;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML private ScrollPane pane;
    @FXML private VBox vBox;
    @FXML private TextField inputField;

    private Text result;
    private boolean resultNotAdded;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        pane.layout();
        pane.vvalueProperty().bind(vBox.heightProperty());
        inputField.setPrefWidth(400);

        resultNotAdded = true;
        result = new Text();
        result.setStyle("-fx-font-size: 17;");

        pane.addEventFilter(MouseEvent.MOUSE_CLICKED, handleFocus());
        inputField.addEventFilter(KeyEvent.KEY_PRESSED, handleKeyPress());
    }

    private EventHandler<KeyEvent> handleKeyPress() {
        return e -> {
            if (e.getCode().equals(KeyCode.ENTER))
                handleInput();
        };
    }

    private EventHandler<MouseEvent> handleFocus() {
        return e-> inputField.requestFocus();
    }

    @FXML
    private void handleInput() {
        if (resultNotAdded) {
            vBox.getChildren().remove(inputField);
            vBox.getChildren().addAll(result, inputField);
            resultNotAdded = false;
        }

        String input = Arrays.stream(inputField.getText().split(""))
                .filter(x->x.matches("\\S"))
                .reduce("", (x, y)->x+y);

        if (input.matches("clear")) {
            result.setText("");
            vBox.getChildren().remove(result);
            resultNotAdded = true;

        } else {
            calculate(input);
        }

        inputField.setText("");
    }

    private void calculate(String input) {
        String calcResult = null;
        try {
            calcResult = String.valueOf(MathExpParser.calculate(input));

        } catch (ParseException e) {
            result.setText(result.getText() + "\n" + "Invalid expression");
        }

        if (calcResult != null) {
            double shit = Double.parseDouble(calcResult);
            int trash = (int)shit;
            if (trash - shit == 0) calcResult = String.valueOf(trash);

            if (result.getText().matches(""))
                result.setText(input + " = " + calcResult);
            else
                result.setText(result.getText() + "\n" + input + " = " + calcResult);
        }
    }
}
