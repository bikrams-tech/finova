package bikram.businessmanager.repository;

import bikram.businessmanager.entity.inventory.Purchase;

public class PurchaseRepository extends BaseRepository<Purchase>{
    public PurchaseRepository(){
        super(Purchase.class);
    }

}
