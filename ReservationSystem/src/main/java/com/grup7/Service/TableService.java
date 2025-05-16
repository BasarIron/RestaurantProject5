package com.grup7.Service;

import com.grup7.Entity.Table;
import com.grup7.Repository.ITableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class TableService {
    @Autowired
    private ITableRepository tableRepository;

    // Belirli bir tarih için boş masa bulma
    public List<Table> getAvailableTables(LocalDate date) {
        return tableRepository.findByReservedDatesNotContaining(date);
    }

    // Masa rezervasyonu yapma
    public boolean reserveTable(Long tableId, LocalDate date) {
        Table table = tableRepository.findById(tableId)
                .orElseThrow(() -> new RuntimeException("Masa bulunamadı"));

        if (table.getReservedDates().contains(date)) {
            return false;
        }

        table.getReservedDates().add(date);
        tableRepository.save(table);
        return true;
    }

    // Rezervasyon iptali
    public void cancelReservation(Long tableId, LocalDate date) {
        Table table = tableRepository.findById(tableId)
                .orElseThrow(() -> new RuntimeException("Masa bulunamadı"));

        table.getReservedDates().remove(date);
        tableRepository.save(table);
    }
    public List<Table> getAllTables() {
        return tableRepository.findAll();
    }
}