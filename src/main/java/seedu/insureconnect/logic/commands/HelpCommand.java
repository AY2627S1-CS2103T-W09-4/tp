package seedu.insureconnect.logic.commands;

import static seedu.insureconnect.logic.commands.CommandFormats.HELP_COMMAND_FORMAT;

import seedu.insureconnect.model.Model;

/**
 * Formats full help instructions for every command for display.
 */
public class HelpCommand extends Command {

    public static final String COMMAND_WORD = "help";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Shows program usage instructions.\n"
            + "Format: " + HELP_COMMAND_FORMAT;

    public static final String SHOWING_HELP_MESSAGE = "Opened help window.";

    @Override
    public CommandResult execute(Model model) {
        return new CommandResult(SHOWING_HELP_MESSAGE, true, false);
    }
}
