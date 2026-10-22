package seedu.insureconnect.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Comparator;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.insureconnect.logic.Messages;
import seedu.insureconnect.logic.parser.FindCommandParser;
import seedu.insureconnect.logic.parser.exceptions.ParseException;
import seedu.insureconnect.model.ModelManager;
import seedu.insureconnect.model.person.Person;
import seedu.insureconnect.testutil.PersonBuilder;

/**
 * Checks combined searches against real model filtering and sorting without mutating stored customers.
 */
public class CombinedFindCommandTest {
    private final Person prefix = customer("Alicea", "91234567", "LIFE1");
    private final Person exact = customer("Alice", "91234568", "LIFE2");
    private final Person otherName = customer("Bob", "91234569", "LIFE3");
    private final Person otherPhone = customer("Alice", "81234567", "LIFE4");
    private final Person otherPolicy = customer("Alice", "91234560", "AUTO1");
    private final List<Person> storedCustomers = List.of(prefix, exact, otherName, otherPhone, otherPolicy);
    private final ModelManager model = createModel();

    @Test
    public void execute_allFields_intersectsCriteriaAndRanksExactNamesFirst() throws ParseException {
        assertResults("n/Alice p/912", List.of(exact, otherPolicy, prefix));
        assertResults("n/Alice p/912 t/life2", List.of(exact));
        assertResults("n/Bob p/912 t/LIFE2", List.of());
        assertResults("n/Alice p/812 t/LIFE2", List.of());
        assertResults("n/Alice p/912 t/LIFE4", List.of());
        assertResults("t/LIFE3 n/Alice Bob p/912", List.of(otherName));
    }

    @Test
    public void execute_withoutName_restoresStoredOrderFromPreviousSort() throws ParseException {
        model.updatePersonListComparator(Comparator.comparing(person -> person.getName().fullName));
        assertResults("p/912", List.of(prefix, exact, otherName, otherPolicy));
        assertResults("p/912 t/LIFE1", List.of(prefix));
        assertResults("t/life4", List.of(otherPhone));
    }

    @Test
    public void execute_subsequentSearch_replacesFilterIncludingEmptyResults() throws ParseException {
        assertResults("n/Alice p/000", List.of());
        assertResults("n/Bob t/LIFE3", List.of(otherName));
        assertResults("n/Alice p/812", List.of(otherPhone));
        assertResults("p/912", List.of(prefix, exact, otherName, otherPolicy));
        assertResults("Bob", List.of(otherName));
    }

    @Test
    public void execute_policyNumber_ignoresCaseAndRequiresExactMatchOnAnyPolicy() throws ParseException {
        Person multiplePolicies = new PersonBuilder().withName("Carol").withPhone("92345678")
                .withTags("AUTO2", "LIFE5").build();
        model.addPerson(multiplePolicies);
        new FindCommandParser().parse("n/Carol t/life5").execute(model);
        assertEquals(List.of(multiplePolicies), model.getFilteredPersonList());
        new FindCommandParser().parse("n/Carol t/LIFE").execute(model);
        assertEquals(List.of(), model.getFilteredPersonList());
        new FindCommandParser().parse("t/life5").execute(model);
        assertEquals(List.of(multiplePolicies), model.getFilteredPersonList());
        new FindCommandParser().parse("t/LIFE").execute(model);
        assertEquals(List.of(), model.getFilteredPersonList());
    }

    /**
     * Builds independent customers with unique identity and policy values.
     */
    private Person customer(String name, String phone, String policy) {
        return new PersonBuilder().withName(name).withPhone(phone).withTags(policy).build();
    }

    /**
     * Inserts customers in deliberately nonalphabetical order to expose sorting regressions.
     */
    private ModelManager createModel() {
        ModelManager result = new ModelManager();
        storedCustomers.forEach(result::addPerson);
        return result;
    }

    /**
     * Checks the displayed result, count and unchanged storage order after a parsed command.
     */
    private void assertResults(String query, List<Person> expected) throws ParseException {
        CommandResult result = new FindCommandParser().parse(query).execute(model);
        assertEquals(expected, model.getFilteredPersonList());
        assertEquals(String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW, expected.size()),
                result.getFeedbackToUser());
        assertEquals(storedCustomers, model.getInsureConnect().getPersonList());
    }
}
