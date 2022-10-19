package net.octopvp.aetheriacoremaster.controllers.api;

import com.vexsoftware.votifier.model.Vote;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.octopvp.aetheriacore.common.AetheriaCoreCommon;
import net.octopvp.aetheriacore.common.redis.RedisManager;
import net.octopvp.aetheriacoremaster.components.LightningHolder;
import net.octopvp.aetheriacoremaster.interceptor.RevokedTokenInterceptor;
import net.octopvp.aetheriacoremaster.master.votifier.VotifierEvent;
import net.octopvp.aetheriacoremaster.models.APIKey;
import net.octopvp.aetheriacoremaster.models.UserModel;
import net.octopvp.aetheriacoremaster.repositories.APIRepository;
import net.octopvp.aetheriacoremaster.repositories.UserRepository;
import net.octopvp.aetheriacoremaster.services.impl.UserDetailsImpl;
import net.octopvp.aetheriacoremaster.stereotypes.CurrentUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/authenticated/")
@CrossOrigin(origins = "*", maxAge = 3600)
// Maybe do @PreAuthorized("isFullyAuthenticated()") ?
public class AuthenticatedAPIController {
    @Autowired
    private UserRepository userRepository;

    private static final String SUCCESS = "{\"success\": true, \"message\": \"OK\", \"code\": 200}";

    @GetMapping("/test")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok(SUCCESS);
    }

    @PostMapping("/profile/set-image")
    public ResponseEntity<?> uploadProfileImage(@CurrentUser UserDetailsImpl user, @RequestBody ProfileRequest base64) {
        //System.out.println("Uploading profile image for user: " + user.getUsername() + " | " + base64.base64);
        user.setProfilePicture(base64.base64);
        Optional<UserModel> optional = userRepository.findById(user.getId());
        if (optional.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }
        UserModel userModel = optional.get();
        userModel.setProfilePicture(base64.base64);
        userRepository.save(userModel);
        return ResponseEntity.ok().build();
    }

    @Autowired
    private LightningHolder lightningHolder;

    @PostMapping("/votes/testvote")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<?> testVote(@RequestBody VoteRequest voterequest) {
        Vote vote = new Vote(voterequest.service == null || voterequest.service.isEmpty() ? "Test" : voterequest.service, voterequest.player, "0.0.0.0", Long.toString(System.currentTimeMillis(), 10));
        vote.setUsername(voterequest.player);
        lightningHolder.getEventBus().callEvent(new VotifierEvent(vote));
        return ResponseEntity.ok(SUCCESS);
    }

    @PostMapping("/sessions/revoke")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> revokeSession(@RequestBody RevokeSessionRequest request) {
        System.out.println("[REQ]: " + request.session);
        if (request.session == null || request.session.isEmpty()) {
            return ResponseEntity.badRequest().body("No Session provided");
        }
        RevokedTokenInterceptor.getRevokedTokens().add(request.session);
        return ResponseEntity.ok(SUCCESS);
    }

    @PostMapping("/redis/post")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DEV')")
    public ResponseEntity<?> postToRedis(@RequestBody RedisRequest request) {
        System.out.println("[REQ-REDIS]: " + request.json);
        if (request.json == null || request.json.isEmpty()) {
            return ResponseEntity.badRequest().body("No JSON provided");
        }
        RedisManager redisManager = AetheriaCoreCommon.getInstance().getRedisManager();
        redisManager.write(request.json);
        return ResponseEntity.ok(SUCCESS);
    }

    @Autowired
    private APIRepository apiRepository;

    @PostMapping("/apikey/revoke")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DEV')")
    public ResponseEntity<?> revokeAPIKey(@RequestBody ApiKeyRequest request) {
        apiRepository.deleteById(request.key);
        return ResponseEntity.ok(SUCCESS);
    }

    @PostMapping("/apikey/create")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DEV')")
    public ResponseEntity<?> createAPIKey(@CurrentUser UserDetailsImpl user, @RequestBody ApiKeyRequest request) {
        String key = request.key == null || request.key.isEmpty() ? UUID.randomUUID().toString() : request.key;
        if (apiRepository.findById(key).isPresent()) {
            return ResponseEntity.badRequest().body("Key already exists");
        }
        Optional<UserModel> optional = userRepository.findById(user.getId());
        if (optional.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }
        APIKey apiKey = new APIKey(key, System.currentTimeMillis(), optional.get());
        apiRepository.save(apiKey);
        return ResponseEntity.ok(SUCCESS);
    }


    //@formatter:off // https://www.jetbrains.com/help/idea/reformat-and-rearrange-code.html#exclude_part_of_code Make sure to enable this
    @AllArgsConstructor @NoArgsConstructor public static class RedisRequest { public String json; }
    @AllArgsConstructor @NoArgsConstructor public static class ApiKeyRequest { public String key; }
    @AllArgsConstructor @NoArgsConstructor public static class RevokeSessionRequest { public String session; }
    @AllArgsConstructor @NoArgsConstructor @Getter public static class VoteRequest { private String service; private String player; }
    @AllArgsConstructor @NoArgsConstructor public static class ProfileRequest { private String base64; public String getBase64() { return base64; }}
    //@formatter:on
}
