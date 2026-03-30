package bikram.businessmanager.entity;

import lombok.Getter;

@Getter
public enum CompanyType {
    PVT_LTD("Private Limited"),
    PARTNERSHIP("Partnership"),
    SOLE_PROPRIETOR("Sole Proprietor");

    private final String displayName;

    CompanyType(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}