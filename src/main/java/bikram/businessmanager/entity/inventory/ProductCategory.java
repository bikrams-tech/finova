package bikram.businessmanager.entity.inventory;

public enum ProductCategory {

    FOOD("Food"),
    BEVERAGE("Beverage"),
    ELECTRONICS("Electronics"),
    STATIONERY("Stationery"),
    CLOTHING("Clothing"),
    MEDICINE("Medicine"),
    ALCOHOL("Alcohol"),
    CIGARETTE("Cigarette"),
    OTHER("Other");

    private final String displayName;

    ProductCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}