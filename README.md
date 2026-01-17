# HydroSync: Concurrent Service Station Simulator

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![JavaFX](https://img.shields.io/badge/JavaFX-GUI-4285F4?style=for-the-badge)
![Concurrency](https://img.shields.io/badge/Pattern-Producer--Consumer-success?style=for-the-badge)

**HydroSync**  is a multi-threaded simulation of a high-volume service station, engineered to demonstrate advanced Operating System concepts including **process synchronization**, **resource management**, and **deadlock prevention**.

It implements the classic **Producer-Consumer** problem using the **Bounded Buffer** pattern, visualized through a real-time JavaFX dashboard.

## 🚀 Overview

The simulation models a service station with limited resources:
1.  **Waiting Area (Buffer):** A fixed-size queue for incoming vehicles.
2.  **Service Bays (Consumers):** A limited number of pumps working concurrently.
3.  **Vehicles (Producers):** Autonomous threads arriving at stochastic intervals.

The system manages these competing resources using custom **Semaphores** to ensure data integrity and prevent race conditions.

## 📸 Dashboard Preview

*(Place a screenshot of your JavaFX interface here. Save it as `dashboard.png` in your repo)*
> The dashboard provides real-time status monitoring of all pumps, indicating whether they are `Available` (Green) or `Occupied` (Yellow).

## 🛠 Technical Architecture

This project strictly separates logic into concurrent entities to enforce thread safety.

### 1. The Core Mechanism: Semaphores
Instead of using high-level Java concurrent libraries, we implemented a custom `CountingSemaphore` class using `wait()` and `notifyAll()` to manually manage critical sections.

* **`mutex`**: Ensures mutual exclusion when accessing the shared queue.
* **`empty`**: Tracks available spots in the waiting area.
* **`full`**: Tracks the number of cars waiting to be served.

### 2. The Producer: `Car` Class
* Represents an autonomous vehicle thread.
* **Behavior:** Attempts to acquire the `empty` semaphore. If successful, it locks the `mutex`, adds itself to the `waitingQueue`, and signals `full`.

### 3. The Consumer: `Pump` Class
* Represents a service bay thread running in an infinite loop.
* **Behavior:** Waits for the `full` signal. Once a car is available, it locks the `mutex`, retrieves the car, and simulates service time using thread sleeping.

### 4. The UI Layer: `JavaFX`
* Decoupled from the core logic, the UI observes the state of the pumps and updates the `TableView` in real-time without blocking the simulation threads.

## 💻 Installation & Usage

### Prerequisites
* Java Development Kit (JDK) 11 or higher (requires JavaFX).

## 👥 Contributors

This simulation was architected and implemented by our engineering team:

* **Mousa Mohamed Mousa**
* **Omar Mohamed Farag**
* **Mohab Amr**
* **Mariel Robert John**
* **Malak Amr**

This project was developed as part of the **Operating Systems** subject coursework to simulate real-world concurrency challenges. It serves as a practical implementation of the following core university concepts:

* **The Producer-Consumer Problem:** Solving synchronization between vehicle arrival (Production) and service availability (Consumption).
* **Bounded Buffer Pattern:** Managing a fixed-size queue for the waiting area to handle resource limits.
* **Custom Semaphores & Mutex:** Implementing `wait()` and `signal()` logic from scratch to manage critical sections and ensure thread safety.
* **Process Synchronization:** Utilizing low-level locking to prevent race conditions and starvation in a multithreaded environment.


### Running the Simulation
1.  **Clone the repository**
    ```bash
    git clone [https://github.com/yourusername/HydroSync.git](https://github.com/yourusername/HydroSync.git)
    ```
2.  **Run via CLI** (or open in IntelliJ IDEA/Eclipse)
    ```bash
    java --module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml -jar HydroSync.jar
    ```

### Configuration
Upon launch, the application requests three inputs:
1.  **Waiting Area Capacity:** Size of the Bounded Buffer.
2.  **Pump Count:** Number of Consumer threads.
3.  **Car Count:** Number of Producer threads to generate.

## 📊 Sample Log Output

The system generates a detailed audit log in the console ensuring strict ordering of events:

```text
C1 arrived
C1 arrived and waiting
Pump 1: C1 Occupied
Pump 1: C1 login
Pump 1: C1 begins service at Bay 1
...
Pump 1: C1 finishes service
Pump 1: Bay 1 is now free
