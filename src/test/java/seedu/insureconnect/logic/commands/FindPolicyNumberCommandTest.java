package seedu.insureconnect.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.insureconnect.logic.Messages;
import seedu.insureconnect.logic.parser.InsureConnectParser;
import seedu.insureconnect.model.InsureConnect;
import seedu.insureconnect.model.Model;
import seedu.insureconnect.model.ModelManager;
import seedu.insureconnect.model.UserPrefs;
import seedu.insureconnect.model.person.Person;
import seedu.insureconnect.testutil.PersonBuilder;

/**
 * Tests policy-number searches from command text through the model.
 */
public class FindPolicyNumberCommandTest {
    private final Person alex = new PersonBuilder()
            .withName("Alex Tan")
            .withTags("LIFE20001")
            .build();
    private final Person bernice = new PersonBuilder()
            .withName("Bernice Lim")
            .withTags("HEALTH20002", "LIFE20003")
            .build();

    private Model model;

    /**
     * Creates customers with distinct policy numbers for every test.
     */
    @BeforeEach
    public void setUp() {
        InsureConnect data = new InsureConnect();
        data.setPersons(List.of(alex, bernice));
        model = new ModelManager(data, new UserPrefs());
    }

    /**
     * Finds the customer who owns the requested policy number.
     */
    @Test
    public void execute_matchingPolicyNumber_returnsPolicyOwner() throws Exception {
        CommandResult result = new InsureConnectParser()
                .parseCommand("find t/LIFE20001")
                .execute(model);

        assertEquals(List.of(alex), model.getFilteredPersonList());
        assertEquals(String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW, 1),
                result.getFeedbackToUser());
    }

    /**
     * Shows an empty list when no customer owns the requested policy number.
     */
    @Test
    public void execute_nonMatchingPolicyNumber_returnsNoCustomers() throws Exception {
        new InsureConnectParser()
                .parseCommand("find t/MOTOR99999")
                .execute(model);

        assertEquals(List.of(), model.getFilteredPersonList());
    }
}

