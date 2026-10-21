package seedu.insureconnect.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Comparator;
import java.util.function.Predicate;

import seedu.insureconnect.commons.util.ToStringBuilder;
import seedu.insureconnect.logic.Messages;
import seedu.insureconnect.model.Model;
import seedu.insureconnect.model.person.NameContainsKeywordsPredicate;
import seedu.insureconnect.model.person.Person;

/**
 * Finds customers by case-insensitive name prefixes, phone-number prefixes, or an exact policy number.
 * Exact name-word matches appear before prefix-only name matches.
 */
public class FindCommand extends Command {

    public static final String COMMAND_WORD = "find";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Finds customers by name, phone-number prefix, or exact policy number.\n"
            + "Parameters: KEYWORD [MORE_KEYWORDS]... OR p/DIGITS OR t/POLICY_NUMBER\n"
            + "Examples: " + COMMAND_WORD + " alice; "
            + COMMAND_WORD + " p/9876; "
            + COMMAND_WORD + " t/LIFE20001";

    private final Predicate<Person> predicate;

    /**
     * Creates a search using the supplied customer matching rule.
     */
    public FindCommand(Predicate<Person> predicate) {
        this.predicate = requireNonNull(predicate);
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updatePersonListComparator(getResultComparator());
        model.updateFilteredPersonList(predicate);
        return createResult(model);
    }

    /**
     * Ranks name matches by relevance and leaves phone and policy-number matches in stored order.
     */
    private Comparator<Person> getResultComparator() {
        if (predicate instanceof NameContainsKeywordsPredicate namePredicate) {
            return Comparator.comparing(namePredicate::hasExactMatch).reversed();
        }
        return null;
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
