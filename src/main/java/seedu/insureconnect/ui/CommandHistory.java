package seedu.insureconnect.ui;

import java.util.ArrayList;
import java.util.List;

/**
 * Keeps submitted commands and an unfinished draft for navigation within one session.
 */
class CommandHistory {
    private final List<String> commands = new ArrayList<>();
    private int position;
    private String draft = "";

    /**
     * Records nonblank submissions once consecutively and starts a fresh navigation cycle.
     */
    void record(String command) {
        if (!command.isBlank() && (commands.isEmpty() || !commands.getLast().equals(command))) {
            commands.add(command);
        }
        position = commands.size();
    }

    /**
     * Recalls an older command, saving the current draft when leaving the newest position.
     */
    String previous(String currentText) {
        if (commands.isEmpty()) {
            return currentText;
        }
        if (isAtDraft()) {
            draft = currentText;
        }
        position = Math.max(0, position - 1);
        return commands.get(position);
    }

    /**
     * Recalls a newer command or restores the draft without wrapping around.
     */
    String next(String currentText) {
        if (isAtDraft()) {
            return currentText;
        }
        position++;
        return isAtDraft() ? draft : commands.get(position);
    }

    /**
     * Returns whether navigation is at the editable draft after all recorded commands.
     */
    boolean isAtDraft() {
        return position == commands.size();
    }
}
