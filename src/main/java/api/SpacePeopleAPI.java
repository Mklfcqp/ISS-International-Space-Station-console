package api;

import database.DbConnect;
import entity.PersonEntity;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SpacePeopleAPI implements APILoaderToDatabase {

    @Override
    public void apiLoaderToDatabase() {

        Session session = DbConnect.getSession();
        Transaction transaction = session.beginTransaction();

        try {

            URL url = new URL("http://api.open-notify.org/astros.json");

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            // zkontroluje pripojeni
            int responseCode = connection.getResponseCode();

            if (responseCode != 200) {
                throw new RuntimeException("HttpResponse code: " + responseCode);
            } else {
                // Získání vstupního proudu z API
                InputStream inputStream = connection.getInputStream();
                JSONTokener tokener = new JSONTokener(inputStream);
                JSONObject jsonObject = new JSONObject(tokener);

                // Získání informací o jednotlivých osobách a uložení do databáze
                JSONArray peopleArray = jsonObject.getJSONArray("people");
                for (int i = 0; i < peopleArray.length(); i++) {
                    JSONObject personObject = peopleArray.getJSONObject(i);
                    String name = personObject.getString("name");
                    String craft = personObject.getString("craft");

                    PersonEntity person = new PersonEntity();
                    person.setName(name);
                    person.setCraft(craft);

                    session.save(person);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        transaction.commit();
        session.close();
        System.out.println("Data were loaded to the database.");
    }
}
