package bikram.businessmanager.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubAccountDto {

    private Long id;
    private String accountName;     // parent account
    private String subAccountName;  // this subaccount
}
