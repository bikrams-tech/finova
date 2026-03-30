package bikram.businessmanager.dto;

import bikram.businessmanager.entity.account.AccountCategory;

public record SubAccountDtoWithAcount(
        Long id,
        String subAccountName,
        String mainAccountName,
        AccountCategory accountCategory,
        String code
) {
}
