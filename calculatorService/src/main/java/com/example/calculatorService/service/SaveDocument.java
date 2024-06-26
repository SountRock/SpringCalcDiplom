package com.example.calculatorService.service;

import com.example.calculatorService.domain.funcvar.FuncVar;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public interface SaveDocument<T> {

    /**
     * Сохранить сущности
     * @param directory
     * @param entities
     * @return
     */
    default boolean saveDocument(String directory, String fileName, String filePreFormantValue, List<T> entities) {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(directory);
        file.mkdirs();
        try {
            file = new File(directory,
                    fileName + "." + filePreFormantValue +
                    ".json");

            mapper.writeValue(file, entities);

            return true;
        } catch (IOException | IndexOutOfBoundsException e){
            e.printStackTrace();

            return false;
        }
    }

    /**
     * Получить имена файлов в директории
     * @param directory
     * @return
     */
    default List<String> showFiles(String directory){
        File dir = new File(directory);
        List<String> list = new ArrayList<>();
        for (File file : dir.listFiles() ){
            list.add(file.getName());
        }

        return list;
    }
}
