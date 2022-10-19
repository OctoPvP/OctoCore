package net.octopvp.octocore.master.controller;

import net.octopvp.octocore.master.models.User;
import net.octopvp.octocore.master.payload.request.SignupRequest;
import net.octopvp.octocore.master.payload.response.MessageResponse;
import net.octopvp.octocore.master.payload.response.UserInfoResponse;
import net.octopvp.octocore.master.repository.MongoUserRepository;
import net.octopvp.octocore.master.services.JwtUtils;
import net.octopvp.octocore.master.services.UserDetailsImpl;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class LoginController {
    @Autowired
    JwtUtils jwtUtilsTop;
    @Autowired
    AuthenticationManager authenticationManagerTop;
    @PostMapping(value = "/loginform", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<?> authenticateUser(@Valid LoginRequest loginRequest) {
        System.out.println("Login request: " + loginRequest);
        Authentication authentication;
        try {
            authentication = authenticationManagerTop.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        } catch (AuthenticationException e) {
            String message = e.getMessage();
            if (message == null) message = "Invalid username or password";
            HttpHeaders headers = new HttpHeaders();
            headers.add("Location", "/login-error?message=" + message);
            return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT).headers(headers).build();
        }
        System.out.println("Authentication: " + authentication);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        //ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).toList();

        ResponseCookie jwt = jwtUtilsTop.generateJwtCookie(userDetails);
        System.out.println("JWT: " + jwt);
        // Redirect to /home and set the jwt cookie
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, jwt.toString());
        headers.add("Location", "/home");
        return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT).headers(headers).body("Logged in successfully!");
    }

    @CrossOrigin(origins = "*", maxAge = 3600)
    @RestController
    @RequestMapping("/api/auth")
    public class AuthController {
        @Autowired
        AuthenticationManager authenticationManager;

        @Autowired
        MongoUserRepository userRepository;

        @Autowired
        PasswordEncoder encoder;

        @Autowired
        JwtUtils jwtUtils;

        @PostMapping(value = "/login")
        public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
            System.out.println("Login request: " + loginRequest);
            Authentication authentication;
            try {
                authentication = authenticationManagerTop.authenticate(
                        new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
            } catch (AuthenticationException e) {
                String message = e.getMessage();
                if (message == null) message = "Invalid username or password";
                String json = "{\"success\": false, \"message\": \"" + message + "\"}";
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(json);
            }
            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            //ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            ResponseCookie jwt = jwtUtils.generateJwtCookie(userDetails);
            System.out.println("JWT: " + jwt);
            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwt.toString())
                    .body(new UserInfoResponse(true,
                            userDetails.getUserID().toString(),
                            userDetails.getUsername(),
                            jwt.getValue(),
                            roles));
        }

        @PostMapping("/signup")
        public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
            if (userRepository.existsByUsername(signUpRequest.getUsername())) {
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse(false, "Error: Username is already taken!"));
            }

            // Create new user's account
            User user = new User(signUpRequest.getUsername(),
                    encoder.encode(signUpRequest.getPassword()));

            Set<String> strRoles = signUpRequest.getRoles();

            if (strRoles == null) {
                strRoles.add("ROLE_USER");
            } else {
                strRoles.forEach(role -> {
                    switch (role) {
                        case "admin":
                            strRoles.add("ROLE_ADMIN");
                            break;
                        default:
                            strRoles.add("ROLE_USER");
                    }
                });
            }

            user.setRoles(strRoles);
            userRepository.save(user);

            return ResponseEntity.ok(new MessageResponse(true, "User registered successfully!"));
        }
    }

    @Getter
    @Setter
    public class LoginRequest {
        @NotBlank
        private String username;

        @NotBlank
        private String password;

        @Override
        public String toString() {
            return "LoginRequest{" +
                    "username='" + username + '\'' +
                    ", password='" + password + '\'' +
                    '}';
        }
    }

}
