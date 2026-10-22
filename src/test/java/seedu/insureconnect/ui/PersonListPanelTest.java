package seedu.insureconnect.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.insureconnect.testutil.TypicalPersons.ALICE;
import static seedu.insureconnect.testutil.TypicalPersons.BENSON;

import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;
import seedu.insureconnect.model.person.Person;

public class PersonListPanelTest extends GuiTest {

    private static final double CARD_WIDTH_MARGIN = 28;

    @Test
    public void constructor_nonEmptyList_displaysItemsSelectsFirstAndExposesFocusTarget() throws Exception {
        runOnFxThread(() -> {
            ObservableList<Person> persons = FXCollections.observableArrayList(ALICE, BENSON);
            PersonListPanel panel = new PersonListPanel(persons);
            ListView<Person> listView = listViewOf(panel);

            assertSame(persons, listView.getItems());
            assertEquals(ALICE, panel.getSelectedPerson());
            assertEquals(ALICE, panel.selectedPersonProperty().get());
            assertSame(listView, panel.getFocusTarget());
        });
    }

    @Test
    public void list_modelChanges_updatesItemsAndMaintainsSensibleSelection() throws Exception {
        runOnFxThread(() -> {
            ObservableList<Person> persons = FXCollections.observableArrayList(ALICE, BENSON);
            PersonListPanel panel = new PersonListPanel(persons);
            ListView<Person> listView = listViewOf(panel);

            listView.getSelectionModel().select(BENSON);
            persons.remove(BENSON);

            assertEquals(List.of(ALICE), listView.getItems());
            assertEquals(ALICE, panel.getSelectedPerson());

            persons.clear();
            assertTrue(listView.getItems().isEmpty());
            assertNull(panel.getSelectedPerson());

            persons.addAll(BENSON, ALICE);
            assertEquals(List.of(BENSON, ALICE), listView.getItems());
            assertEquals(BENSON, panel.getSelectedPerson());
        });
    }

    @Test
    public void layout_listWidthChanges_cardWidthTracksAvailableSpace() throws Exception {
        runOnFxThread(() -> {
            PersonListPanel panel = new PersonListPanel(FXCollections.observableArrayList(ALICE));
            show(panel.getRoot());
            ListView<Person> listView = listViewOf(panel);
            Region card = visibleCardFor(listView, ALICE);

            assertTrue(card.prefWidthProperty().isBound());
            assertEquals(listView.getWidth() - CARD_WIDTH_MARGIN, card.getPrefWidth(), 0.01);

            listView.resize(500, listView.getHeight());
            assertEquals(500 - CARD_WIDTH_MARGIN, card.getPrefWidth(), 0.01);
        });
    }

    @Test
    public void cell_nullItem_clearsPreviousGraphic() throws Exception {
        runOnFxThread(() -> {
            ObservableList<Person> persons = FXCollections.observableArrayList(ALICE);
            PersonListPanel panel = new PersonListPanel(persons);
            ListView<Person> list = listViewOf(panel);
            ListCell<Person> cell = list.getCellFactory().call(list);
            cell.updateListView(list);
            cell.updateIndex(0);
            Region previousGraphic = (Region) cell.getGraphic();
            assertTrue(previousGraphic.prefWidthProperty().isBound());
            persons.set(0, null);
            assertNull(cell.getGraphic());
            assertNull(cell.getText());
            assertFalse(previousGraphic.prefWidthProperty().isBound());
        });
    }

    @SuppressWarnings("unchecked")
    private ListView<Person> listViewOf(PersonListPanel panel) {
        return (ListView<Person>) panel.getRoot().lookup("#personListView");
    }

    private Region visibleCardFor(ListView<Person> listView, Person person) {
        for (Node node : listView.lookupAll(".list-cell")) {
            if (node instanceof ListCell<?> cell && person.equals(cell.getItem())) {
                return (Region) cell.getGraphic();
            }
        }
        throw new AssertionError("No visible card found for " + person.getName().fullName);
    }
}
