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
public class Task {
    
    private String id;
    private Model model;

    public Task() {
    }

    public Task(String id, Model model) {
        this.id = id;
        this.model = model;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }
    
}
