package seedu.insureconnect.ui;

import javafx.scene.control.TextArea;
import javafx.scene.text.Text;

/**
 * A read-only, selectable text area that grows vertically to fit its content.
 */
public class SelectableText extends TextArea {

    private static final double CONTENT_WIDTH_ALLOWANCE = 4;

    /**
     * Creates a selectable text control with label-like behaviour.
     */
    public SelectableText() {
        setEditable(false);
        setFocusTraversable(true);
        setWrapText(true);
        setPrefRowCount(1);
        setMinHeight(USE_PREF_SIZE);
        setMaxHeight(USE_PREF_SIZE);

        textProperty().addListener((observable, oldText, newText) -> requestLayout());
    }

    @Override
    protected double computePrefHeight(double width) {
        double singleLineHeight = super.computePrefHeight(width);

        if (width <= 0 || getText().isEmpty()) {
            return singleLineHeight;
        }

        Text measuredText = new Text(getText());
        measuredText.setFont(getFont());
        measuredText.setWrappingWidth(Math.max(0,
            width - snappedLeftInset() - snappedRightInset() - CONTENT_WIDTH_ALLOWANCE));

        Text singleLineText = new Text("Ag");
        singleLineText.setFont(getFont());

        double additionalHeight = Math.max(0,
            measuredText.getLayoutBounds().getHeight() - singleLineText.getLayoutBounds().getHeight());

        return singleLineHeight + additionalHeight;
    }
}
