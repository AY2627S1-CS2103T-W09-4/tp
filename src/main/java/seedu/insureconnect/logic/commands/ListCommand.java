package seedu.insureconnect.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.insureconnect.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.Comparator;

import seedu.insureconnect.model.Model;
import seedu.insureconnect.model.person.Person;

/**
 * Lists all persons in InsureConnect to the user.
 */
public class ListCommand extends Command {

    public static final String COMMAND_WORD = "list";

    public static final String MESSAGE_SUCCESS = "Listed all persons.";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Lists all persons, optionally sorted by name or creation time.\n"
            + "Parameters: [n/|c/]\n"
            + "Examples: list, list n/, list c/";

    /** The order in which persons are displayed. */
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
            case NAME -> Comparator.comparing(person -> person.getName().fullName, String.CASE_INSENSITIVE_ORDER);
            case CREATED_AT -> Comparator.comparing(Person::getCreatedAt);
        };
        model.updatePersonListComparator(comparator);
        return new CommandResult(MESSAGE_SUCCESS);
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof ListCommand otherCommand && sortOrder == otherCommand.sortOrder;
    }
}
