package seedu.insureconnect.model.person;

import static java.util.Objects.requireNonNull;

import java.util.function.Predicate;

import seedu.insureconnect.model.tag.Tag;

/**
 * Tests whether a customer owns the specified policy number.
 */
public class PolicyNumberMatchesPredicate implements Predicate<Person> {
    public static final String MESSAGE_CONSTRAINTS = Tag.MESSAGE_CONSTRAINTS;

    private final String policyNumber;

    /**
     * Creates a predicate that matches customers owning the specified policy number.
     *
     * @param policyNumber the policy number to search for
     */
    public PolicyNumberMatchesPredicate(String policyNumber) {
        requireNonNull(policyNumber);
        this.policyNumber = policyNumber;
    }

    /**
     * Returns true if the policy number contains one or more letters or digits.
     */
    public static boolean isValidPolicyNumber(String policyNumber) {
        return Tag.isValidTagName(policyNumber);
    }

    @Override
    public boolean test(Person person) {
        return person.getTags().stream()
                .map(tag -> tag.tagName)
                .anyMatch(tagName -> tagName.equalsIgnoreCase(policyNumber));
    }

    @Override
    public boolean equals(Object other) {
        return other == this
                || (other instanceof PolicyNumberMatchesPredicate otherPredicate
                && policyNumber.equalsIgnoreCase(otherPredicate.policyNumber));
    }

    @Override
    public String toString() {
        return policyNumber;
    }
}
