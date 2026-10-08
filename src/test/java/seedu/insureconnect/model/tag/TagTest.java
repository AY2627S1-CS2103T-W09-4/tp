package seedu.insureconnect.model.tag;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.insureconnect.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class TagTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Tag(null));
    }

    @Test
    public void constructor_invalidTagName_throwsIllegalArgumentException() {
        String invalidTagName = "";
        assertThrows(IllegalArgumentException.class, () -> new Tag(invalidTagName));
    }

    @Test
    public void isValidTagName() {
        // null tag name
        assertThrows(NullPointerException.class, () -> Tag.isValidTagName(null));
        assertTrue(Tag.isValidTagName("A"));
        assertTrue(Tag.isValidTagName("LIFE20481"));
        assertTrue(Tag.isValidTagName("A".repeat(30)));
        assertFalse(Tag.isValidTagName(""));
        assertFalse(Tag.isValidTagName("A".repeat(31)));
        assertFalse(Tag.isValidTagName("POLICY-123"));
        assertFalse(Tag.isValidTagName("POLICY 123"));
    }

}
