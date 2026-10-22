package seedu.insureconnect.ui;

import static java.util.Objects.requireNonNull;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Region;

/**
 * A UI component that displays the result of a command execution.
 */
public class ResultDisplay extends UiPart<Region> {

    private static final String FXML = "ResultDisplay.fxml";

    @FXML
    private TextArea resultDisplay;

    /**
     * Creates a result display with its initial ready message.
     */
    public ResultDisplay() {
        super(FXML);
        resultDisplay.setText("Ready. Type a command to search or manage customers.");
    }

    /**
     * Displays the supplied feedback to the user.
     */
    public void setFeedbackToUser(String feedbackToUser) {
        requireNonNull(feedbackToUser);
        resultDisplay.setText(feedbackToUser);
    }

    /**
     * Returns the control that receives keyboard focus for this area.
     */
    Node getFocusTarget() {
        return resultDisplay;
    }
}
