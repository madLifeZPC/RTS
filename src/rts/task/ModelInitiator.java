/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rts.task;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author e0013405
 */
public class ModelInitiator {

    private static final String FILE_DIR = "model";
    private static final String FILE_NAME = "model.txt";

    public static List<Model> getModels() {
        List<Model> models = new ArrayList<>();
        ArrayList<String> fileContent = getFileContent();
        fileContent.stream().forEach(item -> {
            String[] attrs = item.split(",");
            models.add( new Model(attrs[0], Integer.parseInt(attrs[1]), Double.parseDouble(attrs[2])) );
        });
        return models;
    }

    private static ArrayList<String> getFileContent() {
        ArrayList<String> fileContent = new ArrayList<>();
        Path filePath = Paths.get(FILE_DIR, FILE_NAME);
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
