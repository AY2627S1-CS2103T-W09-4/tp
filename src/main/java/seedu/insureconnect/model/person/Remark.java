package seedu.insureconnect.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.insureconnect.commons.util.AppUtil.checkArgument;

/**
 * Represents an optional free-text remark attached to a customer.
 * Guarantees: value is at most 500 characters long.
 */
public class Remark {
    public static final String MESSAGE_CONSTRAINTS = "Remarks should be at most 500 characters long.";
    public static final int MAX_LENGTH = 500;

    public final String value;

    /**
     * Constructs a {@code Remark}
     * @param remark A valid remark, possibly empty;
     */
    public Remark(String remark) {
        requireNonNull(remark);
        checkArgument(isValidRemark(remark), MESSAGE_CONSTRAINTS);
        value = remark;
    }

    /**
     * Returns true if the given remark is at most {@value #MAX_LENGTH} characters long.
     */
    public static boolean isValidRemark(String test) {
        requireNonNull(test);
        return test.length() <= MAX_LENGTH;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Remark otherRemark)) {
            return false;
        }
        return value.equals(otherRemark.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value;
    }
}
