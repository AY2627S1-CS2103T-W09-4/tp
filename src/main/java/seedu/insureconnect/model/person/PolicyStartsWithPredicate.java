package seedu.insureconnect.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.insureconnect.commons.util.AppUtil.checkArgument;

import java.util.function.Predicate;

import seedu.insureconnect.commons.util.ToStringBuilder;
import seedu.insureconnect.model.tag.Tag;

/**
 * Matches any of a customer's policy numbers by a case-sensitive prefix.
 */
public class PolicyStartsWithPredicate implements Predicate<Person> {
    private final String prefix;

    /**
     * Creates a policy search using the existing policy-number character and length constraints.
     */
    public PolicyStartsWithPredicate(String prefix) {
        requireNonNull(prefix);
        checkArgument(Tag.isValidTagName(prefix), Tag.MESSAGE_CONSTRAINTS);
        this.prefix = prefix;
    }

    @Override
    public boolean test(Person person) {
        return person.getTags().stream().anyMatch(tag -> tag.tagName.startsWith(prefix));
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof PolicyStartsWithPredicate otherPredicate
                && prefix.equals(otherPredicate.prefix);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("prefix", prefix).toString();
    }
}
