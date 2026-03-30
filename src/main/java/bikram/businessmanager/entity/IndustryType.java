package bikram.businessmanager.entity;

import lombok.Getter;

@Getter
public enum IndustryType {
    AGRICULTURE("Agriculture"),
    MANUFACTURING("Manufacturing"),
    IT("Information Technology"),
    SERVICES("Services"),
    TRADING("Trading"),
    EDUCATION("Education"),
    HEALTHCARE("Healthcare"),
    CONSTRUCTION("Construction"),
    OTHER("Other");

    private final String displayName;

    IndustryType(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}