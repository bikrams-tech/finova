package bikram.businessmanager.service;

import bikram.businessmanager.entity.account.AccountGroup;
import bikram.businessmanager.entity.account.AccountType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AccountCodeGenerator {
    public static  String generate(EntityManager em, AccountGroup group){
        String jpql = """
                SELECT a.code
                FROM Account a
                WHERE a.accountGroup = : group
                ORDER BY a.code DESC
                """;
        TypedQuery<String> query = em.createQuery(jpql ,String.class);
        query.setParameter("group",group);
        query.setMaxResults(1);

        var result = query.getResultList();
        if (result.isEmpty()){
            return increament(group.getGroupCode());
        }
        return increament(result.get(0));
    }

    private static String increament(String code) {
        int num = Integer.parseInt(code);
        return String.valueOf(num+1);
    }
}
