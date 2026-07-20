package model;

/**
 * Types of hints that can be revealed after a number of failed attempts.
 */
public enum HintType {
    ODD_EVEN    ("Odd / Even",        "Is the number odd or even?"),
    PRIME       ("Prime Check",       "Is the number prime or composite?"),
    MULTIPLE_5  ("Multiple of 5",     "Is the number a multiple of 5?"),
    RANGE_NARROW("Narrowed Range",    "The number lies between two values.");

    private final String label;
    private final String question;

    HintType(String label, String question) {
        this.label    = label;
        this.question = question;
    }

    public String getLabel()    { return label; }
    public String getQuestion() { return question; }

    @Override
    public String toString() { return label; }
}
