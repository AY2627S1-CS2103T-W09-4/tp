package seedu.insureconnect.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.insureconnect.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static seedu.insureconnect.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.insureconnect.testutil.Assert.assertThrows;
import static seedu.insureconnect.testutil.TypicalPersons.ALICE;
import static seedu.insureconnect.testutil.TypicalPersons.getTypicalInsureConnect;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.insureconnect.model.person.Person;
import seedu.insureconnect.model.person.exceptions.DuplicatePersonException;
import seedu.insureconnect.testutil.PersonBuilder;

public class InsureConnectTest {

    private final InsureConnect insureConnect = new InsureConnect();

    @Test
    public void constructor() {
        assertEquals(List.of(), insureConnect.getPersonList());
    }

    @Test
    public void resetData_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> insureConnect.resetData(null));
    }

    @Test
    public void resetData_withValidReadOnlyInsureConnect_replacesData() {
        InsureConnect newData = getTypicalInsureConnect();
        insureConnect.resetData(newData);
        assertEquals(newData, insureConnect);
    }

    @Test
    public void resetData_withDuplicatePersons_throwsDuplicatePersonException() {
        // Two persons with the same identity fields
        Person editedAlice = new PersonBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).withTags(VALID_TAG_HUSBAND)
                .build();
        List<Person> newPersons = List.of(ALICE, editedAlice);
        InsureConnectStub newData = new InsureConnectStub(newPersons);

        assertThrows(DuplicatePersonException.class, () -> insureConnect.resetData(newData));
    }

    @Test
    public void hasPerson_nullPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> insureConnect.hasPerson(null));
    }

    @Test
    public void hasPerson_personNotInInsureConnect_returnsFalse() {
        assertFalse(insureConnect.hasPerson(ALICE));
    }

    @Test
    public void hasPerson_personInInsureConnect_returnsTrue() {
        insureConnect.addPerson(ALICE);
        assertTrue(insureConnect.hasPerson(ALICE));
    }

    @Test
    public void hasPerson_personWithSameIdentityFieldsInInsureConnect_returnsTrue() {
        insureConnect.addPerson(ALICE);
        Person editedAlice = new PersonBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).withTags(VALID_TAG_HUSBAND)
                .build();
        assertTrue(insureConnect.hasPerson(editedAlice));
    }

    @Test
    public void getPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> insureConnect.getPersonList().remove(0));
    }

    @Test
    public void toStringMethod() {
        String expected = InsureConnect.class.getCanonicalName() + "{persons=" + insureConnect.getPersonList() + "}";
        assertEquals(expected, insureConnect.toString());
    }

    /**
     * A stub ReadOnlyInsureConnect whose persons list can violate interface constraints.
     */
    private static class InsureConnectStub implements ReadOnlyInsureConnect {
        private final ObservableList<Person> persons = FXCollections.observableArrayList();

        InsureConnectStub(Collection<Person> persons) {
            this.persons.setAll(persons);
        }

        @Override
        public ObservableList<Person> getPersonList() {
            return persons;
        }
    }

}
