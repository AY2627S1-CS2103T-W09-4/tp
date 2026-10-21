package seedu.insureconnect.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.insureconnect.testutil.TypicalPersons.ALICE;
import static seedu.insureconnect.testutil.TypicalPersons.BENSON;

import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import seedu.insureconnect.model.person.Person;

public class PersonListPanelTest extends GuiTest {

    @Test
    public void list_modelChanges_rebuildsDisplayedCardsAndIndices() throws Exception {
        runOnFxThread(() -> {
            ObservableList<Person> persons = FXCollections.observableArrayList(ALICE);
            PersonListPanel panel = new PersonListPanel(persons);
            show(panel.getRoot());
            GridPane cards = cardsOf(panel);

            assertEquals(List.of(ALICE.getName().fullName), namesOf(cards));
            assertEquals(List.of("01"), idsOf(cards));

            persons.setAll(BENSON, ALICE);
            assertEquals(List.of(BENSON.getName().fullName, ALICE.getName().fullName), namesOf(cards));
            assertEquals(List.of("01", "02"), idsOf(cards));

            persons.clear();
            assertEquals(List.of(), cards.getChildren());
        });
    }

    @Test
    public void layout_viewportResizes_reflowsCardsBetweenOneAndTwoColumns() throws Exception {
        runOnFxThread(() -> {
            PersonListPanel panel = new PersonListPanel(FXCollections.observableArrayList(ALICE, BENSON));
            Stage stage = show(panel.getRoot());
            GridPane cards = cardsOf(panel);
            ScrollPane scroll = (ScrollPane) panel.getRoot().lookup("#personScrollPane");

            stage.setWidth(700);
            resizeAndLayout(panel, scroll, 700);
            assertEquals(0, columnOf(cards.getChildren().get(0)));
            assertEquals(0, columnOf(cards.getChildren().get(1)));

            stage.setWidth(1200);
            resizeAndLayout(panel, scroll, 1200);
            assertEquals(0, columnOf(cards.getChildren().get(0)));
            assertEquals(1, columnOf(cards.getChildren().get(1)), () -> "viewport="
                    + scroll.getViewportBounds().getWidth() + ", root=" + panel.getRoot().getWidth()
                    + ", scroll=" + scroll.getWidth());

            stage.setWidth(700);
            resizeAndLayout(panel, scroll, 700);
            assertEquals(0, columnOf(cards.getChildren().get(0)));
            assertEquals(0, columnOf(cards.getChildren().get(1)));
        });
    }

    private GridPane cardsOf(PersonListPanel panel) {
        return (GridPane) panel.getRoot().lookup("#personCards");
    }

    private List<String> namesOf(GridPane cards) {
        return cards.getChildren().stream().map(card -> labelOf(card, "name").getText()).toList();
    }

    private List<String> idsOf(GridPane cards) {
        return cards.getChildren().stream().map(card -> labelOf(card, "id").getText()).toList();
    }

    private Label labelOf(Node card, String id) {
        return (Label) card.lookup("#" + id);
    }

    private int columnOf(Node card) {
        Integer column = GridPane.getColumnIndex(card);
        return column == null ? 0 : column;
    }

    private void resizeAndLayout(PersonListPanel panel, ScrollPane scroll, double width) {
        panel.getRoot().resize(width, panel.getRoot().getHeight());
        panel.getRoot().applyCss();
        panel.getRoot().layout();
        scroll.applyCss();
        scroll.layout();
    }
}
