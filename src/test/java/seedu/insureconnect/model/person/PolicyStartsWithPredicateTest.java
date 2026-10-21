package seedu.insureconnect.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.insureconnect.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.insureconnect.testutil.PersonBuilder;

/**
 * Tests policy prefix matching, validation and value semantics.
 */
public class PolicyStartsWithPredicateTest {
    @Test
    public void test_multiplePolicies_matchesAnyPrefixWithExactCase() {
        Person customer = new PersonBuilder().withTags("AUTO123", "LIFE456").build();
        assertTrue(new PolicyStartsWithPredicate("LIFE").test(customer));
        assertTrue(new PolicyStartsWithPredicate("AUTO123").test(customer));
        assertFalse(new PolicyStartsWithPredicate("life").test(customer));
        assertFalse(new PolicyStartsWithPredicate("456").test(customer));
        assertFalse(new PolicyStartsWithPredicate("LIFE4567").test(customer));
    }

    @Test
    public void constructor_invalidPrefix_rejectsInput() {
        assertThrows(NullPointerException.class, () -> new PolicyStartsWithPredicate(null));
        for (String invalid : new String[]{"", " ", "LIFE-1", "A".repeat(31)}) {
            assertThrows(IllegalArgumentException.class, () -> new PolicyStartsWithPredicate(invalid));
        }
    }

    @Test
    public void equalsAndToString_describePrefix() {
        PolicyStartsWithPredicate predicate = new PolicyStartsWithPredicate("LIFE");
        assertTrue(predicate.equals(predicate));
        assertEquals(predicate, new PolicyStartsWithPredicate("LIFE"));
        assertFalse(predicate.equals(new PolicyStartsWithPredicate("AUTO")));
        assertFalse(predicate.equals(null));
        assertFalse(predicate.equals("LIFE"));
        assertEquals(PolicyStartsWithPredicate.class.getCanonicalName() + "{prefix=LIFE}", predicate.toString());
    }
}
