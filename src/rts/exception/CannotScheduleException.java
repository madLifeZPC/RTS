/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rts.exception;

/**
 *
 * @author e0013405
 */
public class CannotScheduleException extends Exception{
    
    private final String message;

    public CannotScheduleException() {
        message = "The tasks can not be scheduled.";
    }

    public CannotScheduleException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
    
}
