package net.octopvp.octocore.master.config;

import com.vaadin.flow.spring.security.VaadinWebSecurityConfigurerAdapter;
import lombok.extern.slf4j.Slf4j;
import net.octopvp.octocore.master.component.AuthEntryPointJwt;
import net.octopvp.octocore.master.saml.SAMLConfig;
import net.octopvp.octocore.master.saml.SamlAppProperties;
import net.octopvp.octocore.master.services.UserDetailsServiceImpl;
import net.octopvp.octocore.master.views.pages.impl.login.LoginView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jose.jws.JwsAlgorithms;
import org.springframework.security.saml.provider.identity.config.SamlIdentityProviderSecurityConfiguration;

import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;

import static org.springframework.security.saml.provider.identity.config.SamlIdentityProviderSecurityDsl.identityProvider;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)
@SuppressWarnings({"unused", "deprecation"})
public class WebSecurityConfig extends VaadinWebSecurityConfigurerAdapter {


    @Configuration
    @Order(1)
    @Slf4j
    public static class SamlSecurity extends SamlIdentityProviderSecurityConfiguration {

        private final SamlAppProperties appProperties;
        private final SAMLConfig samlConfig;
        private UserDetailsServiceImpl userDetailsService;

        public SamlSecurity(SAMLConfig samlConfig, @Qualifier("samlAppProperties") SamlAppProperties appProperties, UserDetailsServiceImpl userDetailsService) throws IOException {
            super("/saml/idp/", samlConfig);
            this.appProperties = appProperties;
            this.samlConfig = samlConfig;
            this.userDetailsService = userDetailsService;
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            super.configure(http);
            http.
                    userDetailsService(userDetailsService)
                            .formLogin(httpSecurityFormLoginConfigurer -> httpSecurityFormLoginConfigurer.loginPage("/login"));
        //.formLogin();
            //http.authorizeRequests()
            //                .antMatchers("/saml/**").permitAll();
            http.
                    apply(identityProvider())
                    .configure(appProperties);
        }
    }
    public static final String LOGOUT_URL = "/";

    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;
    @Value("${auth.secret}")
    private String secretKey;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(@Autowired AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(bCryptPasswordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        /*
        http
                .cors().and().csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests()
                .antMatchers("/").permitAll()
                .antMatchers("/api/auth/**").permitAll()
                .antMatchers("/api/check/**").permitAll()
                .antMatchers("/login", "/login-error").permitAll()
                .antMatchers("/css/**").permitAll()
                .antMatchers("/img/**").permitAll()
                .antMatchers("/public/**").permitAll()
                .anyRequest().authenticated().and()
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
        ;
        http
                .logout()
                .logoutUrl("/api/auth/logout")
                .permitAll()
                .logoutSuccessHandler((httpServletRequest, httpServletResponse, authentication) -> {
                    httpServletResponse.setStatus(HttpServletResponse.SC_OK);
                });
         */
        http.authorizeRequests()
                .antMatchers("/img/**", "/dist/**", "/js/**", "/css/**", "/saml/**").permitAll();

        super.configure(http);


        //http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        setLoginView(http, LoginView.class);
        setStatelessAuthentication(http, new SecretKeySpec(secretKey.getBytes(), JwsAlgorithms.HS256), "OctoCore");
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return userDetailsService;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
