package net.octopvp.octocore.master.config;

import com.vaadin.flow.spring.security.VaadinWebSecurity;
import net.octopvp.octocore.master.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.saml2.provider.service.metadata.OpenSamlMetadataResolver;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.web.DefaultRelyingPartyRegistrationResolver;
import org.springframework.security.saml2.provider.service.web.Saml2MetadataFilter;
import org.springframework.security.saml2.provider.service.web.authentication.Saml2WebSsoAuthenticationFilter;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.ForwardedHeaderFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends VaadinWebSecurity {
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    @Autowired
    private RelyingPartyRegistrationRepository relyingPartyRegistrationRepository;
    @Autowired
    private Saml2LoginSettings settings;
    @Autowired
    private Saml2LogoutSettings logoutSettings;

    @Value("${master.saml.discovery}")
    private String registrationsStr;

    @Bean(name = "VaadinSecurityFilterChainBean")
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable().cors().disable();

        DefaultRelyingPartyRegistrationResolver relyingPartyRegistrationResolver = new DefaultRelyingPartyRegistrationResolver(this.relyingPartyRegistrationRepository);
        Saml2MetadataFilter filter = new Saml2MetadataFilter(relyingPartyRegistrationResolver, new OpenSamlMetadataResolver());
        String[] allowPaths = {
                "/favicon.ico",
                "/VAADIN/**",
                "/auth/**",
                "/*.js",
                "/*.css",
                "/offline-stub.html",
                "/login/**",
        };
        RequestMatcher[] matchers = new RequestMatcher[allowPaths.length];
        for (int i = 0; i < allowPaths.length; i++) {
            matchers[i] = new AntPathRequestMatcher(allowPaths[i]);
        }
        http
                .headers()
                .frameOptions().sameOrigin()
                .and()
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(matchers).permitAll()
                        .anyRequest().authenticated()
                )
                .saml2Login(settings)
                .saml2Logout(logoutSettings)
                // .addFilterBefore(new ForwardedHeaderFilter(), Saml2WebSsoAuthenticationFilter.class)
                // .addFilterBefore(filter, ForwardedHeaderFilter.class)
                .addFilterBefore(filter, Saml2WebSsoAuthenticationFilter.class)
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/auth/logout")
                .invalidateHttpSession(false)
                .clearAuthentication(false)
        ;
        /*
        http.addFilterAfter((request, response, chain) -> {
            if (request instanceof HttpServletRequest req) {
                System.out.println("Request: " + (req.getMethod() + " " + req.getRequestURI()));
            }
            chain.doFilter(request, response);
        }, Saml2WebSsoAuthenticationFilter.class);
         */
        setLoginView(http, "/login");
        // setLoginView(http, LoginPage.class);
        DefaultSecurityFilterChain chain = http.build();
        return chain;
    }

    @Override
    protected void configure(WebSecurity web) throws Exception {
        super.configure(web);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        filterChain(http);
    }

    @Bean
    public PasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
