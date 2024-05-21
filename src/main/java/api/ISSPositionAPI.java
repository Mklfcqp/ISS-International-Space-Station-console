package api;

import database.DbConnect;
import entity.ISSPositionEntity;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;


public class ISSPositionAPI implements APILoaderToDatabase {

    @Override
    public void apiLoaderToDatabase() {

        Session session = DbConnect.getSession();
        Transaction transaction = session.beginTransaction();

        try {
            URL url = new URL("http://api.open-notify.org/iss-now.json");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            int responseCode = connection.getResponseCode();

            if (responseCode != 200) {
                throw new RuntimeException("HttpResponse code: " + responseCode);
            } else {
                InputStream inputStream = connection.getInputStream();
                JSONTokener tokener = new JSONTokener(inputStream);
                JSONObject jsonObject = new JSONObject(tokener);

                JSONObject issPositionObject = jsonObject.getJSONObject("iss_position");
                double latitude = issPositionObject.getDouble("latitude");
                double longitude = issPositionObject.getDouble("longitude");
                long timestamp = jsonObject.getLong("timestamp");
                LocalDateTime localDateTime = convertTimestampToLocalDateTime(timestamp);

                ISSPositionEntity issPosition = new ISSPositionEntity();
                issPosition.setLatitude(latitude);
                issPosition.setLongitude(longitude);
                issPosition.setLocalDateTime(localDateTime);

                session.save(issPosition);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        transaction.commit();
        session.close();
        System.out.println("Data were loaded to the database.");
    }

    public void apiCurrentPosition() {
        try {
            URL url = new URL("http://api.open-notify.org/iss-now.json");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            int responseCode = connection.getResponseCode();

            if (responseCode != 200) {
                throw new RuntimeException("HttpResponse code: " + responseCode);
            } else {
                // Získání vstupního proudu z API
                InputStream inputStream = connection.getInputStream();
                JSONTokener tokener = new JSONTokener(inputStream);
                JSONObject jsonObject = new JSONObject(tokener);

                // Získání informace o aktuální poloze
                JSONObject issPositionObject = jsonObject.getJSONObject("iss_position");
                double latitude = issPositionObject.getDouble("latitude");
                double longitude = issPositionObject.getDouble("longitude");
                long timestamp = jsonObject.getLong("timestamp");
                LocalDateTime localDateTime = convertTimestampToLocalDateTime(timestamp);

                System.out.println("Current position of ISS:");
                System.out.println("Latitude: " + latitude);
                System.out.println("Longitude: " + longitude);
                System.out.println("Timestamp: " + timestamp);
                System.out.println("Time: " + localDateTime);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private LocalDateTime convertTimestampToLocalDateTime(long timestamp) {
        Instant instant = Instant.ofEpochSecond(timestamp);
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.of("Europe/Prague"));
        return localDateTime;
    }

    private LocalDateTime findLocalDateTimeById(int id) {
        Session session = DbConnect.getSession();
        Transaction transaction = session.beginTransaction();

        ISSPositionEntity issPositionEntity = session.get(ISSPositionEntity.class, id);

        LocalDateTime localDateTime = null;
        if (issPositionEntity != null) {
            localDateTime = issPositionEntity.getLocalDateTime();
        }

        transaction.commit();
        session.close();

        return localDateTime;
    }

    private LocalTime convertToLocalTime(int id) {
        LocalDateTime localDateTime = findLocalDateTimeById(id);
        LocalTime localTime = localDateTime.toLocalTime();
        return localTime;
    }

    private double secondsDifference(int id2, int id1) {
        LocalTime time1 = convertToLocalTime(id1);
        LocalTime time2 = convertToLocalTime(id2);
        double difference = ChronoUnit.SECONDS.between(time2, time1);
        return difference;
    }

    private double findLatitudeById(int id) {
        Session session = DbConnect.getSession();
        Transaction transaction = session.beginTransaction();

        ISSPositionEntity issPositionEntity = session.get(ISSPositionEntity.class, id);

        Double latitude = null;
        if (issPositionEntity != null) {
            latitude = issPositionEntity.getLatitude();
        }

        transaction.commit();
        session.close();

        return latitude;
    }

    private double findLongitudeById(int id) {
        Session session = DbConnect.getSession();
        Transaction transaction = session.beginTransaction();

        ISSPositionEntity issPositionEntity = session.get(ISSPositionEntity.class, id);

        Double longitude = null;
        if (issPositionEntity != null) {
            longitude = issPositionEntity.getLongitude();
        }

        transaction.commit();
        session.close();

        return longitude;
    }

    public void ISSspeed() {

        double lat1 = findLatitudeById(1);
        double long1 = findLongitudeById(1);
        double lat2 = findLatitudeById(2);
        double long2 = findLongitudeById(2);

        //vzorec pro drahu
        double earthRadius = 6371000;
        double latitudeRad1 = Math.toRadians(lat1);
        double longitudeRad1 = Math.toRadians(long1);
        double latitudeRad2 = Math.toRadians(lat2);
        double longitudeRad2 = Math.toRadians(long2);

        double zavorka1 = ((latitudeRad1 - latitudeRad2)/2);
        double zavorka2 = ((longitudeRad1 - longitudeRad2)/2);

        double mezivypocet1 = 2 * earthRadius;
        double mezivypocet2 = Math.pow(Math.sin(zavorka1), 2);
        double mezivypocet3 = Math.pow(Math.sin(zavorka2), 2);
        double mezivypocet4 = Math.cos(latitudeRad1);
        double mezivypocet5 = Math.cos(latitudeRad2);
        double mezivypocet6 = mezivypocet2 + mezivypocet4 * mezivypocet5 * mezivypocet3;
        double mezivypocet7 = Math.sqrt(mezivypocet6);
        double mezivypocet8 = Math.asin(mezivypocet7);

        double draha = mezivypocet1 * mezivypocet8;
        System.out.println("Draha je: " +draha);

        //vzorec pro cas
        double time = -secondsDifference(2, 1);

        //vzorec pro rychlost
        double speedMS = draha / time;
        double speedKMH = 3.6 * speedMS;
        BigDecimal roundedSpeed = new BigDecimal(speedKMH).setScale(2, RoundingMode.HALF_UP);
        System.out.println("Speed of ISS is: "+roundedSpeed+ " km/h");
    }

}
