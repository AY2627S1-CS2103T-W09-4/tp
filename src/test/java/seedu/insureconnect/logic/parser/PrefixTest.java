package seedu.insureconnect.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * Verifies prefix value semantics used by argument maps.
 */
public class PrefixTest {
    @Test
    public void equalPrefixes_findSameMapEntry() {
        Prefix name = new Prefix("n/");
        Map<Prefix, String> values = new HashMap<>();
        values.put(name, "Alice");
        assertEquals("Alice", values.get(new Prefix("n/")));
        assertNull(values.get(new Prefix("p/")));
        assertEquals(name, name);
        assertFalse(name.equals(null));
        assertFalse(name.equals("n/"));
        assertEquals("n/", name.getPrefix());
        assertEquals("n/", name.toString());
    }

    @Test
    public void nullPrefix_hashCodeIsZero() {
        assertEquals(0, new Prefix(null).hashCode());
    }
}
