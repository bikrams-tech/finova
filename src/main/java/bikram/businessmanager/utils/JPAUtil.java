package bikram.businessmanager.utils;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.HashMap;
import java.util.Map;

public class JPAUtil {

    private static final EntityManagerFactory emf = build();

    private static EntityManagerFactory build() {

        Map<String, Object> props = new HashMap<>();

        props.put("hibernate.connection.datasource", DataSourceConfig.getDataSource());

        return Persistence.createEntityManagerFactory("companyPU", props);
    }

    public static EntityManagerFactory getEmf() {
        return emf;
    }

    public static void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}