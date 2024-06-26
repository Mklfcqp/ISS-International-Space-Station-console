package database;


import entity.ISSPositionEntity;
import entity.PersonEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class DbConnect {

    public static Session getSession() {
        String dbUserName = System.getenv("DB_USERNAME");
        String dbUserPassword = System.getenv("DB_PASSWORD");
        SessionFactory sessionFactory = new Configuration()
                .configure("hibernate.cfg.xml")
                .addAnnotatedClass(PersonEntity.class)
                .addAnnotatedClass(ISSPositionEntity.class)
                .setProperty("hibernate.connection.username", dbUserName)
                .setProperty("hibernate.connection.password", dbUserPassword)
                .buildSessionFactory();
        Session session = sessionFactory.openSession();
        return session;
    }
}
