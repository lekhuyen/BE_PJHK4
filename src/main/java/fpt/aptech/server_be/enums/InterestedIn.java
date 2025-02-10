package fpt.aptech.server_be.enums;

public enum InterestedIn {
    CONTACT("Contact"),
    FEEDBACK("Feedback"),
    REPORT("Report");

    private final String interestedIn;

    InterestedIn(String interestedIn) {
        this.interestedIn = interestedIn;
    }

    public String getInterestedIn() {
        return interestedIn;
    }
}
