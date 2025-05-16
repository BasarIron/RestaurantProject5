package com.grup7.Config;

import com.grup7.Entity.Table;
import com.grup7.Repository.ITableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class TableInitializer implements CommandLineRunner {

    @Autowired
    private ITableRepository tableRepository;

    @Override
    public void run(String... args) {
        if (tableRepository.count() == 0) {
            for (int i = 1; i <= 10; i++) {
                Table table = new Table();
                table.setTableNumber("Masa-"+i);
                table.setReservedDates(new ArrayList<>());
                tableRepository.save(table);
            }
        }
    }
}