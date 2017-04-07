/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rts;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import rts.exception.CannotScheduleException;
import rts.scheduler.TLSScheduler;
import rts.task.Model;
import rts.task.Task;
import rts.task.TaskFactory;

/**
 *
 * @author e0013405
 */
public class Main {

    // Default configs.
    public static int NUMBER_OF_TASK = 90; // Generate 90 tasks of different models.
    public static int NUMBER_OF_DESTROYED = 45; // Delete 45 tasks during runtime.
    public static int NUMBER_OF_ADDED = 45; // Delete 45 tasks during runtime.
    public static int TIME_STEP = 50; // Time step size, 50ms.
    public static long SIMULATION_TIME = 180000; // Simulation time, 3min;
    public static double THRESHOLD = 1.0; // Threshold, 1ms.
    public static int TIME_INTERVAL = 100; // Frequency for the add or delete event.

    // Result statitics.
    public static int NUMBER_OF_NON_SCHEDULABLE_TASK = 0;
    public static int NUMBER_OF_JITTER = 0;
    public static double OVERHEAD = 0;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        // Read configuration data.
        Initiator.InitiateConfig();
        // Read models.
        List<Model> models = Initiator.InitiateModels();
        // Randomly generate tasks.
        List<Task> initialTask = TaskFactory.generateTasks(NUMBER_OF_TASK, models);

        try {

            // Generate the schdule table before execution.
            TLSScheduler scheduler = new TLSScheduler(initialTask);

            // Add task and delete task during runtime.
            Runnable runnable = new Runnable() {
                public void run() {
                    boolean add = true;
                    int addCounter = 0, deleteCounter = 0;
                    long time = 0;
                    while (time < SIMULATION_TIME) {
                        long entryTime = System.currentTimeMillis();
                        if (add) {
                            if (addCounter < NUMBER_OF_ADDED) {
                                List<Task> tasks = TaskFactory.generateTasks(1, models);
                                long beforeAlgo = System.nanoTime();
                                try {
                                    scheduler.insertTask(tasks.get(0));
                                } catch (CannotScheduleException ex) {
                                    NUMBER_OF_NON_SCHEDULABLE_TASK++;
                                }
                                OVERHEAD += ((double)System.nanoTime() - (double)beforeAlgo)/1000000;
                                addCounter++;
                            }
                            add = false;
                        } else {
                            long beforeAlgo = System.nanoTime();
                            if (deleteCounter < NUMBER_OF_DESTROYED) {
                                if (deleteCounter < initialTask.size()) {
                                    scheduler.deleteTask(initialTask.get(deleteCounter));
                                    deleteCounter++;
                                }
                            }
                            OVERHEAD += ((double)System.nanoTime() - (double)beforeAlgo)/1000000;
                            add = true;
                        }
                        try {
                            Thread.sleep(TIME_INTERVAL);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        time += (TIME_INTERVAL + System.currentTimeMillis() - entryTime);
                    }

                    // Simulation done, output result.
                    List<String> data = new ArrayList<>();
                    data.add("NUMBER_OF_NON_SCHEDULABLE_TASK:" + NUMBER_OF_NON_SCHEDULABLE_TASK);
                    data.add("NUMBER_OF_JITTER:" + NUMBER_OF_JITTER);
                    data.add("OVERHEAD:" + OVERHEAD);
                    outputResult(data);
                }
            };

            Thread thread = new Thread(runnable);
            thread.start();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void outputResult(List<String> data) {
        Path filePath = Paths.get("result", "result.txt");
        FileWriter fileWriter = null;
        PrintWriter printWriter = null;

        try {
            fileWriter = new FileWriter(filePath.toString(), true);
            printWriter = new PrintWriter(fileWriter);
            Iterator<String> iterator = data.iterator();
            while (iterator.hasNext()) {
                printWriter.println(iterator.next().toString());
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                fileWriter.close();
                printWriter.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

}
