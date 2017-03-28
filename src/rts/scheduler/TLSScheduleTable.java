/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rts.scheduler;

import java.util.ArrayList;
import java.util.List;
import rts.Main;

/**
 *
 * @author e0013405
 */
public class TLSScheduleTable {
    
    private List<TLSStep> table;
   
    public TLSScheduleTable(int maxPeriod) {
        this.table = new ArrayList<>();
        int period = Main.TIME_STEP;
        while( period<= maxPeriod ){
            table.add( new TLSStep(period) );
            period = period * 2;
        }
    }

    public List<TLSStep> getTable() {
        return table;
    }

    public void setTable(List<TLSStep> table) {
        this.table = table;
    }
    
    public TLSStep findStepByPeriod( int period ){
        for( TLSStep step : table ){
            if( step.getPeriod()== period ){
                return step;
            }
        }
        return null;
    }

}
