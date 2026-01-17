//TA Amany Mohamed
//Omar Mohamed Yousef 20236066
//Mohab Amr Mohamed 20236105
//Mariel Robert John 20236078
//Mousa Mohamed Mousa Mohamed Hussien 20235042
// Malak Amr 20236098
package com.example.carpump;
import javafx.application.Application;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;
import java.util.ResourceBundle;

class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(com.example.carpump.HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 960, 720);
        stage.setTitle("Simulation");
        stage.setScene(scene);
        stage.show();
    }
}
package com.example.carpump;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

        import java.net.URL;
import java.util.*;

        import javafx.scene.control.cell.PropertyValueFactory;

class CountingSemaphore {
    private int value;

    public CountingSemaphore(int value) {
        this.value = value;
    }

    public synchronized void acquire() {
        while (value == 0) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
        value--;
    }

    public synchronized void release() {
        value++;
        notifyAll();
    }

    public synchronized int getValue() {
        return value;
    }
}


class Car implements Runnable {
    private String carId;
    private Queue<String> waitingQueue;
    private com.example.carpump.CountingSemaphore mutex;
    private com.example.carpump.CountingSemaphore empty;
    private com.example.carpump.CountingSemaphore full;

    public Car(String carId, Queue<String> waitingQueue, com.example.carpump.CountingSemaphore mutex, com.example.carpump.CountingSemaphore empty, com.example.carpump.CountingSemaphore full) {
        this.carId = carId;
        this.waitingQueue = waitingQueue;
        this.mutex = mutex;
        this.empty = empty;
        this.full = full;
    }

    @Override
    public void run() {
        System.out.println(carId + " arrived");
        empty.acquire();
        mutex.acquire();
        waitingQueue.add(carId);
        System.out.println(carId + " arrived and waiting");
        mutex.release();
        full.release();
    }
}

class Pump implements Runnable {
    private int pumpId;
    private Queue<String> waitingQueue;
    private com.example.carpump.CountingSemaphore mutex;
    private com.example.carpump.CountingSemaphore empty;
    private com.example.carpump.CountingSemaphore full;
    private com.example.carpump.CountingSemaphore pumps;

    public Pump(int pumpId, Queue<String> waitingQueue, com.example.carpump.CountingSemaphore mutex, com.example.carpump.CountingSemaphore empty, com.example.carpump.CountingSemaphore full, com.example.carpump.CountingSemaphore pumps) {
        this.pumpId = pumpId;
        this.waitingQueue = waitingQueue;
        this.mutex = mutex;
        this.empty = empty;
        this.full = full;
        this.pumps = pumps;
    }

    @Override
    public void run() {
        try {
            while (true) {
                full.acquire();
                mutex.acquire();
                String carId = waitingQueue.poll();
                mutex.release();

                if ("END".equals(carId)) {

                    break;
                }

                // Service car
                System.out.println("Pump " + pumpId + ": " + carId + " Occupied");
                pumps.acquire();
                System.out.println("Pump " + pumpId + ": " + carId + " login");
                for (int i = 0; i < com.example.carpump.ServiceStation.users.size(); i++)
                {
                    if (Objects.equals(com.example.carpump.ServiceStation.users.get(i).getPumpName(), "Pump " + pumpId)) {
                        com.example.carpump.ServiceStation.users.set(i, new PumpItem("Pump " + pumpId, carId));
                    }
                }
                System.out.println("Pump " + pumpId + ": " + carId + " begins service at Bay " + pumpId);
                Thread.sleep((long)(Math.random() * 1000 * 3));
                System.out.println("Pump " + pumpId + ": " + carId + " finishes service");
                System.out.println("Pump " + pumpId + ": Bay " + pumpId + " is now free");
                for (int i = 0; i < com.example.carpump.ServiceStation.users.size(); i++)
                {
                    if (Objects.equals(com.example.carpump.ServiceStation.users.get(i).getPumpName(), "Pump " + pumpId)) {
                        com.example.carpump.ServiceStation.users.set(i, new PumpItem("Pump " + pumpId, ""));
                    }
                }
                pumps.release();
                empty.release();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}

class ServiceStation {
    private int waitingAreaCapacity;
    private int numberOfPumps;
    private int numberOfCars;
    private Queue<String> waitingQueue;
    private com.example.carpump.CountingSemaphore mutex;
    private com.example.carpump.CountingSemaphore empty;
    private com.example.carpump.CountingSemaphore full;
    private com.example.carpump.CountingSemaphore pumps;

    public static final ObservableList<PumpItem> users = FXCollections.observableArrayList();
    public ServiceStation(int waitingAreaCapacity, int numberOfPumps, int numberOfCars)
    {
        this.waitingAreaCapacity = waitingAreaCapacity;
        this.numberOfPumps = numberOfPumps;
        this.numberOfCars = numberOfCars;
        this.waitingQueue = new LinkedList<>();
        this.mutex = new com.example.carpump.CountingSemaphore(1);
        this.empty = new com.example.carpump.CountingSemaphore(waitingAreaCapacity);
        this.full = new com.example.carpump.CountingSemaphore(0);
        this.pumps = new com.example.carpump.CountingSemaphore(numberOfPumps);
    }
    public void startSimulation() {
        System.out.println("\n=== Service Station Simulation Starting ===\n");

        Thread[] pumpThreads = new Thread[numberOfPumps];
        for (int i = 0; i < numberOfPumps; i++) {
            pumpThreads[i] = new Thread(new com.example.carpump.Pump(i + 1, waitingQueue, mutex, empty, full, pumps));
            pumpThreads[i].start();
            users.add(new PumpItem("Pump " +Integer.toString(i + 1), ""));
        }

        Thread[] carThreads = new Thread[numberOfCars];
        for (int i = 0; i < numberOfCars; i++) {
            String carId = "C" + (i + 1);
            carThreads[i] = new Thread(new com.example.carpump.Car(carId, waitingQueue, mutex, empty, full));
            carThreads[i].start();

            try {
                Thread.sleep((long)(Math.random() * 500 * 3));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        try {
            // Wait for all cars to arrive
            for (Thread carThread : carThreads) {
                carThread.join();
            }

            mutex.acquire();
            //  Unblock any pumps waiting on 'full.acquire()'
            for (int i = 0; i < numberOfPumps; i++) {
                waitingQueue.add("END"); // Stop
                full.release();
            }
            mutex.release();

            for (Thread pumpThread : pumpThreads) {
                pumpThread.join();
            }





        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("\nAll cars processed, simulation ends");
    }

}

class HelloController implements Initializable
{

    @FXML
    private Label welcomeText;

    @FXML private TableColumn<PumpItem, String> PumpName;
    @FXML private TableColumn<PumpItem, String> OccupiedBy;

    @FXML
    private TableView<PumpItem> table;


    @FXML
    private TextField carCountTF;
    @FXML
    public TextField areaCountTF;
    @FXML
    private TextField pumpCountTF;

    private Property<ObservableList<PumpItem>> authorListProperty = new SimpleObjectProperty<>(com.example.carpump.ServiceStation.users);

    @FXML
    protected void onHelloButtonClick() {
        int waitingAreaCapacity = Integer.valueOf(areaCountTF.getText());
        int numberOfPumps = Integer.valueOf(pumpCountTF.getText());
        int numberOfCars= Integer.valueOf(carCountTF.getText());

        com.example.carpump.ServiceStation.users.clear();
        if (waitingAreaCapacity >= 1 && waitingAreaCapacity <= 10 && numberOfPumps >= 1 && numberOfPumps <= 10) {
            com.example.carpump.ServiceStation station = new com.example.carpump.ServiceStation(waitingAreaCapacity, numberOfPumps, numberOfCars);
            new Thread(station::startSimulation).start();
        }



    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        PumpName.setCellValueFactory(new PropertyValueFactory<PumpItem, String>("pumpName"));
        OccupiedBy.setCellValueFactory(new PropertyValueFactory<PumpItem, String>("occupiedBy"));
        table.getColumns().get(0).prefWidthProperty().bind(table.widthProperty().multiply(0.50));
        table.getColumns().get(1).prefWidthProperty().bind(table.widthProperty().multiply(0.50));
        table.itemsProperty().bind(authorListProperty);

        table.setRowFactory(tv -> new TableRow<PumpItem>() {
            @Override
            protected void updateItem(PumpItem item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setStyle("");
                } else {
                    if ("".equals(item.getOccupiedBy())) {
                        setStyle("-fx-background-color: lightgreen;");
                    }
                    else
                    {
                        setStyle("-fx-background-color: lightyellow;");
                    }
                }
            }
        });


    }
}
package com.example.carpump;

import javafx.application.Application;

class Launcher {
    public static void main(String[] args) {
        Application.launch(com.example.carpump.HelloApplication.class, args);
    }
}
package com.example.carpump;

class PumpItem
{
    private String pumpName = null;
    private String occupiedBy = null;

    public PumpItem() {
    }

    public PumpItem(String pumpName, String occupiedBy) {
        this.pumpName = pumpName;
        this.occupiedBy = occupiedBy;
    }

    public String getOccupiedBy() {
        return occupiedBy;
    }

    public void setOccupiedBy(String occupiedBy) {
        this.occupiedBy = occupiedBy;
    }

    public String getPumpName() {
        return pumpName;
    }

    public void setPumpName(String pumpName) {
        this.pumpName = pumpName;
    }
}
