package net.octopvp.octocore.master.config;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.saml2.Saml2LogoutConfigurer;
import org.springframework.stereotype.Component;

@Component
public class Saml2LogoutSettings implements Customizer<Saml2LogoutConfigurer<HttpSecurity>> {

    @Override
    public void customize(Saml2LogoutConfigurer<HttpSecurity> saml2) {
        saml2
                .logoutRequest((request) -> request.logoutUrl("/logout"))
                .logoutResponse((response) -> response.logoutUrl("/logout"));
        saml2.logoutUrl("/logout");
    }
}
