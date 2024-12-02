package org.example.scraper.service;

import com.opencsv.CSVWriter;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;

@Service
public class CSVService {
    private static final String CSV_FILE_PATH = "products.csv";

    public void saveToCSV(String jsonData) throws IOException {
        JSONObject json = new JSONObject(jsonData);
        String[] header = {"Name", "Price"};
        String[] data = {json.getString("name"), json.getString("price")};

        try (CSVWriter writer = new CSVWriter(new FileWriter(CSV_FILE_PATH, true))) {
            if (new java.io.File(CSV_FILE_PATH).length() == 0) {
                writer.writeNext(header);
            }
            writer.writeNext(data);
        }
    }
}
