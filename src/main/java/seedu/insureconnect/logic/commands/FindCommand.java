package seedu.insureconnect.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Comparator;

import seedu.insureconnect.commons.util.ToStringBuilder;
import seedu.insureconnect.logic.Messages;
import seedu.insureconnect.model.Model;
import seedu.insureconnect.model.person.NameContainsKeywordsPredicate;
import seedu.insureconnect.model.person.Person;

/**
 * Finds and lists all persons in InsureConnect with a name word starting with any of the argument keywords.
 * Keyword matching is case insensitive.
 */
public class FindCommand extends Command {

    public static final String COMMAND_WORD = "find";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Finds all persons with a name word starting with any of the specified keywords (case-insensitive) "
            + "and displays them as a list with index numbers, with exact name words before prefix-only matches.\n"
            + "Parameters: KEYWORD [MORE_KEYWORDS]...\n"
            + "Example: " + COMMAND_WORD + " alice bob charlie";

    private final NameContainsKeywordsPredicate predicate;

    public FindCommand(NameContainsKeywordsPredicate predicate) {
        this.predicate = predicate;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updatePersonListComparator(getResultComparator());
        model.updateFilteredPersonList(predicate);
        return createResult(model);
    }

    /**
     * Orders exact name-word matches first, leaving ties in their stored order.
     */
    private Comparator<Person> getResultComparator() {
        return Comparator.comparing(predicate::hasExactMatch).reversed();
    }

    /**
     * Reports the number of customers in the search results.
     */
    private CommandResult createResult(Model model) {
        return new CommandResult(
                String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW, model.getFilteredPersonList().size()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof FindCommand otherFindCommand)) {
            return false;
        }

        return predicate.equals(otherFindCommand.predicate);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("predicate", predicate)
                .toString();
    }
}
