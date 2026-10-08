package seedu.insureconnect.model;

import static java.util.Objects.requireNonNull;
import static seedu.insureconnect.commons.util.CollectionUtil.requireAllNonNull;

import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import seedu.insureconnect.commons.core.GuiSettings;
import seedu.insureconnect.commons.core.LogsCenter;
import seedu.insureconnect.model.person.Person;

/**
 * Represents the in-memory model of InsureConnect data.
 */
public class ModelManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final InsureConnect insureConnect;
    private final UserPrefs userPrefs;
    private final FilteredList<Person> filteredPersons;

    /**
     * Initializes a ModelManager with the given insureConnect and userPrefs.
     */
    public ModelManager(ReadOnlyInsureConnect insureConnect, ReadOnlyUserPrefs userPrefs) {
        requireAllNonNull(insureConnect, userPrefs);

        logger.fine("Initializing with InsureConnect: " + insureConnect + " and user prefs " + userPrefs);

        this.insureConnect = new InsureConnect(insureConnect);
        this.userPrefs = new UserPrefs(userPrefs);
        filteredPersons = new FilteredList<>(this.insureConnect.getPersonList());
    }

    public ModelManager() {
        this(new InsureConnect(), new UserPrefs());
    }

    //=========== UserPrefs ==================================================================================

    @Override
    public ReadOnlyUserPrefs getUserPrefs() {
        return userPrefs;
    }

    @Override
    public GuiSettings getGuiSettings() {
        return userPrefs.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        requireNonNull(guiSettings);
        userPrefs.setGuiSettings(guiSettings);
    }

    //=========== InsureConnect ================================================================================

    @Override
    public void setInsureConnect(ReadOnlyInsureConnect insureConnect) {
        this.insureConnect.resetData(insureConnect);
    }

    @Override
    public ReadOnlyInsureConnect getInsureConnect() {
        return insureConnect;
    }

    @Override
    public boolean hasPerson(Person person) {
        requireNonNull(person);
        return insureConnect.hasPerson(person);
    }

    @Override
    public void deletePerson(Person target) {
        insureConnect.removePerson(target);
    }

    @Override
    public void addPerson(Person person) {
        insureConnect.addPerson(person);
        updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
    }

    @Override
    public void setPerson(Person target, Person editedPerson) {
        requireAllNonNull(target, editedPerson);

        insureConnect.setPerson(target, editedPerson);
    }

    //=========== Filtered Person List Accessors =============================================================

    /**
     * Returns an unmodifiable view of the list of {@code Person} backed by the internal list of
     * {@code insureConnect}
     */
    @Override
    public ObservableList<Person> getFilteredPersonList() {
        return filteredPersons;
    }

    @Override
    public void updateFilteredPersonList(Predicate<Person> predicate) {
        requireNonNull(predicate);
        filteredPersons.setPredicate(predicate);
    }

    @Override
    public Predicate<Person> getFilteredPersonListPredicate() {
        @SuppressWarnings("unchecked")
        Predicate<Person> predicate = (Predicate<Person>) filteredPersons.getPredicate();
        return predicate != null ? predicate : PREDICATE_SHOW_ALL_PERSONS;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ModelManager otherModelManager)) {
            return false;
        }

        return insureConnect.equals(otherModelManager.insureConnect)
                && userPrefs.equals(otherModelManager.userPrefs)
                && filteredPersons.equals(otherModelManager.filteredPersons);
    }

}
