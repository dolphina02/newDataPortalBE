package com.lina.dataportal.controller;

import com.lina.dataportal.service.TextToSqlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/text-to-sql")
@CrossOrigin(origins = "*")
public class TextToSqlController {

    @Autowired
    private TextToSqlService textToSqlService;

    @PostMapping("/generate")
    public ResponseEntity<Map<String, Object>> generateSQL(@RequestBody Map<String, Object> request) {
        String naturalLanguageQuery = (String) request.get("query");
        String model = (String) request.get("model");
        
        Map<String, Object> result = textToSqlService.generateSQL(naturalLanguageQuery, model);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/execute")
    public ResponseEntity<Map<String, Object>> executeSQL(@RequestBody Map<String, Object> request) {
        String sql = (String) request.get("sql");
        
        Map<String, Object> result = textToSqlService.executeSQL(sql);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/format")
    public ResponseEntity<Map<String, Object>> formatSQL(@RequestBody Map<String, Object> request) {
        String sql = (String) request.get("sql");
        
        String formattedSQL = textToSqlService.formatSQL(sql);
        return ResponseEntity.ok(Map.of("formattedSQL", formattedSQL));
    }

    @GetMapping("/examples")
    public ResponseEntity<List<String>> getExampleQueries() {
        List<String> examples = textToSqlService.getExampleQueries();
        return ResponseEntity.ok(examples);
    }

    @GetMapping("/chat-history")
    public ResponseEntity<List<Map<String, Object>>> getChatHistory() {
        List<Map<String, Object>> history = textToSqlService.getChatHistory();
        return ResponseEntity.ok(history);
    }

    @DeleteMapping("/chat-history")
    public ResponseEntity<Void> clearChatHistory() {
        textToSqlService.clearChatHistory();
        return ResponseEntity.ok().build();
    }
}