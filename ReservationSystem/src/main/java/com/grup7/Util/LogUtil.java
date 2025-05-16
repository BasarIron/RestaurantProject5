package com.grup7.Util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class LogUtil {
    private static final String LOG_FILE = "log.txt";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void logToFile(String customerName, String customerSurname, String tableNumber, String reservationCode) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            LocalDateTime now = LocalDateTime.now(ZoneId.of("Europe/Istanbul"));
            writer.println("----------------------------------------");
            writer.println("Rezervasyon Kapandı");
            writer.println("Current Date and Time (UTC): " + now.format(formatter));
            writer.println("Müşteri: " + customerName + " " + customerSurname);
            writer.println("Masa Numarası: " + tableNumber);
            writer.println("Rezervasyon Kodu: " + reservationCode);
            writer.println("----------------------------------------\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}