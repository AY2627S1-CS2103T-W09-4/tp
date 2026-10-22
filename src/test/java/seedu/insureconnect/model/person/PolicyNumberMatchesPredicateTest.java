package seedu.insureconnect.model.person;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.insureconnect.testutil.PersonBuilder;

/**
 * Tests matching customers against an exact policy number.
 */
public class PolicyNumberMatchesPredicateTest {

    /**
     * A customer with the requested policy number is matched.
     */
    @Test
    public void test_matchingPolicyNumber_returnsTrue() {
        PolicyNumberMatchesPredicate predicate =
                new PolicyNumberMatchesPredicate("LIFE20001");

        assertTrue(predicate.test(new PersonBuilder()
                .withTags("LIFE20001", "HEALTH20002")
                .build()));
    }

    /**
     * A customer without the requested policy number is not matched.
     */
    @Test
    public void test_differentPolicyNumber_returnsFalse() {
        PolicyNumberMatchesPredicate predicate =
                new PolicyNumberMatchesPredicate("LIFE20001");

        assertFalse(predicate.test(new PersonBuilder()
                .withTags("HEALTH20002")
                .build()));
    }

    /**
     * Policy-number matching ignores letter case.
     */
    @Test
    public void test_differentCase_returnsTrue() {
        PolicyNumberMatchesPredicate predicate =
                new PolicyNumberMatchesPredicate("life20001");

        assertTrue(predicate.test(new PersonBuilder()
                .withTags("LIFE20001")
                .build()));
    }
}
