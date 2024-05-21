import api.APILoaderToDatabase;
import api.ISSPositionAPI;
import api.SpacePeopleAPI;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {

        APILoaderToDatabase people = new SpacePeopleAPI();
        people.apiLoaderToDatabase();

        //-----------------------------------------------------

        ISSPositionAPI position = new ISSPositionAPI();

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        //nahrani aktualni polohy do databaze 2x
        ScheduledFuture<?> scheduledFuture = executor.scheduleAtFixedRate(() -> {
            position.apiLoaderToDatabase();
        }, 0, 5, TimeUnit.SECONDS);

        executor.schedule(() -> {
            scheduledFuture.cancel(true);
            executor.shutdown();
//            position.ISSspeed();
        }, 5, TimeUnit.SECONDS);

        //-----------------------------------------------------

//        position.apiCurrentPosition();

        //-----------------------------------------------------

//        DbOperations dbOperations = new DbOperations();
//        dbOperations.printAllPeople();
//        dbOperations.printAllPeopleByCraft("ISS");
//        dbOperations.printPersonByName("Jasmin Moghbeli");
//        dbOperations.addPerson("Leo", "Titanic");
//        dbOperations.updateCraftName("ISS", "vesmirna lodicka");
//        dbOperations.updatePersonName("Jasmin Moghbeli", "Anonymous");
//        dbOperations.updatePersonCraft("Jasmin Moghbeli", "vesmirna lodicka");
//        dbOperations.deletePersonByName("Jasmin Moghbeli");
//        dbOperations.deleteAllPeople();
//        dbOperations.deleteAllPositions(); //musel bych nejdriv nechat dojet vlakno

    }
}


