package seedu.insureconnect.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Comparator;
import java.util.function.Predicate;

import seedu.insureconnect.commons.util.ToStringBuilder;
import seedu.insureconnect.logic.Messages;
import seedu.insureconnect.model.Model;
import seedu.insureconnect.model.person.AllCriteriaPredicate;
import seedu.insureconnect.model.person.NameContainsKeywordsPredicate;
import seedu.insureconnect.model.person.Person;

/**
 * Finds customers by name, phone and policy prefixes, optionally combined with AND.
 * Exact name-word matches appear before prefix-only name matches.
 */
public class FindCommand extends Command {

    public static final String COMMAND_WORD = "find";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Finds customers by name keywords, phone prefix or policy prefix.\n"
            + "Combine fields with AND; name keywords match with OR. Exact name words rank first.\n"
            + "Parameters: KEYWORD [MORE_KEYWORDS]... OR [n/KEYWORDS] [p/DIGITS] [t/POLICY_PREFIX]\n"
            + "Supply at least one field, once each. Policy prefixes are case-sensitive.\n"
            + "Examples: " + COMMAND_WORD + " alice bob; " + COMMAND_WORD + " n/Alice p/912 t/LIFE";

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
     * Ranks name matches by relevance and leaves searches without names in stored order.
     */
    private Comparator<Person> getResultComparator() {
        if (predicate instanceof NameContainsKeywordsPredicate namePredicate) {
            return rankNameMatches(namePredicate);
        }
        if (predicate instanceof AllCriteriaPredicate combinedPredicate) {
            return combinedPredicate.getNamePredicate().map(this::rankNameMatches).orElse(null);
        }
        return null;
    }

    /**
     * Reuses exact-word ranking for both standalone and combined name searches.
     */
    private Comparator<Person> rankNameMatches(NameContainsKeywordsPredicate namePredicate) {
        return Comparator.comparing(namePredicate::hasExactMatch).reversed();
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
