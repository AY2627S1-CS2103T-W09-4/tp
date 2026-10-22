package seedu.insureconnect.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.insureconnect.model.InsureConnect;
import seedu.insureconnect.model.Model;

/**
 * Clears InsureConnect.
 */
public class ClearCommand extends Command {

    public static final String COMMAND_WORD = "clear";
    public static final String MESSAGE_SUCCESS = "InsureConnect has been cleared!";
    public static final String MESSAGE_CONFIRMATION =
            "This will clear all records. Are you sure? (y/n, case-insensitive, default n)";
    public static final String MESSAGE_CANCELLED = "Clear cancelled. No records were cleared.";

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.setInsureConnect(new InsureConnect());
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
