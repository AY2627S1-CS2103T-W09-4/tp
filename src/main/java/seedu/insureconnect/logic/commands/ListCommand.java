package seedu.insureconnect.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.insureconnect.logic.commands.CommandFormats.LIST_COMMAND_FORMAT;
import static seedu.insureconnect.model.Model.COMPARATOR_BY_CREATION_TIME;
import static seedu.insureconnect.model.Model.COMPARATOR_BY_NAME;
import static seedu.insureconnect.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.Comparator;

import seedu.insureconnect.model.Model;
import seedu.insureconnect.model.person.Person;

/**
 * Lists all customers in InsureConnect to the user.
 */
public class ListCommand extends Command {

    public static final String COMMAND_WORD = "list";
    public static final String MESSAGE_SUCCESS = "Listed all %1$d customer(s).";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Lists all customers, optionally sorted by name or creation time.\n"
            + "Format: " + LIST_COMMAND_FORMAT + "\n"
            + "Examples: list, list n/, list c/";

    /** The order in which customers are displayed. */
    public enum SortOrder {
        STORED, NAME, CREATED_AT
    }

    private final SortOrder sortOrder;

    public ListCommand() {
        this(SortOrder.STORED);
    }

    public ListCommand(SortOrder sortOrder) {
        this.sortOrder = requireNonNull(sortOrder);
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        Comparator<Person> comparator = switch (sortOrder) {
            case STORED -> null;
            case NAME -> COMPARATOR_BY_NAME;
            case CREATED_AT -> COMPARATOR_BY_CREATION_TIME;
        };
        model.updatePersonListComparator(comparator);
        return new CommandResult(String.format(MESSAGE_SUCCESS,
                model.getFilteredPersonList().size()));
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof ListCommand otherCommand && sortOrder == otherCommand.sortOrder;
    }
}
