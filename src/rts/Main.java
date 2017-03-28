/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rts;

import java.util.List;
import rts.scheduler.TLSScheduler;
import rts.task.Model;
import rts.task.ModelInitiator;
import rts.task.Task;
import rts.task.TaskFactory;

/**
 *
 * @author e0013405
 */
public class Main {

    public static int NUMBER_OF_TASK = 90; // Generate 90 tasks of different models.
    public static int NUMBER_OF_DESTROYED = 45; // Delete 45 tasks during runtime.
    public static int TIME_STEP = 50; // Time step size, 50ms.
    public static int SIMULATION_TIME = 3; // Simulation time, 3min;
    public static int THRESHOLD = 1; // Threshold, 1ms.
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        List<Model> models = ModelInitiator.getModels();
        List<Task> initialTask = TaskFactory.generateTasks(NUMBER_OF_TASK, models);
        try {
            TLSScheduler scheduler = new TLSScheduler(initialTask);
            
            System.out.println("rts.Main.main()");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("rts.Main.main()");
    }
    
}
