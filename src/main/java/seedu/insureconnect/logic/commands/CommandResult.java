package seedu.insureconnect.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

import seedu.insureconnect.commons.util.ToStringBuilder;

/**
 * Represents the result of a command execution.
 */
public class CommandResult {

    private final String feedbackToUser;

    /** Help information should be shown to the user. */
    private final boolean showHelp;

    /** The application should exit. */
    private final boolean exit;

    /** The next command input should be treated as a confirmation response. */
    private final boolean confirmationRequired;

    /** The online User Guide should be opened in the default browser. */
    private final boolean shouldOpenUserGuide;

    /**
     * Constructs a {@code CommandResult} with the specified fields.
     */
    public CommandResult(String feedbackToUser, boolean showHelp, boolean exit) {
        this(feedbackToUser, showHelp, exit, false, false);
    }

    /**
     * Constructs a {@code CommandResult} with the specified fields and confirmation state.
     */
    public CommandResult(String feedbackToUser, boolean showHelp, boolean exit, boolean confirmationRequired) {
        this(feedbackToUser, showHelp, exit, confirmationRequired, false);
    }

    /**
     * Constructs a {@code CommandResult} with confirmation and User Guide states.
     */
    public CommandResult(String feedbackToUser, boolean showHelp, boolean exit, boolean confirmationRequired,
                         boolean shouldOpenUserGuide) {
        this.feedbackToUser = requireNonNull(feedbackToUser);
        this.showHelp = showHelp;
        this.exit = exit;
        this.confirmationRequired = confirmationRequired;
        this.shouldOpenUserGuide = shouldOpenUserGuide;
    }

    /**
     * Constructs a {@code CommandResult} with the specified {@code feedbackToUser},
     * and other fields set to their default value.
     */
    public CommandResult(String feedbackToUser) {
        this(feedbackToUser, false, false);
    }

    public String getFeedbackToUser() {
        return feedbackToUser;
    }

    public boolean isShowHelp() {
        return showHelp;
    }

    public boolean isExit() {
        return exit;
    }

    public boolean isConfirmationRequired() {
        return confirmationRequired;
    }

    public boolean shouldOpenUserGuide() {
        return shouldOpenUserGuide;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof CommandResult otherCommandResult)) {
            return false;
        }

        return feedbackToUser.equals(otherCommandResult.feedbackToUser)
                && showHelp == otherCommandResult.showHelp
                && exit == otherCommandResult.exit
                && confirmationRequired == otherCommandResult.confirmationRequired
                && shouldOpenUserGuide == otherCommandResult.shouldOpenUserGuide;
    }

    @Override
    public int hashCode() {
        return Objects.hash(feedbackToUser, showHelp, exit, confirmationRequired, shouldOpenUserGuide);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("feedbackToUser", feedbackToUser)
                .add("showHelp", showHelp)
                .add("exit", exit)
                .add("confirmationRequired", confirmationRequired)
                .add("shouldOpenUserGuide", shouldOpenUserGuide)
                .toString();
    }

}
