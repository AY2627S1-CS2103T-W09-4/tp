package seedu.insureconnect.model;

import javafx.collections.ObservableList;
import seedu.insureconnect.model.person.Person;

/**
 * Unmodifiable view of an address book
 */
public interface ReadOnlyInsureConnect {

    /**
     * Returns an unmodifiable view of the persons list.
     * This list will not contain any duplicate persons.
     */
    ObservableList<Person> getPersonList();

}
