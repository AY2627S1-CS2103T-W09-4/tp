package seedu.insureconnect.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.insureconnect.commons.util.AppUtil.checkArgument;

/**
 * Represents an optional free-text remark attached to a customer.
 * Contains at most 500 of any character.
 */
public class Remark {
    public static final String MESSAGE_CONSTRAINTS = "Remarks should be at most 500 characters long.";
    public static final int MAX_LENGTH = 500;

    public final String value;

    /**
     * Constructs a {@code Remark
     * }
     * @param remark A valid remark, possibly empty;
     */
    public Remark(String remark) {
        requireNonNull(remark);
        checkArgument(isValidRemark(remark), MESSAGE_CONSTRAINTS);
        value = remark;
    }

    /**
     * returns true if the remark is no longer than {@value MAX_LENGTH}
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

}
