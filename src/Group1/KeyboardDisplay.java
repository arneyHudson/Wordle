package Group1;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

public class KeyboardDisplay {

    private final VBox userKeys;

    public KeyboardDisplay(VBox userKeys) {
        this.userKeys = userKeys;
        setUpKeyboard();
    }

    /**
     * The setUpKeyboard method the section the user sees their previous input
     * @author Collin Schmocker
     */
    private void setUpKeyboard() {
        String[] keyboard = {"QWERTYUIOP", "ASDFGHJKL", "ZXCVBNM⌫"};
        List<Node> keyboardDisplay = userKeys.getChildren();
        for(int i = 0; i < keyboardDisplay.size(); i++) {
            for(char letter: keyboard[i].toCharArray()) {
                TextField textField = new TextField();
                textField.setDisable(true);
                textField.setPrefSize(32, 32);
                textField.setText(String.valueOf(letter));
                textField.setAlignment(Pos.CENTER);
                textField.setStyle("-fx-control-inner-background: gray; -fx-text-fill: white; -fx-opacity: 1.0;  " +
                        "-fx-font-family: Arial; -fx-font-weight: bold;");
                ((HBox)keyboardDisplay.get(i)).getChildren().add(textField);
            }
        }
    }

}
