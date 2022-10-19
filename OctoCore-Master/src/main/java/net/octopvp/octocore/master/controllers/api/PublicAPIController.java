package net.octopvp.aetheriacoremaster.controllers.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pub/")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PublicAPIController {
    @GetMapping("/status")
    public ResponseEntity<?> status() {
        return ResponseEntity.ok("{\"success\": true, \"message\": \"OK\", \"code\": 200}");
    }
}
