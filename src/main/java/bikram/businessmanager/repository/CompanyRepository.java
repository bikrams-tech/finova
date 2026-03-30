package bikram.businessmanager.repository;

import bikram.businessmanager.dto.CompanyDto;
import bikram.businessmanager.entity.Company;
import bikram.businessmanager.utils.JPAUtil;
import jakarta.persistence.EntityManager;

import java.util.List;

public class CompanyRepository extends BaseRepository<Company>{

    public CompanyRepository(){
        super(Company.class);
    }

   public Company findByPan(EntityManager em,String pan) {
            return em.createQuery(
                    "SELECT c FROM Company WHERE c.panNumber = :pan",
                    Company.class)
                    .setParameter("pan",pan)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);

   }

    public List<Company> findAllByName(EntityManager em,String name) {

            if (name == null || name.isBlank()) {
                return em.createQuery(
                        "SELECT c FROM Company c",
                        Company.class
                ).getResultList();
            }

            return em.createQuery(
                            "SELECT c FROM Company c WHERE LOWER(c.companyName) LIKE LOWER(:name)",
                            Company.class
                    )
                    .setParameter("name", "%" + name.trim() + "%")
                    .getResultList();


    }
    public List<CompanyDto> findByNameallCompanyDto(EntityManager em,String name) {
            if (name == null || name.isBlank()) {
                return em.createQuery(
                        "SELECT new bikram.businessmanager.dto.CompanyDto(\n" +
                        "    c.id,\n" +
                                "    c.name\n" +
                                ")\n" +
                                "FROM Company c", CompanyDto.class
                )
                        .getResultList();
            }

            return em.createQuery(
                            "SELECT new bikram.businessmanager.dto.CompanyDto(" +
                                    "c.id," +
                                    "c.name)" +
                                    " FROM Company c WHERE LOWER(c.companyName) LIKE LOWER(:name)",
                            CompanyDto.class
                    )
                    .setParameter("name", "%" + name.trim() + "%")
                    .getResultList();



    }

    public List<CompanyDto> getAllCompanyDto(EntityManager em) {
            return em.createQuery(
                            "SELECT new bikram.businessmanager.dto.CompanyDto(\n" +
                                    "    c.id,\n" +
                                    "    c.companyName\n" +
                                    ")\n" +
                                    "FROM Company c", CompanyDto.class
                    )
                    .getResultList();
    }
}