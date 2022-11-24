package net.octopvp.octocore.master.saml;

import net.octopvp.octocore.master.saml.filter.CustomIDPAuthenticationRequestFilter;
import net.octopvp.octocore.master.saml.filter.CustomIDPInitiatedLoginFilter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.saml.provider.SamlServerConfiguration;
import org.springframework.security.saml.provider.identity.config.SamlIdentityProviderServerBeanConfiguration;

import javax.servlet.Filter;

@Configuration
public class SAMLConfig extends SamlIdentityProviderServerBeanConfiguration {
    private final SamlAppProperties config;

    public SAMLConfig(@Qualifier("samlAppProperties") SamlAppProperties config) {
        this.config = config;
    }

    @Override
    protected SamlServerConfiguration getDefaultHostSamlServerConfiguration() {
        return config;
    }



    @Bean
    public Filter idpInitatedLoginFilter() {
        return new CustomIDPInitiatedLoginFilter(getSamlProvisioning(), samlAssertionStore());
    }


    @Bean
    public Filter idpAuthnRequestFilter() {
        return new CustomIDPAuthenticationRequestFilter(getSamlProvisioning(), samlAssertionStore());
    }
}
