package bikram.businessmanager.entity.account;

public enum AccountType {
    ASSET(true,"1000"),
    LIABILITY(false,"2000"),
    EQUITY(false,"3000"),
    REVENUE(false,"4000"),
    EXPENSE(true,"5000");

    private final boolean debitNormal;
    private final String baseCode;

    AccountType(boolean debitNormal, String baseCode) {
        this.debitNormal = debitNormal;
        this.baseCode = baseCode;
    }

    public boolean isDebitNormal() {
        return debitNormal;
    }
    public String getBaseCode() {return baseCode; }
}
