package com.grup7.Controller;

import com.grup7.Entity.Table;
import com.grup7.Service.TableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rest/api/tables")
public class TableController {
    @Autowired
    private TableService tableService;

    @GetMapping
    public List<Table> getAllTables() {
        return tableService.getAllTables();
    }
}