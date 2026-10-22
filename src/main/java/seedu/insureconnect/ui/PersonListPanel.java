package seedu.insureconnect.ui;

import static java.util.Objects.requireNonNull;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;
import seedu.insureconnect.model.person.Person;

/**
 * Panel containing the displayed list of customers.
 */
public class PersonListPanel extends UiPart<Region> {

    private static final String FXML = "PersonListPanel.fxml";
    private static final double CARD_WIDTH_MARGIN = 28;

    @FXML
    private ListView<Person> personListView;
    private int lastSelectedIndex;

    /**
     * Creates a {@code PersonListPanel} using the given list of customers.
     */
    public PersonListPanel(ObservableList<Person> personList) {
        super(FXML);
        requireNonNull(personList);

        personListView.setItems(personList);
        personListView.setCellFactory(PersonListViewCell::new);

        personListView.getSelectionModel()
                .selectedIndexProperty()
                .addListener((observable, oldIndex, newIndex) -> {
                    if (newIndex.intValue() >= 0) {
                        lastSelectedIndex = newIndex.intValue();
                    }
                });

        personList.addListener((ListChangeListener<Person>) change ->
                handlePersonListChanged(personList));

        if (!personList.isEmpty()) {
            personListView.getSelectionModel().selectFirst();
        }
    }

    /**
     * Returns the selected customer property.
     */
    ReadOnlyObjectProperty<Person> selectedPersonProperty() {
        return personListView.getSelectionModel().selectedItemProperty();
    }

    /**
     * Returns the currently selected customer.
     */
    Person getSelectedPerson() {
        return personListView.getSelectionModel().getSelectedItem();
    }

    /**
     * Returns the control that receives keyboard focus for this area.
     */
    Node getFocusTarget() {
        return personListView;
    }

    /**
     * Refreshes the displayed cards and maintains a sensible selection.
     */
    private void handlePersonListChanged(ObservableList<Person> personList) {
        personListView.refresh();

        if (personList.isEmpty()) {
            personListView.getSelectionModel().clearSelection();
            return;
        }

        if (personListView.getSelectionModel().getSelectedItem() == null) {
            int indexToSelect = Math.min(lastSelectedIndex, personList.size() - 1);
            personListView.getSelectionModel().select(indexToSelect);
        }
    }

    /**
     * A list cell that renders a customer using a {@link PersonCard}.
     */
    private static class PersonListViewCell extends ListCell<Person> {

        private final ListView<Person> listView;
        private Region cardRoot;

        private PersonListViewCell(ListView<Person> listView) {
            this.listView = listView;
        }

        @Override
        protected void updateItem(Person person, boolean empty) {
            super.updateItem(person, empty);

            clearPreviousCard();

            if (empty || person == null) {
                setText(null);
                setGraphic(null);
                return;
            }

            PersonCard card = new PersonCard(person, getIndex() + 1);
            cardRoot = card.getRoot();

            cardRoot.setMinWidth(0);
            cardRoot.prefWidthProperty().bind(
                    Bindings.max(
                            listView.widthProperty().subtract(CARD_WIDTH_MARGIN),
                            0));
            cardRoot.setMaxWidth(Region.USE_PREF_SIZE);

            setText(null);
            setGraphic(cardRoot);
        }

        /**
         * Removes the width binding from the previous card when this cell is reused.
         */
        private void clearPreviousCard() {
            if (cardRoot != null) {
                cardRoot.prefWidthProperty().unbind();
                cardRoot = null;
            }
        }
    }
}
