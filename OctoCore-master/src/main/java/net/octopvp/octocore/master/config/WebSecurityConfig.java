package net.octopvp.octocore.master.config;

import com.vaadin.flow.spring.security.AuthenticationContext;
import com.vaadin.flow.spring.security.VaadinWebSecurity;
import net.octopvp.octocore.master.saml.AppConfig;
import net.octopvp.octocore.master.saml.BeanConfig;
import net.octopvp.octocore.master.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.saml.provider.identity.config.SamlIdentityProviderSecurityConfiguration;
import org.springframework.security.saml.provider.identity.config.SamlIdentityProviderSecurityDsl;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@EnableWebSecurity
public class WebSecurityConfig {

    @Configuration
    @Order(1)
    public static class SamlSecurity extends SamlIdentityProviderSecurityConfiguration {

        private final AppConfig appConfig;
        private final BeanConfig beanConfig;

        public SamlSecurity(BeanConfig beanConfig, @Qualifier("appConfig") AppConfig appConfig) {
            super("/saml/idp/", beanConfig);
            this.appConfig = appConfig;
            this.beanConfig = beanConfig;
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            super.configure(http);

            http.userDetailsService(beanConfig.userDetailsService())
                    .formLogin();

            http.apply(SamlIdentityProviderSecurityDsl.identityProvider())
                    .configure(appConfig);
        }
    }

    @Configuration
    @Order(2)
    public static class AppSecurity extends VaadinWebSecurity {
        @Autowired
        private UserDetailsServiceImpl userDetailsService;
        private final BeanConfig beanConfig;

        public AppSecurity(BeanConfig beanConfig) {
            this.beanConfig = beanConfig;
        }

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
        protected void configure(WebSecurity web) throws Exception {
            super.configure(web);
        }

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
}
