package seedu.insureconnect.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.insureconnect.logic.parser.Prefix;
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
    @Test
    public void missingFieldsAndPreamble_formatsEachCombination() {
        assertEquals("Invalid command format!\nMissing required field: name.\nusage",
                Messages.getErrorMessageForMissingFieldsAndPreamble(List.of("name"), "", "usage"));
        assertEquals("Invalid command format!\nMissing required fields: name, phone. "
                + "Unexpected text before the first field: \"extra\".\nusage",
                Messages.getErrorMessageForMissingFieldsAndPreamble(List.of("name", "phone"), "extra", "usage"));
        assertEquals("Invalid command format!\nUnexpected text before the first field: \"extra\".\nusage",
                Messages.getErrorMessageForMissingFieldsAndPreamble(List.of(), "extra", "usage"));
    }

    @Test
    public void duplicatePrefixes_deduplicatesAndRequiresAtLeastOneField() {
        assertEquals(Messages.MESSAGE_DUPLICATE_FIELDS + "p/",
                Messages.getErrorMessageForDuplicatePrefixes(new Prefix("p/"), new Prefix("p/")));
        assertThrows(AssertionError.class, Messages::getErrorMessageForDuplicatePrefixes);
    }
}
