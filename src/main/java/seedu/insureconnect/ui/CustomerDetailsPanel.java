package seedu.insureconnect.ui;

import static java.util.Objects.requireNonNull;

import java.util.Comparator;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import seedu.insureconnect.model.person.Person;

/**
 * Displays the full details of the currently selected customer.
 */
public class CustomerDetailsPanel extends UiPart<Region> {

    private static final String FXML = "CustomerDetailsPanel.fxml";

    @FXML
    private VBox emptyState;

    @FXML
    private ScrollPane detailsScrollPane;

    @FXML
    private SelectableText detailName;

    @FXML
    private SelectableText detailPhone;

    @FXML
    private SelectableText detailEmail;

    @FXML
    private SelectableText detailAddress;

    @FXML
    private FlowPane detailPolicies;

    @FXML
    private VBox detailRemarkSection;

    @FXML
    private SelectableText detailRemark;

    /**
     * Creates an empty customer details panel.
     */
    public CustomerDetailsPanel() {
        super(FXML);
        clear();
    }

    /**
     * Displays the details of the given customer.
     */
    public void displayCustomer(Person customer) {
        requireNonNull(customer);

        detailName.setText(customer.getName().fullName);
        detailPhone.setText(customer.getPhone().value);
        detailEmail.setText(customer.getEmail().value);
        detailAddress.setText(customer.getAddress().value);

        detailPolicies.getChildren().clear();
        customer.getTags().stream()
                .sorted(Comparator.comparing(tag -> tag.tagName))
                .forEach(tag -> {
                    Label policyLabel = new Label(tag.tagName);
                    policyLabel.getStyleClass().add("policy-chip");
                    detailPolicies.getChildren().add(policyLabel);
                });

        String remarkText = customer.getRemark().value;
        detailRemark.setText(remarkText);

        boolean hasRemark = !remarkText.isBlank();
        detailRemarkSection.setVisible(hasRemark);
        detailRemarkSection.setManaged(hasRemark);

        emptyState.setVisible(false);
        emptyState.setManaged(false);

        detailsScrollPane.setVisible(true);
        detailsScrollPane.setManaged(true);
    }

    /**
     * Clears the displayed customer and shows the empty state.
     */
    public void clear() {
        detailName.setText("");
        detailPhone.setText("");
        detailEmail.setText("");
        detailAddress.setText("");
        detailRemark.setText("");
        detailPolicies.getChildren().clear();

        detailRemarkSection.setVisible(false);
        detailRemarkSection.setManaged(false);

        detailsScrollPane.setVisible(false);
        detailsScrollPane.setManaged(false);

        emptyState.setVisible(true);
        emptyState.setManaged(true);
    }
}
