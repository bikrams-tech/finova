package bikram.businessmanager.entity.account;


public enum AccountGroup {

    CASH(AccountType.ASSET, "1100"),
    BANK(AccountType.ASSET, "1200"),
    INVENTORY(AccountType.ASSET, "1300"),

    ACCOUNTS_PAYABLE(AccountType.LIABILITY, "2100"),
    LOAN(AccountType.LIABILITY, "2200"),

    CAPITAL(AccountType.EQUITY, "3100"),

    SALES(AccountType.REVENUE, "4100"),

    SALARY(AccountType.EXPENSE, "5100"),
    RENT(AccountType.EXPENSE, "5200");

    private final AccountType accountType;
    private final String groupCode;

    AccountGroup(AccountType accountType, String groupCode) {
        this.accountType = accountType;
        this.groupCode = groupCode;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public String getGroupCode() {
        return groupCode;
    }
}
