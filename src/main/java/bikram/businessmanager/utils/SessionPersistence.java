package bikram.businessmanager.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class SessionPersistence {

    private static final String DIR =
            System.getProperty("user.home") + "/.businessmanager";

    private static final String FILE =
            DIR + "/session.json";

    private static final ObjectMapper mapper = new ObjectMapper();

    public static void saveCompanyId(Long companyId) {

        try {
            File directory = new File(DIR);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            SessionFile state = new SessionFile(companyId);
            mapper.writeValue(new File(FILE), state);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Long loadCompanyId() {

        try {
            File file = new File(FILE);

            if (!file.exists()) {
                return null;
            }

            SessionFile state = mapper.readValue(file, SessionFile.class);
            return state.getCompanyId();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // small inner DTO
    private static class SessionFile {

        private Long companyId;

        public SessionFile() {}

        public SessionFile(Long companyId) {
            this.companyId = companyId;
        }

        public Long getCompanyId() {
            return companyId;
        }

        public void setCompanyId(Long companyId) {
            this.companyId = companyId;
        }
    }
}