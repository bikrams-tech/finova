package bikram.businessmanager.utils;

import bikram.businessmanager.entity.Company;

public class SessionContext {

    private static Company currentCompany;

    private SessionContext() {} // prevent instantiation

    public static void setCurrentCompany(Company company) {
        currentCompany = company;

        // 🔥 persist automatically when set
        if (company != null) {
            SessionPersistence.saveCompanyId(company.getId());
        }
    }

    public static Company getCurrentCompany() {
        return currentCompany;
    }

    public static Long getCurrentCompanyId() {
        return currentCompany != null ? currentCompany.getId() : null;
    }

    public static void clear() {
        currentCompany = null;
        SessionPersistence.saveCompanyId(null);
    }
}