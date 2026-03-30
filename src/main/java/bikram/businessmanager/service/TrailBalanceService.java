package bikram.businessmanager.service;

import bikram.businessmanager.dto.TrailBalanceRow;
import bikram.businessmanager.repository.TrailBalanceRepository;
import bikram.businessmanager.utils.JPAUtil;

import java.util.List;

public class TrailBalanceService {
    private TrailBalanceRepository repository;
    public TrailBalanceService(TrailBalanceRepository repository){
        this.repository = repository;
    }

    public List<TrailBalanceRow> getTrailBalanceRow(Long companyId){
        var em = JPAUtil.getEmf().createEntityManager();
        try {
            return repository.getTrailBalanceRow(em,companyId);
        } finally {
            em.close();
        }
    }
}
