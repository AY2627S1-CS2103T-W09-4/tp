package seedu.insureconnect.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static seedu.insureconnect.testutil.TypicalPersons.ALICE;
import static seedu.insureconnect.testutil.TypicalPersons.BENSON;

import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import seedu.insureconnect.model.person.Person;

public class PersonListPanelTest extends GuiTest {

    @Test
    public void list_modelChanges_updatesDisplayedContacts() throws Exception {
        runOnFxThread(() -> {
            ObservableList<Person> persons = FXCollections.observableArrayList(ALICE);
            PersonListPanel panel = new PersonListPanel(persons);
            show(panel.getRoot());
            ListView<?> list = (ListView<?>) panel.getRoot().lookup("#personListView");
            assertEquals(List.of(ALICE), list.getItems());
            persons.setAll(BENSON, ALICE);
            assertEquals(List.of(BENSON, ALICE), list.getItems());
            list.getSelectionModel().select(1);
            assertEquals(ALICE, list.getSelectionModel().getSelectedItem());
            persons.clear();
            assertEquals(List.of(), list.getItems());
            assertNull(list.getSelectionModel().getSelectedItem());
        });
    }

    @Test
    public void cell_reusedForAnotherPerson_replacesDetailsAndIndex() throws Exception {
        runOnFxThread(() -> {
            PersonListPanel panel = new PersonListPanel(FXCollections.observableArrayList());
            PersonListPanel.PersonListViewCell cell = panel.new PersonListViewCell();
            cell.updateIndex(0);
            cell.updateItem(ALICE, false);
            assertEquals(ALICE.getName().fullName, ((Label) cell.getGraphic().lookup("#name")).getText());
            cell.updateIndex(2);
            cell.updateItem(BENSON, false);
            assertEquals(BENSON.getName().fullName, ((Label) cell.getGraphic().lookup("#name")).getText());
            assertEquals("3. ", ((Label) cell.getGraphic().lookup("#id")).getText());
        });
    }

    @Test
    public void cell_becomesEmptyOrNull_removesStaleContactDetails() throws Exception {
        runOnFxThread(() -> {
            PersonListPanel panel = new PersonListPanel(FXCollections.observableArrayList());
            PersonListPanel.PersonListViewCell cell = panel.new PersonListViewCell();
            cell.updateItem(ALICE, false);
            assertNotNull(cell.getGraphic());
            cell.updateItem(ALICE, true);
            assertNull(cell.getGraphic());
            assertNull(cell.getText());
            cell.updateItem(BENSON, false);
            cell.updateItem(null, false);
            assertNull(cell.getGraphic());
            assertNull(cell.getText());
        });
    }
}
