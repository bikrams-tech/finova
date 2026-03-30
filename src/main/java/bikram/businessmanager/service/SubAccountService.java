package bikram.businessmanager.service;

import bikram.businessmanager.dto.CompanyDto;
import bikram.businessmanager.dto.SubAccountDto;
import bikram.businessmanager.dto.SubAccountDtoWithAcount;
import bikram.businessmanager.entity.Company;
import bikram.businessmanager.entity.account.Account;
import bikram.businessmanager.entity.account.SubAccount;
import bikram.businessmanager.entity.inventory.SystemAccount;
import bikram.businessmanager.repository.SubaccountRepository;

import bikram.businessmanager.utils.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class SubAccountService extends BaseService<SubAccount> {

    private final SubaccountRepository subaccountRepository;

    public SubAccountService(SubaccountRepository subaccountRepository) {
        super(subaccountRepository);
        this.subaccountRepository = subaccountRepository;
    }

    /* =========================================
       EXISTS CHECK
       ========================================= */

    public boolean existsByNameAndAccount(
            String name,
            Account selectedAccount
    ) {
        EntityManager em = JPAUtil.getEmf().createEntityManager();

        return subaccountRepository.existsByNameAndAccount(
                em,
                name,
                selectedAccount
        );
    }

    /* =========================================
       DTO LIST
       ========================================= */

    public List<SubAccountDto> getAllDtos(Long companyId) {
        EntityManager em = JPAUtil.getEmf().createEntityManager();
        try {
            return subaccountRepository.getAllSubAccountDtoBycompany(em,companyId);
        } finally {
            em.close();
        }

    }

    /* =========================================
       DTO BY ID
       ========================================= */

    public SubAccountDto getSubAccountDtoById(
            EntityManager em,
            Long companyId,
            Long id
    ) {

        SubAccount subAccount =
                subaccountRepository.findBySubAccountId(em, companyId, id);

        if (subAccount == null) {
            return null;
        }

        return SubAccountDto.builder()
                .id(subAccount.getId())
                .accountName(subAccount.getAccount().getName())
                .subAccountName(subAccount.getName())
                .build();
    }

    /* =========================================
       ENTITY METHODS
       ========================================= */

    public SubAccount getSubAccountByIdAndCompany(
            Long companyId,
            Long id
    ) {
        var em = JPAUtil.getEmf().createEntityManager();

        return subaccountRepository.findBySubAccountId(em, companyId, id);
    }


    public SubAccount getSubAccountByCompanyAndCode(
            Long companyId,
            String number
    ) {
        var em = JPAUtil.getEmf().createEntityManager();
        return subaccountRepository.getSubAccountByCompanyAndCode(
                em,
                companyId,
                number
        );
    }

    public SubAccount getSubAccountBycompanyandSysAcc(
            Long companyId,
            SystemAccount type
    ) {
        var em = JPAUtil.getEmf().createEntityManager();
        return subaccountRepository
                .findSubAccountByCompanyAndSystemAc(
                        em,
                        companyId,
                        type
                );
    }

    public BigDecimal getBalance(Long companyId,Long subAccountId){
        var em = JPAUtil.getEmf().createEntityManager();
        try {
            return subaccountRepository.getBalance(em,companyId,subAccountId);
        } finally {
            em.close();
        }
    }

    public List<SubAccountDto> getAllContraAllowDto(Long companyId){
        var em = JPAUtil.getEmf().createEntityManager();
        try {
            return subaccountRepository.getAllContraAllowedAccountDto(em,companyId);
        } finally {
            em.close();
        }
    }

    public List<SubAccountDtoWithAcount> getallSubAccountDtoWithAccount(Long companyId){
        var em = JPAUtil.getEmf().createEntityManager();
        try {
            return subaccountRepository.getAllDtowithaccount(em,companyId);
        }
        finally {
            em.close();
        }
    }
}