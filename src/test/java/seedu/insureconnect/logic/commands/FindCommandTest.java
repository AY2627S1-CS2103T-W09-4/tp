package seedu.insureconnect.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.insureconnect.logic.Messages.MESSAGE_PERSONS_LISTED_OVERVIEW;
import static seedu.insureconnect.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.insureconnect.testutil.TypicalPersons.ALICE;
import static seedu.insureconnect.testutil.TypicalPersons.CARL;
import static seedu.insureconnect.testutil.TypicalPersons.ELLE;
import static seedu.insureconnect.testutil.TypicalPersons.FIONA;
import static seedu.insureconnect.testutil.TypicalPersons.getTypicalInsureConnect;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.insureconnect.logic.parser.FindCommandParser;
import seedu.insureconnect.logic.parser.exceptions.ParseException;
import seedu.insureconnect.model.Model;
import seedu.insureconnect.model.ModelManager;
import seedu.insureconnect.model.UserPrefs;
import seedu.insureconnect.model.person.NameContainsKeywordsPredicate;
import seedu.insureconnect.model.person.Person;

/**
 * Contains integration tests (interaction with the Model) for {@code FindCommand}.
 */
public class FindCommandTest {
    private Model model = new ModelManager(getTypicalInsureConnect(), new UserPrefs());
    private Model expectedModel = new ModelManager(getTypicalInsureConnect(), new UserPrefs());

    @Test
    public void equals() {
        NameContainsKeywordsPredicate firstPredicate =
                new NameContainsKeywordsPredicate(List.of("first"));
        NameContainsKeywordsPredicate secondPredicate =
                new NameContainsKeywordsPredicate(List.of("second"));

        FindCommand findFirstCommand = new FindCommand(firstPredicate);
        FindCommand findSecondCommand = new FindCommand(secondPredicate);

        // same object -> returns true
        assertTrue(findFirstCommand.equals(findFirstCommand));

        // same values -> returns true
        FindCommand findFirstCommandCopy = new FindCommand(firstPredicate);
        assertTrue(findFirstCommand.equals(findFirstCommandCopy));

        // different types -> returns false
        assertFalse(findFirstCommand.equals(1));

        // null -> returns false
        assertFalse(findFirstCommand.equals(null));

        // different person -> returns false
        assertFalse(findFirstCommand.equals(findSecondCommand));
    }

    @Test
    public void execute_zeroKeywords_noPersonFound() {
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 0);
        NameContainsKeywordsPredicate predicate = preparePredicate(" ");
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(List.of(), model.getFilteredPersonList());
    }

    @Test
    public void execute_multipleKeywords_multiplePersonsFound() {
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 3);
        NameContainsKeywordsPredicate predicate = preparePredicate("Kurz Elle Kunz");
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(List.of(CARL, ELLE, FIONA), model.getFilteredPersonList());
    }

    /**
     * Parsed mixed-case prefixes return the matching customer and count.
     */
    @Test
    public void execute_mixedCasePrefix_personFound() throws ParseException {
        assertPrefixSearch("aLi", List.of(ALICE));
    }

    /**
     * One prefix can match later name words belonging to multiple customers.
     */
    @Test
    public void execute_lastNamePrefix_multiplePersonsFound() throws ParseException {
        assertPrefixSearch("ku", List.of(CARL, FIONA));
    }

    /**
     * Whitespace, keyword order, and repeated prefixes preserve OR matching without duplicate results.
     */
    @Test
    public void execute_multiplePrefixes_uniquePersonsFound() throws ParseException {
        assertPrefixSearch("  EL\tku ku  ", List.of(CARL, ELLE, FIONA));
    }

    /**
     * Infixes, suffixes, and keywords longer than the name word produce no results.
     */
    @Test
    public void execute_nonPrefixKeywords_noPersonsFound() throws ParseException {
        assertPrefixSearch("lic line Alicee", List.of());
    }

    /**
     * A new search considers all customers instead of narrowing the previous results.
     */
    @Test
    public void execute_prefixAfterPreviousSearch_replacesFilter() throws ParseException {
        assertPrefixSearch("ali", List.of(ALICE));
        assertPrefixSearch("ku", List.of(CARL, FIONA));
    }

    /**
     * Checks parsed searches against explicit expected customers while preserving the underlying records.
     */
    private void assertPrefixSearch(String keywords, List<Person> expectedPersons) throws ParseException {
        FindCommand command = new FindCommandParser().parse(keywords);
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, expectedPersons.size());
        expectedModel.updateFilteredPersonList(expectedPersons::contains);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(expectedPersons, model.getFilteredPersonList());
        assertEquals(getTypicalInsureConnect(), model.getInsureConnect());
    }

    @Test
    public void toStringMethod() {
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(List.of("keyword"));
        FindCommand findCommand = new FindCommand(predicate);
        String expected = FindCommand.class.getCanonicalName() + "{predicate=" + predicate + "}";
        assertEquals(expected, findCommand.toString());
    }

    /**
     * Parses {@code userInput} into a {@code NameContainsKeywordsPredicate}.
     */
    private NameContainsKeywordsPredicate preparePredicate(String userInput) {
        return new NameContainsKeywordsPredicate(List.of(userInput.split("\\s+")));
    }
}
