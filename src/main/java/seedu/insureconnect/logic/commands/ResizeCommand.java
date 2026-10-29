package seedu.insureconnect.logic.commands;

import static seedu.insureconnect.logic.commands.CommandFormats.RESIZE_COMMAND_FORMAT;

import seedu.insureconnect.model.Model;

/** Requests a new size for the main application window. */
public class ResizeCommand extends Command {
    public static final String COMMAND_WORD = "resize";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Resizes the main window.\n"
            + "Format: " + RESIZE_COMMAND_FORMAT + "\n"
            + "Examples: resize p/med; resize s/1000 700";

    private final WindowResize resize;

    public ResizeCommand(WindowResize resize) {
        this.resize = resize;
    }

    @Override
    public CommandResult execute(Model model) {
        return new CommandResult("Window resized.", resize);
    }
}
