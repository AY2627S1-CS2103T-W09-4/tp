package seedu.insureconnect.testutil;

import seedu.insureconnect.model.InsureConnect;
import seedu.insureconnect.model.person.Person;

/**
 * A utility class to help with building InsureConnect objects.
 * Example usage: <br>
 *     {@code InsureConnect ab = new InsureConnectBuilder().withPerson("John", "Doe").build();}
 */
public class InsureConnectBuilder {

    private InsureConnect insureConnect;

    public InsureConnectBuilder() {
        insureConnect = new InsureConnect();
    }

    public InsureConnectBuilder(InsureConnect insureConnect) {
        this.insureConnect = insureConnect;
    }

    /**
     * Adds a new {@code Person} to the {@code InsureConnect} that we are building.
     */
    public InsureConnectBuilder withPerson(Person person) {
        insureConnect.addPerson(person);
        return this;
    }

    public InsureConnect build() {
        return insureConnect;
    }
}
