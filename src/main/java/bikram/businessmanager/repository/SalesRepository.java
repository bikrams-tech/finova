package bikram.businessmanager.repository;

import bikram.businessmanager.dto.sale.SaleReportDto;
import bikram.businessmanager.dto.sale.SaleTableDto;
import bikram.businessmanager.entity.inventory.Sale;
import jakarta.persistence.*;

import java.util.List;

public class SalesRepository extends BaseRepository<Sale>{

    public SalesRepository(){
        super(Sale.class);
    }

    public List<SaleTableDto> getSaleTableDto(EntityManager em, Long companyId) {

        String jpql = """
        SELECT new bikram.businessmanager.dto.SaleTableDto(
            s.id,
            s.invoiceNumber,
            s.customer.name,
            s.paymentMethod,
            SIZE(s.salesItems),
            s.grandTotal,
            s.salesDate
        )
        FROM Sale s
        WHERE s.company.id = :companyId
        ORDER BY s.salesDate DESC
        """;

        return em.createQuery(jpql, SaleTableDto.class)
                .setParameter("companyId", companyId)
                .getResultList();
    }

    public List<SaleReportDto> getSaleReportByProduct(EntityManager em, Long companyId){
        String jpql = """
                SELECT new bikram.businessmanager.dto.sale.SaleReportDto(
                p.name,
                SUM(s.quantity)
                )
                FROM SaleItem s
                JOIN s.product p
                WHERE s.sales.company.id = :companyId
                ORDER BY p.name
                ORDER BY SUM(s.quantity) DESC
                """;
        return em.createQuery(jpql, SaleReportDto.class)
                .setParameter("companyId",companyId)
                .getResultList();
    }

    public List<SaleReportDto> getByDay(EntityManager em,Long companyId){
        String jpql = """
                SELECT new bikram.businessmanager.dto.SaleReportDto(
                FUNCTION('DAYNAME', s.saleDate),
                COUNT(s)
                )
                FROM Sale s 
                WHERE s.company.id = : companyId
                GROUP BY FUNCTION('DAYNAME', s.saleDate)
                """;
        return em.createQuery(jpql,SaleReportDto.class)
                .setParameter("companyId",companyId)
                .getResultList();
    }

    public List<SaleReportDto> getByMonth(EntityManager em,Long companyId){
        String jpql = """
                SELECT new bikram.businessmanager.dto.SaleReportDto(
                                                            FUNCTION('MONTHNAME', s.saleDate),
                                                            COUNT(s)
                                                        )
                                                        FROM Sale s
                                                        GROUP BY FUNCTION('MONTHNAME', s.saleDate)
                """;
        return em.createQuery(jpql,SaleReportDto.class)
                .setParameter("companyId",companyId)
                .getResultList();
    }
    public List<SaleReportDto> getByTime(EntityManager em,Long companyId){
        String jpql = """
                SELECT new bikram.businessmanager.dto.SaleReportDto(
                                                            CASE
                                                                     WHEN HOUR(sale_date) < 12 THEN 'Morning'
                                                                     WHEN HOUR(sale_date) < 18 THEN 'Afternoon'
                                                                     ELSE 'Evening'
                                                                    END
                                                        )
                                                        FROM Sale s
                                                        GROUP BY FUNCTION('MONTHNAME', s.saleDate)
                """;
        return em.createQuery(jpql,SaleReportDto.class)
                .setParameter("companyId",companyId)
                .getResultList();
    }

    public List<SaleReportDto> getByCatagery(EntityManager em,Long companyId){
        String jpql = """
                SELECT new bikram.businessmanager.dto.SaleReportDto(
                                                                    c.name,
                                                                    SUM(si.quantity)
                                                                     )
                                                                     FROM SaleItem si
                                                                     JOIN si.product p
                                                                     OIN p.category c
                                                                     GROUP BY c.name
                """;
        return em.createQuery(jpql,SaleReportDto.class)
                .setParameter("companyId",companyId)
                .getResultList();
    }

    public List<SaleReportDto> getByTop(EntityManager em,Long companyId){
        String jpql = """
                SELECT new bikram.businessmanager.dto.SaleReportDto(
                                                                    p.name,
                                                                    SUM(si.quantity)
                                                                )
                                                               FROM SaleItem si
                                                               JOIN si.product p
                                                               GROUP BY p.name
                                                               ORDER BY SUM(si.quantity) DESC
                """;
        return em.createQuery(jpql,SaleReportDto.class)
                .setParameter("companyId",companyId)
                .getResultList();
    }


}