package seedu.insureconnect.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.insureconnect.commons.util.AppUtil.checkArgument;

import java.util.function.Predicate;

import seedu.insureconnect.commons.util.ToStringBuilder;

/**
 * Matches customers whose phone numbers start with the requested digits.
 */
public class PhoneStartsWithPredicate implements Predicate<Person> {
    public static final String MESSAGE_CONSTRAINTS = "Phone search requires one or more digits (0-9).";

    private final String prefix;

    /**
     * Creates a phone search using a nonempty sequence of digits, including partial phone numbers.
     */
    public PhoneStartsWithPredicate(String prefix) {
        requireNonNull(prefix);
        checkArgument(isValidPrefix(prefix), MESSAGE_CONSTRAINTS);
        this.prefix = prefix;
    }

    /**
     * Returns true if the query contains only digits and is not empty.
     */
    public static boolean isValidPrefix(String prefix) {
        return prefix.matches("[0-9]+");
    }

    @Override
    public boolean test(Person person) {
        return person.getPhone().value.startsWith(prefix);
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof PhoneStartsWithPredicate otherPredicate
                && prefix.equals(otherPredicate.prefix);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("prefix", prefix).toString();
    }
}
