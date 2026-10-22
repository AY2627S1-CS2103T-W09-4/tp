package seedu.insureconnect.logic.commands;

import seedu.insureconnect.logic.commands.exceptions.CommandException;
import seedu.insureconnect.model.Model;

/**
 * Represents a request to confirm the pending customer deletion.
 * The LogicManager supplies the pending deletion before this command can execute.
 */
public class ConfirmDeleteCommand extends Command {

    public static final String COMMAND_WORD = "confirm";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Confirms the pending customer deletion.\n"
            + "Example: " + COMMAND_WORD;
    public static final String MESSAGE_NO_PENDING_DELETION =
            "There is no customer deletion to confirm.";

    @Override
    public CommandResult execute(Model model) throws CommandException {
        throw new CommandException(MESSAGE_NO_PENDING_DELETION);
    }
}
