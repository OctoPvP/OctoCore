package net.octopvp.aetheriacoremaster.controllers.api;

import net.octopvp.aetheriacoremaster.repositories.APIRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/token")
public class TokenAPIController { //TODO implement a filter or something instead
    @Autowired
    private APIRepository repository;


    @RequestMapping("/status")
    public ResponseEntity<?> status() {
        return ResponseEntity.ok("{\"success\": true, \"message\": \"Token is valid.\"}");
    }


}
