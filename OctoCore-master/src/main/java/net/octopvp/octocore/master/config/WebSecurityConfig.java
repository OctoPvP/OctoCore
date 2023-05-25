package net.octopvp.octocore.master.config;

import com.vaadin.flow.spring.security.VaadinWebSecurity;
import jakarta.servlet.FilterChain;
import net.octopvp.octocore.master.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static org.springframework.security.config.Customizer.withDefaults;

@EnableWebSecurity
@Configuration
public class WebSecurityConfig extends VaadinWebSecurity {
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Value("${auth.secret}")
    private String secretKey;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

        /*
        @Override
        protected void configure(@Autowired AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(userDetailsService)
                    .passwordEncoder(bCryptPasswordEncoder());
        }
         */

    @Override
    protected void configure(HttpSecurity http) throws Exception {
            /*
            http.antMatcher("/**")
                    .authorizeRequests()
                    .antMatchers("/saml/**", "/dist/**", "/css/**", "/img/**", "/js/**", "/VAADIN/**").permitAll()
                    .antMatchers("/**").authenticated()
                    .and()
                    .userDetailsService(userDetailsService)

                    .formLogin()
                    .loginPage("/login")
                    .permitAll()
            ;
             */
        String[] permitAll = new String[]{
                "/saml/**", "/dist/**", "/css/**", "/img/**", "/js/**", "/VAADIN/**"
        };
        for (String s : permitAll) {
            http.authorizeHttpRequests().requestMatchers(new AntPathRequestMatcher(s)).permitAll();
        }
        http.authorizeHttpRequests().requestMatchers(new AntPathRequestMatcher("/**")).authenticated()
                .and()
                .userDetailsService(userDetailsService).formLogin().loginPage("/login").permitAll();
        super.configure(http);
        //setLoginView(http, LoginView.class);
        //setStatelessAuthentication(http, new SecretKeySpec(secretKey.getBytes(), JwsAlgorithms.HS256), "OctoCore");

    }

        /*
        @Bean
        @Override
        public AuthenticationManager authenticationManagerBean() throws Exception {
            return super.authenticationManagerBean();
        }
         */

    public UserDetailsServiceImpl getUserDetailsService() {
        return userDetailsService;
    }

}
