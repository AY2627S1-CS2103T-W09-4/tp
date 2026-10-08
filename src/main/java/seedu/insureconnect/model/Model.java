package seedu.insureconnect.model;

import java.util.function.Predicate;

import javafx.collections.ObservableList;
import seedu.insureconnect.commons.core.GuiSettings;
import seedu.insureconnect.model.person.Person;

/**
 * The API of the Model component.
 */
public interface Model {
    /** {@code Predicate} that always evaluates to true */
    Predicate<Person> PREDICATE_SHOW_ALL_PERSONS = unused -> true;

    /**
     * Returns the user prefs.
     */
    ReadOnlyUserPrefs getUserPrefs();

    /**
     * Returns the user prefs' GUI settings.
     */
    GuiSettings getGuiSettings();

    /**
     * Sets the user prefs' GUI settings.
     */
    void setGuiSettings(GuiSettings guiSettings);

    /**
     * Replaces InsureConnect data with the data in {@code insureConnect}.
     */
    void setInsureConnect(ReadOnlyInsureConnect insureConnect);

    /** Returns InsureConnect */
    ReadOnlyInsureConnect getInsureConnect();

    /**
     * Returns true if a person with the same identity as {@code person} exists in InsureConnect.
     */
    boolean hasPerson(Person person);

    /**
     * Deletes the given person.
     * The person must exist in InsureConnect.
     */
    void deletePerson(Person target);

    /**
     * Adds the given person.
     * {@code person} must not already exist in InsureConnect.
     */
    void addPerson(Person person);

    /**
     * Replaces the given person {@code target} with {@code editedPerson}.
     * {@code target} must exist in InsureConnect.
     * The person identity of {@code editedPerson} must not be the same as another existing person in InsureConnect.
     */
    void setPerson(Person target, Person editedPerson);

    /** Returns an unmodifiable view of the filtered person list */
    ObservableList<Person> getFilteredPersonList();

    /**
     * Updates the filter of the filtered person list to filter by the given {@code predicate}.
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateFilteredPersonList(Predicate<Person> predicate);

    /**
     * Returns the current predicate of the filtered person list.
     */
    default Predicate<Person> getFilteredPersonListPredicate() {
        return PREDICATE_SHOW_ALL_PERSONS;
    }
}
