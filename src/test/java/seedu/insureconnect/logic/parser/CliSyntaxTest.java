package seedu.insureconnect.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.insureconnect.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.insureconnect.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.insureconnect.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.insureconnect.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.insureconnect.logic.parser.CliSyntax.PREFIX_REMARK;
import static seedu.insureconnect.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Checks that the shared CLI prefixes keep each documented field distinct during tokenization.
 */
public class CliSyntaxTest {
    @Test
    public void tokenize_allDocumentedFields_keepsValuesAndRepeatedPoliciesSeparate() {
        ArgumentMultimap fields = ArgumentTokenizer.tokenize(
                " n/Alice Tan p/91234567 e/alice@example.com a/123 Main Street t/LIFE1 t/AUTO2 r/Call tomorrow",
                PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS, PREFIX_TAG, PREFIX_REMARK);
        assertEquals("Alice Tan", fields.getValue(PREFIX_NAME).orElseThrow());
        assertEquals("91234567", fields.getValue(PREFIX_PHONE).orElseThrow());
        assertEquals("alice@example.com", fields.getValue(PREFIX_EMAIL).orElseThrow());
        assertEquals("123 Main Street", fields.getValue(PREFIX_ADDRESS).orElseThrow());
        assertEquals(List.of("LIFE1", "AUTO2"), fields.getAllValues(PREFIX_TAG));
        assertEquals("Call tomorrow", fields.getValue(PREFIX_REMARK).orElseThrow());
        assertEquals("", fields.getPreamble());
    }
}
