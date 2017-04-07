/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rts.scheduler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
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
    private Map<Integer, List<Task>> classifiedTasks;
    private double[] globalIdleRate;
    private int maxPeriod;
    private Memento memento;

    public TLSScheduler(List<Task> tasks) throws CannotScheduleException {

        // Find maximum period of all the tasks.
        for (Task task : tasks) {
            int period = task.getModel().getPeriod();
            if (period > maxPeriod) {
                maxPeriod = period;
            }
        }

        // Initiate schedule table.
        table = new TLSScheduleTable(maxPeriod);

        // Initiate global idle rates.
        globalIdleRate = new double[maxPeriod / Main.TIME_STEP];
        for (int i = 0; i < globalIdleRate.length; i++) {
            globalIdleRate[i] = 1.0;
        }

        // Classify tasks based on different period.
        classifiedTasks = new HashMap<>();
        for (Task task : tasks) {
            Integer period = task.getModel().getPeriod();
            if (classifiedTasks.containsKey(period)) {
                classifiedTasks.get(period).add(task);
            } else {
                List<Task> tasksForPeriod = new ArrayList<>();
                tasksForPeriod.add(task);
                classifiedTasks.put(period, tasksForPeriod);
            }
        }

        // Insert all the tasks into schedule table.
        generateInitialTable();
    }

    /**
     * Generate the schedule table based on all the tasks known before execution.
     * @throws CannotScheduleException 
     */
    private void generateInitialTable() throws CannotScheduleException {

        List<Integer> periods = new ArrayList<>(classifiedTasks.keySet());
        Collections.sort(periods);

        for (Integer period : periods) {
            List<Task> tasksWithSamePeriod = classifiedTasks.get(period);
            Collections.sort(tasksWithSamePeriod, (o1, o2) -> {
                return (int) (o1.getModel().getComputationTime() - o2.getModel().getComputationTime());
            });
            for (Task task : tasksWithSamePeriod) {
                insertTask(task);
            }
        }

    }

    /**
     * Re-balance the step if the step is not balanced.
     * @param step 
     */
    private void rebalanceTable(TLSStep step) {

        if (step.getLocalSteps().size() <= 1) {
            return;
        }

        IndexEntry result;
        while ((result = checkBalanced(step)) != null) {
            int minIndex = result.getMinIndex(), maxIndex = result.getMaxIndex();

            // Find the last element in max.
            List<Task> maxLocalStep = step.getLocalSteps().get(maxIndex);
            List<Task> minLocalStep = step.getLocalSteps().get(minIndex);
            Task task = maxLocalStep.get(maxLocalStep.size() - 1);

            // Delete the task from max.
            maxLocalStep.remove(maxLocalStep.size() - 1);
            step.getLocalload()[maxIndex] -= task.getModel().getComputationTime();
            for (int i = 0; i < (maxPeriod / step.getPeriod()); i++) {
                int affectedIndex = maxIndex + (step.getLocalSteps().size() * i);
                globalIdleRate[affectedIndex] += task.getModel().getComputationTime() / Main.TIME_STEP;
            }

            // Add the task to min.
            minLocalStep.add(task);
            step.getLocalload()[minIndex] += task.getModel().getComputationTime();
            for (int i = 0; i < (maxPeriod / step.getPeriod()); i++) {
                int affectedIndex = minIndex + (step.getLocalSteps().size() * i);
                globalIdleRate[affectedIndex] -= task.getModel().getComputationTime() / Main.TIME_STEP;
            }
            
            Main.NUMBER_OF_JITTER++;
        }

    }

    /**
     * Check if the step is balanced.
     * @param step
     * @return 
     */
    private IndexEntry checkBalanced(TLSStep step) {
        double[] localload = step.getLocalload();
        double minLoad = Double.MAX_VALUE, maxLoad = Double.MIN_VALUE;
        int minIndex = 0, maxIndex = 0;
        for (int i = 0; i < localload.length; i++) {
            if (localload[i] > maxLoad) {
                maxLoad = localload[i];
                maxIndex = i;
            }
            if (localload[i] < minLoad) {
                minLoad = localload[i];
                minIndex = i;
            }
        }
        if (maxLoad - minLoad >= Main.THRESHOLD) {
            return new IndexEntry(minIndex, maxIndex);
        }
        return null;
    }

    private class IndexEntry {

        private int minIndex;
        private int maxIndex;

        public IndexEntry(int minIndex, int maxIndex) {
            this.minIndex = minIndex;
            this.maxIndex = maxIndex;
        }

        public int getMinIndex() {
            return minIndex;
        }

        public void setMinIndex(int minIndex) {
            this.minIndex = minIndex;
        }

        public int getMaxIndex() {
            return maxIndex;
        }

        public void setMaxIndex(int maxIndex) {
            this.maxIndex = maxIndex;
        }

    }

    /**
     * Insert a task into the schedule table.
     * @param task
     * @throws CannotScheduleException 
     */
    public void insertTask(Task task) throws CannotScheduleException {
        
        memento = new Memento(globalIdleRate);
        
        TLSStep belongedStep = table.findStepByPeriod(task.getModel().getPeriod());
        // Find the step that has minimum local load.
        int numberOfLocalSteps = belongedStep.getLocalSteps().size();
        int indexWithMinLoad = 0;
        double minLoad = Double.MAX_VALUE;
        for (int i = 0; i < numberOfLocalSteps; i++) {
            if (belongedStep.getLocalload()[i] < minLoad) {
                minLoad = belongedStep.getLocalload()[i];
                indexWithMinLoad = i;
            }
        }
        // Put the task into the step.
        belongedStep.getLocalSteps().get(indexWithMinLoad).add(task);
        // Update local load.
        belongedStep.getLocalload()[indexWithMinLoad] += task.getModel().getComputationTime();
        // Update global idle rate.
        for (int i = 0; i < (maxPeriod / belongedStep.getPeriod()); i++) {
            int affectedIndex = indexWithMinLoad + (belongedStep.getLocalSteps().size() * i);
            globalIdleRate[affectedIndex] -= task.getModel().getComputationTime() / Main.TIME_STEP;
            if (globalIdleRate[affectedIndex] < 0) {       
                // Reset to previous state
                belongedStep.getLocalSteps().get(indexWithMinLoad).remove(task);
                belongedStep.getLocalload()[indexWithMinLoad] -= task.getModel().getComputationTime();
                double[] globalIdleRateCopy = memento.getGlobalIdleRateCopy();
                for( int j=0;j<globalIdleRate.length;j++ ){
                    globalIdleRate[j] = globalIdleRateCopy[j];
                }
                throw new CannotScheduleException();
            }
        }
    }
    
    private class Memento{
        
        private double[] globalIdleRateCopy;

        public Memento(double[] idleRate) {
            this.globalIdleRateCopy = new double[idleRate.length];
            for( int i=0;i<idleRate.length;i++ ){
                globalIdleRateCopy[i] = idleRate[i];
            }
        }

        public double[] getGlobalIdleRateCopy() {
            return globalIdleRateCopy;
        }

        public void setGlobalIdleRateCopy(double[] idleRate) {
            this.globalIdleRateCopy = new double[idleRate.length];
            for( int i=0;i<idleRate.length;i++ ){
                globalIdleRateCopy[i] = idleRate[i];
            }
        }
        
    }
    

    /**
     * Delete a task from the schedule table if exists.
     * @param task 
     */
    public void deleteTask(Task task) {
        TLSStep belongedStep = table.findStepByPeriod(task.getModel().getPeriod());

        Iterator<List<Task>> iterator = belongedStep.getLocalSteps().iterator();
        boolean deleted = false;
        int localstepIndex = -1;
        while (iterator.hasNext()) {
            List<Task> localstep = iterator.next();
            localstepIndex++;
            Iterator<Task> it = localstep.iterator();
            while (it.hasNext()) {
                Task tmp = it.next();
                if (tmp.equals(task)) {
                    // delete the task.
                    it.remove();
                    // update local load. 
                    belongedStep.getLocalload()[localstepIndex] -= task.getModel().getComputationTime();
                    // update global idle rate.
                    for (int i = 0; i < (maxPeriod / belongedStep.getPeriod()); i++) {
                        int affectedIndex = localstepIndex + (belongedStep.getLocalSteps().size() * i);
                        globalIdleRate[affectedIndex] += task.getModel().getComputationTime() / Main.TIME_STEP;
                    }

                    deleted = true;
                    break;
                }
            }
            if (deleted) {
                break;
            }
        }

        if (deleted) {
            rebalanceTable(belongedStep);
        }
    }

    public TLSScheduleTable getTable() {
        return table;
    }

}
