package database;

import entity.PersonEntity;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class DbOperations {

//---------------------Print all people---------------------

    public void printAllPeople() {
        Session session = DbConnect.getSession();
        Transaction transaction = session.beginTransaction();

        List<PersonEntity> people = session.createQuery("FROM PersonEntity").list();

        for (PersonEntity person : people) {
            System.out.println("Person: " + person.getName() + ", Craft: " +person.getCraft());
        }

        transaction.commit();
        session.close();
    }

//---------------------Print people by craft---------------------

    public void printAllPeopleByCraft(String craftName) {
        Session session = DbConnect.getSession();
        Transaction transaction = session.beginTransaction();

        Query query = session.createQuery("FROM PersonEntity WHERE craft = :craftName");
        query.setParameter("craftName", craftName);

        List<PersonEntity> people = query.list();

        for (PersonEntity person : people) {
            System.out.println("Person: " + person.getName() + ", Craft: " + person.getCraft());
        }

        transaction.commit();
        session.close();
    }

//---------------------Print person by name---------------------

    public void printPersonByName(String personName) {
        Session session = DbConnect.getSession();
        Transaction transaction = session.beginTransaction();

        Query query = session.createQuery("FROM PersonEntity WHERE name = :name");
        query.setParameter("name", personName);

        PersonEntity person = (PersonEntity) query.uniqueResult();

        if (person != null) {
            System.out.println("Person: " +person.getName()+ ", Craft: " +person.getCraft());
        } else {
            System.out.println("Person with name '" + personName + "' not found.");
        }

        transaction.commit();
        session.close();
    }

//---------------------Add new person---------------------

    public void addPerson(String personName, String craftName){
        Session session = DbConnect.getSession();
        Transaction transaction = session.beginTransaction();

        PersonEntity newPerson = new PersonEntity();                                // vytvoreni nove osoby
        newPerson.setName(personName);                                              // nastaveni jmena osoby
        newPerson.setCraft(craftName);                                              // prirazeni stanice
        session.persist(newPerson);                                                 // ulozeni do db

        System.out.println("Person: " + newPerson.getName() + ", Craft: " + newPerson.getCraft());

        transaction.commit();
        session.close();
    }

//---------------------Update craft name---------------------

    public void updateCraftName(String oldName, String newName){
        Session session = DbConnect.getSession();
        Transaction transaction = session.beginTransaction();

        Query query = session.createQuery("FROM PersonEntity WHERE craft = :craft");
        query.setParameter("craft", oldName);

        List<PersonEntity> people = query.list();

        if (!people.isEmpty()) {
            for (PersonEntity person : people) {
                person.setCraft(newName);
                session.merge(person);
            }

            transaction.commit();
            session.close();

            for (PersonEntity person : people) {
                System.out.println("Person: " + person.getName() + ", Craft: " + person.getCraft());
            }
        } else {
            System.out.println("Craft with name '" + oldName + "' not found.");
            transaction.commit();
            session.close();
        }
    }

//---------------------Update person name---------------------

    public void updatePersonName(String oldName, String newName){
        Session session = DbConnect.getSession();
        Transaction transaction = session.beginTransaction();

        Query query = session.createQuery("FROM PersonEntity WHERE name = :name");
        query.setParameter("name", oldName);

        PersonEntity person = (PersonEntity) query.uniqueResult();

        if (person != null) {
            person.setName(newName);
            session.persist(person);

            transaction.commit();
            session.close();

            printPersonByName(newName);
        } else {
            System.out.println("Person with name '" + oldName + "' not found.");
            transaction.commit();
            session.close();
        }
    }

//---------------------Update craft name for one person---------------------

    public void updatePersonCraft(String personName, String newCraftName){
        Session session = DbConnect.getSession();
        Transaction transaction = session.beginTransaction();

        Query query = session.createQuery("FROM PersonEntity WHERE name = :name");
        query.setParameter("name", personName);

        PersonEntity person = (PersonEntity) query.uniqueResult();

        if (person != null) {
            person.setCraft(newCraftName);
            session.persist(person);

            transaction.commit();
            session.close();

            printPersonByName(personName);
        } else {
            System.out.println("Person with name '" + personName + "' not found.");
            transaction.commit();
            session.close();
        }
    }

//---------------------Delete person by name---------------------

    public void deletePersonByName(String personName) {
        Session session = DbConnect.getSession();
        Transaction transaction = session.beginTransaction();

        try {
            // Vyhledání osoby podle jména
            Query query = session.createQuery("FROM PersonEntity WHERE name = :name");
            query.setParameter("name", personName);

            PersonEntity person = (PersonEntity) query.uniqueResult();

            if (person != null) {
                session.remove(person); // Odstranění nalezené osoby
                transaction.commit();
                System.out.println("Person with name '" + personName + "' has been deleted from the database.");
            } else {
                System.out.println("Person with name '" + personName + "' not found.");
                transaction.commit();
            }
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

//---------------------Delete all people---------------------

    public void deleteAllPeople(){
        Session session = DbConnect.getSession();
        Transaction transaction = session.beginTransaction();

        String hql = "DELETE FROM PersonEntity";
        Query query = session.createQuery(hql);
        int deletedRows = query.executeUpdate();

        transaction.commit();
        session.close();
    }

//---------------------Delete all positions---------------------

    public void deleteAllPositions(){
        Session session = DbConnect.getSession();
        Transaction transaction = session.beginTransaction();

        String hql = "DELETE FROM ISSPositionEntity";
        Query query = session.createQuery(hql);
        int deletedRows = query.executeUpdate();

        transaction.commit();
        session.close();
    }

}
