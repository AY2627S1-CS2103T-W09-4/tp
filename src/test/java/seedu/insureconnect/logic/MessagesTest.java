package seedu.insureconnect.logic;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.insureconnect.model.person.Person;
import seedu.insureconnect.testutil.PersonBuilder;

public class MessagesTest {

    @Test
    public void format_personWithRemark_includesRemark() {
        Person person = new PersonBuilder()
                .withRemark("Prefers WhatsApp after 6pm")
                .build();

        assertTrue(Messages.format(person)
                .contains("; Remark: Prefers WhatsApp after 6pm"));
    }
}