package bikram.businessmanager.test;

import bikram.businessmanager.entity.account.JournalLine;
import bikram.businessmanager.utils.JPAUtil;

import java.util.List;

public class JournalLineTest {

    public static void main(String[] args) {
        JournalLineTest test = new JournalLineTest();
        test.test();
    }

    public List<JournalLine> loadLine() {
        var em = JPAUtil.getEmf().createEntityManager();
        try {
            return em.createQuery(
                    "SELECT j FROM JournalLine j", JournalLine.class
            ).getResultList();
        } finally {
            em.close();
        }
    }

    public void test() {
        List<JournalLine> lines = loadLine();

        for (JournalLine line : lines) {
            System.out.println("Journal ID: " + line.getJournalEntry().getId());
            System.out.println("SubAccount: " + line.getSubAccount().getName());
            System.out.println("Debit: " + line.getDebit());
            System.out.println("Credit: " + line.getCredit());
            System.out.println("----------------------");
        }
    }
}