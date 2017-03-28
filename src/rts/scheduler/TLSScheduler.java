/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rts.scheduler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import rts.Main;
import rts.exception.CannotScheduleException;
import rts.task.Task;

/**
 *
 * @author e0013405
 */
public class TLSScheduler {
    
    private TLSScheduleTable table;
    private Map<Integer,List<Task>> classifiedTasks;
    private double[] globalIdleRate;
    private int maxPeriod;

    public TLSScheduler( List<Task> tasks ) throws CannotScheduleException {
        
        // Find maximum period of all the tasks.
        for( Task task : tasks ){
            int period = task.getModel().getPeriod();
            if( period > maxPeriod )
                maxPeriod = period;
        }
        
        // Initiate schedule table.
        table = new TLSScheduleTable(maxPeriod);
        
        // Initiate global idle rates.
        globalIdleRate = new double[ maxPeriod/Main.TIME_STEP];
        for( int i=0;i<globalIdleRate.length;i++ ) globalIdleRate[i] = 1.0;
        
        // Classify tasks based on different period.
        classifiedTasks = new HashMap<>();
        for( Task task : tasks ){
            Integer period = task.getModel().getPeriod();
            if( classifiedTasks.containsKey(period) ){
                classifiedTasks.get(period).add(task);
            }
            else{
                List<Task> tasksForPeriod = new ArrayList<>();
                tasksForPeriod.add(task);
                classifiedTasks.put(period, tasksForPeriod);
            }
        }
        
        // Insert all the tasks into schedule table.
        generateInitialTable();
    }
    
    private void generateInitialTable() throws CannotScheduleException{
        
        List<Integer> periods = new ArrayList<>(classifiedTasks.keySet());
        Collections.sort(periods);
        
        for( Integer period : periods ){
            List<Task> tasksWithSamePeriod = classifiedTasks.get(period);
            Collections.sort(tasksWithSamePeriod, (o1, o2) -> {
                return (int)(o1.getModel().getComputationTime()-o2.getModel().getComputationTime());
            });
            TLSStep belongedStep = table.findStepByPeriod(period);
            for( Task task : tasksWithSamePeriod ){
                // Find the step that has maximum local idle rate.
                int numberOfLocalSteps = belongedStep.getLocalSteps().size();
                int indexWithMaxIdlerate = 0;
                double maxIdlerate = 0;
                for( int i=0;i<numberOfLocalSteps;i++ ){
                    if( belongedStep.getLocalIdleRates()[i]>maxIdlerate ){
                        maxIdlerate = belongedStep.getLocalIdleRates()[i];
                        indexWithMaxIdlerate = i;
                    }
                }
                // Put the task into the step.
                belongedStep.getLocalSteps().get(indexWithMaxIdlerate).add(task);
                // Update the idle rate for the step.
                for( int i=0;i< (maxPeriod/belongedStep.getPeriod());i++ ){
                    int affectedIndex = indexWithMaxIdlerate + (belongedStep.getLocalSteps().size()*i);
                    globalIdleRate[ affectedIndex ] -= task.getModel().getComputationTime()/Main.TIME_STEP;
                    if( globalIdleRate[ affectedIndex ] < 0 ) 
                        throw new CannotScheduleException();
                }
            }
        }
    }
    
    private void rebalanceTable(){
        
    }
    
    public void insertNewTask( Task task ){
        
    }
    
    public void deleteTask( Task task ){
        
    }

    public TLSScheduleTable getTable() {
        return table;
    } 
    
}
