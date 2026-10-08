package seedu.insureconnect.model.person;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.insureconnect.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class RemarkTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        // A Remark cannot be created without text, so null must cause an exception.
        assertThrows(NullPointerException.class, () -> new Remark(null));
    }

    @Test
    public void isValidRemark() {
        // The validation method must also reject null input.
        assertThrows(NullPointerException.class, () -> Remark.isValidRemark(null));

        // Remarks longer than 500 characters are invalid.
        assertFalse(Remark.isValidRemark("a".repeat(501)));

        // Empty remarks and remarks up to 500 characters, including symbols, are valid.
        assertTrue(Remark.isValidRemark(""));
        assertTrue(Remark.isValidRemark("Prefers WhatsApp, call after 6pm"));
        assertTrue(Remark.isValidRemark("Any symbols are allowed: #!@&/"));
        assertTrue(Remark.isValidRemark("a".repeat(500)));
    }

    @Test
    public void equals() {
        Remark remark = new Remark("Call after 6pm");

        // The same object is equal to itself.
        assertTrue(remark.equals(remark));

        // Two Remark objects with the same text are equal.
        assertTrue(remark.equals(new Remark("Call after 6pm")));

        // A Remark is not equal to null, another type, or different text.
        assertFalse(remark.equals(null));
        assertFalse(remark.equals("Call after 6pm"));
        assertFalse(remark.equals(new Remark("Call before 6pm")));
    }
}
