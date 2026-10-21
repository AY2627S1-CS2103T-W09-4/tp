package seedu.insureconnect.ui;

import java.util.List;

import javafx.scene.Node;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;

/**
 * Cycles keyboard focus between the main working areas without changing their contents.
 */
class FocusNavigation {

    private static final KeyCombination AREA_NEXT = new KeyCodeCombination(KeyCode.F6);
    private static final KeyCombination AREA_PREVIOUS = new KeyCodeCombination(KeyCode.F6, KeyCombination.SHIFT_DOWN);

    private final List<Node> focusTargets;

    /**
     * Creates a focus cycle in command, output, contacts order.
     */
    FocusNavigation(Node commandInput, Node commandOutput, Node contacts) {
        focusTargets = List.of(commandInput, commandOutput, contacts);
        markFocusTargets();
    }

    /**
     * Handles only the dedicated area shortcuts, leaving ordinary control keys untouched.
     */
    void handleKeyPressed(KeyEvent event) {
        if (AREA_NEXT.match(event)) {
            moveFocus(event, false);
        } else if (AREA_PREVIOUS.match(event)) {
            moveFocus(event, true);
        }
    }

    /**
     * Marks each area for a visible focus indicator.
     */
    private void markFocusTargets() {
        for (Node target : focusTargets) {
            target.getStyleClass().add("navigation-target");
        }
    }

    /**
     * Moves focus relative to the event target and prevents control-level handling of the shortcut.
     */
    private void moveFocus(KeyEvent event, boolean isReverse) {
        focusTarget(getNextTarget(event, isReverse));
        event.consume();
    }

    /**
     * Focuses an area while retaining the editing state of text controls.
     */
    private void focusTarget(Node target) {
        List<TextSelection> selections = captureTextSelections();
        target.requestFocus();
        selections.forEach(TextSelection::restore);
    }

    /**
     * Captures editing state before either focus loss or focus gain changes a selection.
     */
    private List<TextSelection> captureTextSelections() {
        return focusTargets.stream().filter(TextInputControl.class::isInstance)
                .map(TextInputControl.class::cast)
                .map(input -> new TextSelection(input, input.getAnchor(), input.getCaretPosition()))
                .toList();
    }

    /**
     * Resolves the shortcut's target to the next focusable area.
     */
    private Node getNextTarget(KeyEvent event, boolean isReverse) {
        Node currentTarget = event.getTarget() instanceof Node ? (Node) event.getTarget() : null;
        return focusTargets.get(getNextIndex(currentTarget, isReverse));
    }

    /**
     * Finds the adjacent area, entering the cycle at either end when focus is outside it.
     */
    private int getNextIndex(Node currentTarget, boolean isReverse) {
        int currentIndex = getAreaIndex(currentTarget);
        if (currentIndex == -1) {
            return isReverse ? focusTargets.size() - 1 : 0;
        }
        return Math.floorMod(currentIndex + (isReverse ? -1 : 1), focusTargets.size());
    }

    /**
     * Resolves child nodes such as list cells to their containing focus area.
     */
    private int getAreaIndex(Node node) {
        if (node == null) {
            return -1;
        }
        int index = focusTargets.indexOf(node);
        return index == -1 ? getAreaIndex(node.getParent()) : index;
    }

    /**
     * Retains selection direction as well as the insertion point for a text control.
     */
    private record TextSelection(TextInputControl input, int anchor, int caret) {
        /**
         * Restores the selection after native focus handling has completed.
         */
        private void restore() {
            input.selectRange(anchor, caret);
        }
    }
}
