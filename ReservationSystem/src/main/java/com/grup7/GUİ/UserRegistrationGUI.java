package com.grup7.GUİ;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;

public class UserRegistrationGUI extends JFrame {
    private JTextField nameField;
    private JTextField surnameField;
    private JComboBox<String> dayBox;
    private JComboBox<String> monthBox;
    private JComboBox<String> yearBox;
    private JLabel reservationCodeLabel;
    private JButton copyButton; // Kopyalama butonu
    private String currentReservationCode; // Mevcut rezervasyon kodunu tutmak için

    public UserRegistrationGUI() {
        setTitle("Rezervasyon Sistemi");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Form paneli
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(5, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // İsim ve soyisim alanları
        formPanel.add(new JLabel("İsim:"));
        nameField = new JTextField();
        formPanel.add(nameField);

        formPanel.add(new JLabel("Soyisim:"));
        surnameField = new JTextField();
        formPanel.add(surnameField);

        // Tarih seçimi
        formPanel.add(new JLabel("Tarih:"));
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // ComboBox'lar için veri hazırlığı
        String[] days = new String[31];
        for(int i = 1; i <= 31; i++) {
            days[i-1] = String.format("%02d", i);
        }
        dayBox = new JComboBox<>(days);

        String[] months = {"01", "02", "03", "04", "05", "06",
                "07", "08", "09", "10", "11", "12"};
        monthBox = new JComboBox<>(months);

        String[] years = {"2024", "2025", "2026"};
        yearBox = new JComboBox<>(years);

        datePanel.add(dayBox);
        datePanel.add(new JLabel("/"));
        datePanel.add(monthBox);
        datePanel.add(new JLabel("/"));
        datePanel.add(yearBox);
        formPanel.add(datePanel);

        // Rezervasyon kodu ve kopyalama butonu paneli
        JPanel reservationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        formPanel.add(new JLabel("Rezervasyon Kodunuz:"));

        reservationCodeLabel = new JLabel("Henüz rezervasyon yapılmadı");
        reservationCodeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        reservationCodeLabel.setForeground(Color.BLUE);
        reservationPanel.add(reservationCodeLabel);

        // Kopyalama butonu
        copyButton = new JButton("Kopyala");
        copyButton.setEnabled(false); // Başlangıçta devre dışı
        copyButton.addActionListener(e -> kopyalaReservationCode());
        copyButton.setIcon(UIManager.getIcon("FileView.floppyDriveIcon")); // Sistem ikonu
        reservationPanel.add(copyButton);

        formPanel.add(reservationPanel);

        // Butonlar
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton submitButton = new JButton("Rezervasyon Yap");
        submitButton.addActionListener(e -> kullaniciEkle());
        buttonPanel.add(submitButton);

        // Ana pencereye ekleme
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void kopyalaReservationCode() {
        if (currentReservationCode != null && !currentReservationCode.isEmpty()) {
            StringSelection stringSelection = new StringSelection(currentReservationCode);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);

            // Kopyalama animasyonu
            copyButton.setBackground(Color.GREEN);
            copyButton.setText("Kopyalandı!");

            // 2 saniye sonra butonu eski haline getir
            Timer timer = new Timer(2000, e -> {
                copyButton.setBackground(null);
                copyButton.setText("Kopyala");
            });
            timer.setRepeats(false);
            timer.start();
        }
    }

    private void kullaniciEkle() {
        try {
            String date = String.format("%s/%s/%s",
                    dayBox.getSelectedItem(),
                    monthBox.getSelectedItem(),
                    yearBox.getSelectedItem()
            );

            String jsonBody = String.format("""
                {
                    "name": "%s",
                    "surname": "%s",
                    "date": "%s"
                }""",
                    nameField.getText(),
                    surnameField.getText(),
                    date
            );

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/rest/api/users/add"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 201 || response.statusCode() == 200) {
                String responseBody = response.body();
                currentReservationCode = extractReservationCode(responseBody);

                if (currentReservationCode != null) {
                    reservationCodeLabel.setText(currentReservationCode);
                    copyButton.setEnabled(true); // Kopyalama butonunu aktif et

                    JOptionPane.showMessageDialog(this,
                            "Rezervasyon başarıyla oluşturuldu!\nRezarvasyon Kodunuz: " + currentReservationCode +
                                    "\n(Kopyalamak için 'Kopyala' butonunu kullanabilirsiniz)",
                            "Başarılı",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    reservationCodeLabel.setText("Kod alınamadı");
                    copyButton.setEnabled(false);
                    JOptionPane.showMessageDialog(this,
                            "Rezervasyon oluşturuldu fakat kod alınamadı.",
                            "Uyarı",
                            JOptionPane.WARNING_MESSAGE);
                }
                formuTemizle();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Hata: Rezervasyon oluşturulamadı!\nStatus Code: " + response.statusCode() +
                                "\nResponse: " + response.body(),
                        "Hata",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Hata: " + ex.getMessage(),
                    "Hata",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private String extractReservationCode(String responseBody) {
        try {
            int startIndex = responseBody.indexOf("\"reservationCode\":\"") + "\"reservationCode\":\"".length();
            int endIndex = responseBody.indexOf("\"", startIndex);
            return responseBody.substring(startIndex, endIndex);
        } catch (Exception e) {
            return null;
        }
    }

    private void formuTemizle() {
        nameField.setText("");
        surnameField.setText("");
        dayBox.setSelectedIndex(0);
        monthBox.setSelectedIndex(0);
        yearBox.setSelectedIndex(0);
        // Rezervasyon kodu ve kopyalama butonu durumunu koruyoruz
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            UserRegistrationGUI gui = new UserRegistrationGUI();
            gui.setVisible(true);
        });
    }
}