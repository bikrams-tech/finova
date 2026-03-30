package bikram.businessmanager.service;

import bikram.businessmanager.dto.BalanceSheetReport;
import bikram.businessmanager.dto.BalanceSheetRow;
import bikram.businessmanager.repository.BalanceSheetRepository;
import bikram.businessmanager.utils.JPAUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BalanceSheetService {

    private BalanceSheetRepository repository;
    public BalanceSheetService(BalanceSheetRepository repository){
        this.repository = repository;
    }

    public List<BalanceSheetRow> getBalanceSheetRow(Long companyId, LocalDate endDate) {

        var em = JPAUtil.getEmf().createEntityManager();

        try {
            List<Object[]> rows = repository.getBalanceSheetRaw(em, companyId, endDate);

            List<BalanceSheetRow> result = new ArrayList<>();

            for (Object[] row : rows) {

                String name = (String) row[0];
                String category = row[1].toString();
                BigDecimal amount = (BigDecimal) row[2];

                if (amount == null) {
                    amount = BigDecimal.ZERO;
                }

                result.add(new BalanceSheetRow(name, category, amount));
            }

            return result;

        } catch (Exception e){
            e.printStackTrace();
            throw e;
        }
        finally {
            em.close();
        }
    }

    public BalanceSheetReport buildBalanceSheet(Long companyId, LocalDate endDate) {

        List<BalanceSheetRow> rows = getBalanceSheetRow(companyId, endDate);

        List<BalanceSheetRow> assets = new ArrayList<>();
        List<BalanceSheetRow> liabilities = new ArrayList<>();
        List<BalanceSheetRow> equity = new ArrayList<>();

        BigDecimal totalAssets = BigDecimal.ZERO;
        BigDecimal totalLiabilities = BigDecimal.ZERO;
        BigDecimal totalEquity = BigDecimal.ZERO;

        for (BalanceSheetRow row : rows) {

            switch (row.category().toUpperCase()) {

                case "ASSET" -> {
                    assets.add(row);
                    totalAssets = totalAssets.add(row.amount());
                }

                case "LIABILITY" -> {
                    liabilities.add(row);
                    totalLiabilities = totalLiabilities.add(row.amount());
                }

                case "EQUITY" -> {
                    equity.add(row);
                    totalEquity = totalEquity.add(row.amount());
                }
            }
        }

        return new BalanceSheetReport(
                assets,
                liabilities,
                equity,
                totalAssets,
                totalLiabilities,
                totalEquity
        );
    }
}