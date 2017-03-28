/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rts.task;

/**
 *
 * @author e0013405
 */
public class Model {
    
    private String name;
    private int period;
    private double computationTime;

    public Model() {
    }

    public Model(String name, int period, double computationTime) {
        this.name = name;
        this.period = period;
        this.computationTime = computationTime;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public double getComputationTime() {
        return computationTime;
    }

    public void setComputationTime(double computationTime) {
        this.computationTime = computationTime;
    }
}
