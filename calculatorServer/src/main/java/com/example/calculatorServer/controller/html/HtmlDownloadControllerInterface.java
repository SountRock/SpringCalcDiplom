package com.example.calculatorServer.controller.html;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

public interface HtmlDownloadControllerInterface<T> {
    /**
     * Скачать файл
     * @param fileName
     * @param preFormantValue
     * @param response
     * @throws IOException
     */
    default void downloadFile(String fileName, String preFormantValue, HttpServletResponse response) throws IOException {
        try {
            List<T> list = getEntities();
            if(list!=null) {
                response.setContentType("application/octet-stream");
                String headerKey = "Content-Disposition";
                String headerValue;
                if(fileName.replaceAll(" ", "").equals("")) {
                    headerValue = "attachment; filename = " + LocalDate.now() + ".FL.json";
                } else {
                    headerValue = "attachment; filename = " + fileName + "." + preFormantValue + ".json";
                }
                response.setHeader(headerKey, headerValue);
                ServletOutputStream outputStream = response.getOutputStream();

                ObjectMapper mapper = new ObjectMapper();
                String json = mapper.writeValueAsString(list);

                outputStream.write(json.getBytes());
                outputStream.close();
            }
        } catch (NoSuchElementException e){}
    }

    /**
     * Получить сущности
     * @return
     */
    List<T> getEntities();

    /**
     * Получить обработаное значение сущности для добавления в репозиторий
     * @return
     */
    T getEntityForTable(T loadEntity);
}