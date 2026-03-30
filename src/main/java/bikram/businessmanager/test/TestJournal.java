package bikram.businessmanager.test;

import bikram.businessmanager.entity.account.JournalEntry;
import bikram.businessmanager.entity.account.JournalLine;
import bikram.businessmanager.service.JournalEntryService;
import bikram.businessmanager.service.ServiceProvider;
import bikram.businessmanager.utils.SessionContext;

import java.util.List;

public class TestJournal {

    public static void main(String[] args) {

        JournalEntryService journalEntryService =
                ServiceProvider.services().getJournalEntryService();

        List<JournalEntry> journalEntries =
                journalEntryService.getAllByCompany(1L);

        for (JournalEntry entry : journalEntries) {

            System.out.println("Journal ID: " + entry.getId());

            for (JournalLine line : entry.getLine()) {

                System.out.println(
                        "SubAccount: " + line.getSubAccount().getName()
                                + " | Debit: " + line.getDebit()
                                + " | Credit: " + line.getCredit()
                );
            }

            System.out.println("----------------------");
        }
    }
}