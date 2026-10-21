package seedu.insureconnect.ui;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import seedu.insureconnect.model.person.Person;

/**
 * Panel containing the responsive grid of persons.
 */
public class PersonListPanel extends UiPart<Region> {
    private static final String FXML = "PersonListPanel.fxml";
    private static final double MIN_CARD_WIDTH = 390;
    private static final double CARD_GAP = 14;
    private static final double GRID_HORIZONTAL_PADDING = 24;

    @FXML
    private ScrollPane personScrollPane;
    @FXML
    private GridPane personCards;

    private final ObservableList<Person> personList;
    private int columnCount = 1;

    /**
     * Creates a {@code PersonListPanel} with the given {@code ObservableList}.
     */
    public PersonListPanel(ObservableList<Person> personList) {
        super(FXML);
        this.personList = personList;
        personScrollPane.setFitToWidth(true);
        personScrollPane.setFocusTraversable(true);
        personCards.setMinWidth(0);
        personCards.setMaxWidth(Double.MAX_VALUE);
        personCards.setHgap(CARD_GAP);
        personCards.setVgap(CARD_GAP);
        personScrollPane.viewportBoundsProperty().addListener((observable, oldBounds, newBounds) -> {
            updateCardWidths(newBounds);
        });
        personList.addListener((ListChangeListener<Person>) change -> refreshCards());
        refreshCards();
    }

    /**
     * Returns the control that receives keyboard focus for this area.
     */
    Node getFocusTarget() {
        return personScrollPane;
    }

    /**
     * Rebuilds the cards so each displayed index matches the current filtered list.
     */
    private void refreshCards() {
        personCards.getChildren().clear();
        for (int index = 0; index < personList.size(); index++) {
            PersonCard card = new PersonCard(personList.get(index), index + 1);
            card.getRoot().setMinWidth(0);
            GridPane.setHgrow(card.getRoot(), Priority.ALWAYS);
            personCards.add(card.getRoot(), index % columnCount, index / columnCount);
        }
        updateCardWidths(personScrollPane.getViewportBounds());
    }

    /**
     * Gives each card the available width for two columns, or the full width for one column.
     */
    private void updateCardWidths(Bounds viewportBounds) {
        double availableWidth = viewportBounds.getWidth() - GRID_HORIZONTAL_PADDING;
        if (availableWidth <= 0) {
            return;
        }
        boolean useTwoColumns = availableWidth >= 2 * MIN_CARD_WIDTH + CARD_GAP;
        int newColumnCount = useTwoColumns ? 2 : 1;
        personCards.setPrefWidth(viewportBounds.getWidth());
        if (newColumnCount != columnCount) {
            columnCount = newColumnCount;
            personCards.getColumnConstraints().clear();
            for (int column = 0; column < columnCount; column++) {
                ColumnConstraints constraints = new ColumnConstraints();
                constraints.setPercentWidth(100.0 / columnCount);
                constraints.setHgrow(Priority.ALWAYS);
                constraints.setMinWidth(0);
                personCards.getColumnConstraints().add(constraints);
            }
            for (int index = 0; index < personCards.getChildren().size(); index++) {
                Node card = personCards.getChildren().get(index);
                GridPane.setColumnIndex(card, index % columnCount);
                GridPane.setRowIndex(card, index / columnCount);
            }
        }
        double cardWidth = (availableWidth - (columnCount - 1) * CARD_GAP) / columnCount;
        for (Node card : personCards.getChildren()) {
            Region cardRegion = (Region) card;
            cardRegion.setPrefWidth(cardWidth);
        }
    }
}
