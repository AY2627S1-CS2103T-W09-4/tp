package seedu.insureconnect.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.insureconnect.commons.util.AppUtil.checkArgument;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import seedu.insureconnect.commons.util.ToStringBuilder;

/**
 * Matches customers satisfying every supplied criterion while retaining name-ranking information.
 */
public class AllCriteriaPredicate implements Predicate<Person> {
    public static final String MESSAGE_NO_CRITERIA = "At least one search criterion is required.";

    private final List<Predicate<Person>> criteria;

    /**
     * Takes an immutable snapshot of the criteria to combine.
     * An empty list is rejected because it would match every customer.
     */
    public AllCriteriaPredicate(List<Predicate<Person>> criteria) {
        requireNonNull(criteria);
        checkArgument(!criteria.isEmpty(), MESSAGE_NO_CRITERIA);
        this.criteria = List.copyOf(criteria);
    }

    @Override
    public boolean test(Person person) {
        return criteria.stream().allMatch(criterion -> criterion.test(person));
    }

    /**
     * Returns the name criterion, if present, for ranking matching customers.
     */
    public Optional<NameContainsKeywordsPredicate> getNamePredicate() {
        return criteria.stream().filter(NameContainsKeywordsPredicate.class::isInstance)
                .map(NameContainsKeywordsPredicate.class::cast).findFirst();
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof AllCriteriaPredicate otherPredicate
                && criteria.equals(otherPredicate.criteria);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("criteria", criteria).toString();
    }
}
