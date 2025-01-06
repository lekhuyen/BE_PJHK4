package fpt.aptech.server_be.enums;

public enum CountryCode {
    US("+1"),
    UK("+44"),
    INDIA("+91");

    private final String code;

    CountryCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
