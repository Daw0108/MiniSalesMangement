package utils;

import model.Product;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

    public static boolean exportProductsToCSV(List<Product> products, String filePath) {
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(filePath), "UTF-8"))) {
            writer.write('\ufeff'); // Ký tự BOM cho UTF-8

            writer.println("Tên Sản Phẩm,Giá Bán,Số Lượng,Trạng Thái");

            for (Product p : products) {
                writer.println(String.format("%s,%f,%d,%s",
                        p.getName(), p.getPrice(), p.getStock(), p.getStatus()));
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<Product> importProductsFromCSV(String filePath) throws Exception {
        List<Product> products = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "UTF-8"))) {
            String line = reader.readLine(); // Đọc dòng đầu tiên (Header) và bỏ qua

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue; // Bỏ qua dòng trống

                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    Product p = new Product();
                    p.setId(0); // ID = 0 để Database tự động tạo mới
                    p.setName(parts[0].trim());
                    p.setPrice(Double.parseDouble(parts[1].trim()));
                    p.setStock(Integer.parseInt(parts[2].trim()));
                    p.setStatus(parts[3].trim());

                    products.add(p);
                }
            }
        }
        return products;
    }
}