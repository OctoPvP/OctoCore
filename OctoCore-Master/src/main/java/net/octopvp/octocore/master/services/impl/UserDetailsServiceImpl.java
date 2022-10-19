package net.octopvp.aetheriacoremaster.services.impl;

import net.octopvp.aetheriacoremaster.config.HttpSessionConfig;
import net.octopvp.aetheriacoremaster.models.UserModel;
import net.octopvp.aetheriacoremaster.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Autowired(required = false)
    private HttpServletRequest request;

    @Autowired(required = false)
    private HttpSessionConfig sessionConfig;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserModel user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));
        UserDetailsImpl userDetails = UserDetailsImpl.build(user);
        if (request != null && sessionConfig != null) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                sessionConfig.updateUser(session, user.getId(), user.getUsername(), request);
            }
        }
        return userDetails;
    }

}
