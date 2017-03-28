/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rts.task;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 *
 * @author e0013405
 */
public class TaskFactory {

    public static List<Task> generateTasks(int total, List<Model> models) {
        List<Task> tasks = new LinkedList<>();
        for (int i = 0; i < total; i++) {
            Random random = new Random();
            int min = 0;
            int max = models.size() - 1;
            int s = random.nextInt(max) % (max - min + 1) + min;
            tasks.add(new Task(UUID.randomUUID().toString(), models.get(s)));
        }
        return tasks;
    }

}
