/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rts;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import rts.task.Model;

/**
 *
 * @author e0013405
 */
public class Initiator {

    private static final String FILE_DIR = "data";
    private static final String MODEL_FILE_NAME = "model.txt";
    private static final String CONFIG_FILE_NAME = "config.txt";

    public static List<Model> InitiateModels() {
        List<Model> models = new ArrayList<>();
        ArrayList<String> fileContent = getFileContent(MODEL_FILE_NAME);
        fileContent.stream().forEach(item -> {
            String[] attrs = item.split(",");
            models.add( new Model(attrs[0], Integer.parseInt(attrs[1]), Double.parseDouble(attrs[2])) );
        });
        return models;
    }
    
    public static void InitiateConfig(){
        ArrayList<String> fileContent = getFileContent(CONFIG_FILE_NAME);
        fileContent.stream().forEach(item -> {
            String[] attrs = item.split(":");
            if( attrs[0].equals("NUMBER_OF_TASK")){
                Main.NUMBER_OF_TASK = Integer.parseInt(attrs[1]);
            }
            else if(attrs[0].equals("NUMBER_OF_DESTROYED")){
                Main.NUMBER_OF_DESTROYED = Integer.parseInt(attrs[1]);
            }
            else if(attrs[0].equals("NUMBER_OF_ADDED")){
                Main.NUMBER_OF_ADDED = Integer.parseInt(attrs[1]);
            }
            else if(attrs[0].equals("TIME_STEP")){
                Main.TIME_STEP = Integer.parseInt(attrs[1]);
            }
            else if(attrs[0].equals("SIMULATION_TIME")){
                Main.SIMULATION_TIME = Long.parseLong(attrs[1])*60*1000;
            }
            else if(attrs[0].equals("THRESHOLD")){
                Main.THRESHOLD = Double.parseDouble(attrs[1]);
            }
            else{
                Main.TIME_INTERVAL = Integer.parseInt(attrs[1]);
            }
        });
    }

    private static ArrayList<String> getFileContent( String fileName ) {
        ArrayList<String> fileContent = new ArrayList<>();
        Path filePath = Paths.get(FILE_DIR, fileName);
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;

        try {
            fileReader = new FileReader(filePath.toString());
            bufferedReader = new BufferedReader(fileReader);
            String buffer;
            while ((buffer = bufferedReader.readLine()) != null) {
                if (!buffer.equals("")) {
                    fileContent.add(buffer);
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                bufferedReader.close();
                fileReader.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }

        return fileContent;
    }

}
