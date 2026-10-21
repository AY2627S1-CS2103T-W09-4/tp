package seedu.insureconnect.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests session history boundaries, draft restoration and submission handling.
 */
public class CommandHistoryTest {
    private final CommandHistory history = new CommandHistory();

    @Test
    public void navigate_emptyHistory_preservesDraft() {
        assertEquals("draft", history.previous("draft"));
        assertEquals("draft", history.next("draft"));
        assertTrue(history.isAtDraft());
    }

    @Test
    public void navigate_multipleCommands_stopsAtBoundariesAndRestoresDraft() {
        history.record("list");
        history.record("help");
        assertEquals("help", history.previous("unfinished"));
        assertFalse(history.isAtDraft());
        assertEquals("list", history.previous("help"));
        assertEquals("list", history.previous("edited old command"));
        assertEquals("help", history.next("list"));
        assertEquals("unfinished", history.next("help"));
        assertEquals("new draft", history.next("new draft"));
        assertTrue(history.isAtDraft());
    }

    @Test
    public void record_blankAndConsecutiveDuplicates_keepsDistinctSubmissions() {
        history.record("   ");
        history.record("list");
        history.record("list");
        history.record("help");
        history.record("list");
        assertEquals("list", history.previous(""));
        assertEquals("help", history.previous(""));
        assertEquals("list", history.previous(""));
        assertEquals("list", history.previous(""));
    }

    @Test
    public void record_whileBrowsing_startsNewCycleAndPreservesExactText() {
        history.record("list");
        history.previous("old draft");
        history.record("  help  ");
        assertTrue(history.isAtDraft());
        assertEquals("  help  ", history.previous("new draft"));
        assertEquals("new draft", history.next(""));
        assertEquals("  help  ", history.previous("latest draft"));
        assertEquals("latest draft", history.next(""));
    }
}
