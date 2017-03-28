/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rts.scheduler;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import rts.Main;
import rts.task.Task;

/**
 *
 * @author e0013405
 */
public class TLSStep {
    
    private int period;
    private List< List<Task> > localSteps;
    private double[] localIdleRates;

    public TLSStep(int period) {
        this.period = period;
        this.localSteps = new ArrayList<>();
        this.localIdleRates = new double[period/Main.TIME_STEP];
        for( int i=0;i< (period/Main.TIME_STEP);i++ ){
            localSteps.add( new LinkedList<>() );
            localIdleRates[i] = 1.0;
        }
        
    }

    public List<List<Task>> getLocalSteps() {
        return localSteps;
    }

    public void setLocalSteps(List<List<Task>> localSteps) {
        this.localSteps = localSteps;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public double[] getLocalIdleRates() {
        return localIdleRates;
    }

    public void setLocalIdleRates(double[] localIdleRates) {
        this.localIdleRates = localIdleRates;
    }
    
}
