package seedu.insureconnect.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    @Test
    public void test_partialPolicy_returnsFalse() {
        Person customer = new PersonBuilder().withTags("LIFE20001").build();
        for (String query : new String[]{"LIFE", "20001", "LIFE200012"}) {
            assertFalse(new PolicyNumberMatchesPredicate(query).test(customer));
        }
    }

    @Test
    public void equals_ignoresCaseAndRejectsOtherValues() {
        PolicyNumberMatchesPredicate predicate = new PolicyNumberMatchesPredicate("LIFE20001");
        assertEquals(predicate, predicate);
        assertEquals(predicate, new PolicyNumberMatchesPredicate("life20001"));
        assertFalse(predicate.equals(new PolicyNumberMatchesPredicate("AUTO1")));
        assertFalse(predicate.equals(null));
        assertFalse(predicate.equals("LIFE20001"));
        assertEquals("LIFE20001", predicate.toString());
        assertThrows(NullPointerException.class, () -> new PolicyNumberMatchesPredicate(null));
    }
}
