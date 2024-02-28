package com.example.parser.services;

import com.opencsv.CSVWriter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

@Service
public class ParseProductService {

    private Map<String, String> getProductMap(Document document) {
        var products = document.select(".product_list li");

        Map<String, String> priceMapBySHU = new HashMap<>();

        for (Element product : products) {
            boolean hasVendorCode = product.text().contains("Артикул:");
            if (!hasVendorCode) continue;

            var price = product
                    .select(".price")
                    .text()
                    .replaceAll("[\\.\\s\\p{L}]", "")
                    .replace(",", ".");
            var vendorCode = product.select(".comments_note span").text();

            priceMapBySHU.put(vendorCode, price);
        }

        return priceMapBySHU;
    }

    public String toCSV() throws IOException {
        Document svarkaMMADocument = Jsoup.connect("https://hugongweld.ru/catalog/svarka_mma/").get();
        Document svarkaMigMagDocument = Jsoup.connect("https://hugongweld.ru/catalog/svarka_mig_mag/").get();
        Document svarkaTigDocument = Jsoup.connect("https://hugongweld.ru/catalog/svarka_tig/").get();
        Document plazmennayaRezkaCutDocument = Jsoup.connect("https://hugongweld.ru/catalog/plazmennaya_rezka_cut/").get();
        Document svarochnyeTraktoryDocument = Jsoup.connect("https://hugongweld.ru/catalog/svarochnye_traktory/").get();
        Document lazernayaSvarkaDocument = Jsoup.connect("https://hugongweld.ru/catalog/lazernaya_svarka/").get();

        Map<String, String> resultMap = new HashMap<>();

        resultMap.putAll(this.getProductMap(svarkaMMADocument));
        resultMap.putAll(this.getProductMap(svarkaMigMagDocument));
        resultMap.putAll(this.getProductMap(svarkaTigDocument));
        resultMap.putAll(this.getProductMap(plazmennayaRezkaCutDocument));
        resultMap.putAll(this.getProductMap(svarochnyeTraktoryDocument));
        resultMap.putAll(this.getProductMap(lazernayaSvarkaDocument));

        try (StringWriter stringWriter = new StringWriter();
             CSVWriter csvWriter = new CSVWriter(stringWriter)) {
            // Записываем заголовок
            csvWriter.writeNext(new String[]{"SKU", "Price"});

            // Записываем данные из HashMap в CSV файл
            for (String sku : resultMap.keySet()) {
                String[] line = new String[]{sku, resultMap.get(sku)};
                csvWriter.writeNext(line);
            }

            // Получаем CSV строку
            String csvString = stringWriter.toString();

            System.out.println("@@@@@@@@ CSV String: " + csvString);

            return csvString;
        } catch (Exception e) {
            e.fillInStackTrace();
        }

        return "CSV";
    }
}
