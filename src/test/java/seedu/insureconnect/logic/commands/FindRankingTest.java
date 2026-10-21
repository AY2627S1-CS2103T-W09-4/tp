package seedu.insureconnect.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.insureconnect.logic.Messages.MESSAGE_PERSONS_LISTED_OVERVIEW;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.insureconnect.commons.core.index.Index;
import seedu.insureconnect.logic.commands.exceptions.CommandException;
import seedu.insureconnect.logic.parser.FindCommandParser;
import seedu.insureconnect.logic.parser.exceptions.ParseException;
import seedu.insureconnect.model.InsureConnect;
import seedu.insureconnect.model.Model;
import seedu.insureconnect.model.ModelManager;
import seedu.insureconnect.model.UserPrefs;
import seedu.insureconnect.model.person.Person;
import seedu.insureconnect.model.person.Phone;
import seedu.insureconnect.testutil.PersonBuilder;

/**
 * Verifies search ranking and commands that operate on the ranked display.
 */
public class FindRankingTest {
    private final Person johnny = new PersonBuilder().withName("Johnny Lim").withTags("P1").build();
    private final Person mary = new PersonBuilder().withName("Mary John").withTags("P2").build();
    private final Person johnson = new PersonBuilder().withName("Alice Johnson").withTags("P3").build();
    private final Person john = new PersonBuilder().withName("John Tan").withTags("P4").build();
    private final Person bob = new PersonBuilder().withName("Bob Lee").withTags("P5").build();
    private final List<Person> storedPersons = List.of(johnny, mary, johnson, john, bob);
    private Model model;

    /**
     * Places prefix-only matches before exact matches in storage to expose incorrect ranking.
     */
    @BeforeEach
    public void setUp() {
        InsureConnect data = new InsureConnect();
        data.setPersons(storedPersons);
        model = new ModelManager(data, new UserPrefs());
    }

    /**
     * Exact words lead the results, and both groups retain their original relative order.
     */
    @Test
    public void execute_exactAndPrefixMatches_ranksExactFirst() throws ParseException {
        assertSearch("jOhN", List.of(mary, john, johnny, johnson));
        assertEquals(storedPersons, model.getInsureConnect().getPersonList());
    }

    /**
     * Any exact keyword qualifies, and duplicate keywords do not duplicate customers.
     */
    @Test
    public void execute_multipleKeywords_ranksAnyExactMatchFirst() throws ParseException {
        assertSearch("john alice john", List.of(mary, johnson, john, johnny));
    }

    /**
     * Ranking overrides previous alphabetical sorting but not the underlying stored order.
     */
    @Test
    public void execute_afterSortedList_usesMatchRank() throws ParseException {
        new ListCommand(ListCommand.SortOrder.NAME).execute(model);
        assertSearch("john", List.of(mary, john, johnny, johnson));
    }

    /**
     * Each search replaces the previous rank and considers the complete customer list.
     */
    @Test
    public void execute_repeatedSearch_replacesRanking() throws ParseException {
        assertSearch("john", List.of(mary, john, johnny, johnson));
        assertSearch("jo", List.of(johnny, mary, johnson, john));
        assertSearch("bob", List.of(bob));
        assertSearch("nobody", List.of());
    }

    /**
     * Listing clears search ranking and restores stored or explicitly requested order.
     */
    @Test
    public void execute_listAfterSearch_restoresListOrder() throws ParseException {
        assertSearch("john", List.of(mary, john, johnny, johnson));
        new ListCommand().execute(model);
        assertEquals(storedPersons, model.getFilteredPersonList());
        new ListCommand(ListCommand.SortOrder.NAME).execute(model);
        assertEquals(List.of(johnson, bob, john, johnny, mary), model.getFilteredPersonList());
    }

    /**
     * Deletion uses the displayed rank, not the original storage index.
     */
    @Test
    public void execute_deleteRankedResult_deletesDisplayedCustomer() throws ParseException, CommandException {
        assertSearch("john", List.of(mary, john, johnny, johnson));
        new DeleteCommand(Index.fromOneBased(1)).confirmed().execute(model);
        assertFalse(model.hasPerson(mary));
        assertTrue(model.hasPerson(johnny));
        assertEquals(List.of(john, johnny, johnson), model.getFilteredPersonList());
    }

    /**
     * Editing uses the displayed rank without modifying another customer.
     */
    @Test
    public void execute_editRankedResult_editsDisplayedCustomer() throws ParseException, CommandException {
        assertSearch("john", List.of(mary, john, johnny, johnson));
        EditCommand.EditPersonDescriptor descriptor = new EditCommand.EditPersonDescriptor();
        descriptor.setPhone(new Phone("98765432"));
        new EditCommand(Index.fromOneBased(1), descriptor).execute(model);
        assertTrue(model.hasPerson(new PersonBuilder(mary).withPhone("98765432").build()));
        assertFalse(model.hasPerson(mary));
        assertTrue(model.hasPerson(johnny));
    }

    /**
     * Checks the complete displayed order and reported count using a parsed search command.
     */
    private void assertSearch(String query, List<Person> expectedPersons) throws ParseException {
        CommandResult result = new FindCommandParser().parse(query).execute(model);
        assertEquals(expectedPersons, model.getFilteredPersonList());
        assertEquals(String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, expectedPersons.size()),
                result.getFeedbackToUser());
    }
}
