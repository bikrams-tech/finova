package bikram.businessmanager.repository;

import bikram.businessmanager.entity.Company;
import bikram.businessmanager.entity.InvoiceSequence;
import bikram.businessmanager.entity.PurchaseSequence;
import bikram.businessmanager.entity.inventory.DocumentType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;

import java.time.LocalDate;

public class InvoiceSequenceRepository {

    public synchronized Long getNextInvoiceNumber(
            EntityManager em,
            Long companyId,
            int year,
            DocumentType type
    ) {

        InvoiceSequence sequence;

        try {

            sequence = em.createQuery(
                            """
                            SELECT s FROM InvoiceSequence s
                            WHERE s.companyId = :companyId
                            AND s.year = :year
                            AND s.documentType = :type
                            """,
                            InvoiceSequence.class
                    )
                    .setParameter("companyId", companyId)
                    .setParameter("year", year)
                    .setParameter("type", type)
                    .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                    .getSingleResult();

        } catch (NoResultException e) {

            sequence = new InvoiceSequence();
            sequence.setCompanyId(companyId);
            sequence.setYear(year);
            sequence.setDocumentType(type);
            sequence.setCurrentNumber(0L);

            em.persist(sequence);
        }

        Long next = sequence.getCurrentNumber() + 1;
        sequence.setCurrentNumber(next);

        return next;
    }
}