package net.octopvp.octocore.master.saml;

import lombok.SneakyThrows;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.saml.key.SimpleKey;
import org.springframework.security.saml.provider.SamlServerConfiguration;
import org.springframework.security.saml.provider.identity.config.LocalIdentityProviderConfiguration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

@ConfigurationProperties(prefix = "saml2")
@Configuration
public class SamlAppProperties extends SamlServerConfiguration {

    @SneakyThrows
    @Override
    public SamlServerConfiguration setIdentityProvider(LocalIdentityProviderConfiguration identityProvider) {

        SimpleKey simpleKey = identityProvider.getKeys().getActive();
        if (!simpleKey.getCertificate().startsWith("|") && !simpleKey.getCertificate().startsWith("-----BEGIN")) {
            simpleKey.setCertificate(parsePEMFile(simpleKey.getCertificate()));
        }
        if (!simpleKey.getPrivateKey().startsWith("|") && !simpleKey.getPrivateKey().startsWith("-----BEGIN")) {
            simpleKey.setPrivateKey(parsePEMFile(simpleKey.getPrivateKey()));
        }
        return super.setIdentityProvider(identityProvider);
    }
    public static String parsePEMFile(String pemFilePath) throws IOException {
        File pemFile = new File(pemFilePath);
        if (!pemFile.isFile() || !pemFile.exists()) {
            throw new FileNotFoundException(String.format("The file '%s' doesn't exist.", pemFile.getAbsolutePath()));
        }
        StringBuilder content = new StringBuilder();
        Scanner scanner = new Scanner(pemFile);
        while (scanner.hasNextLine()) {
            content.append(scanner.nextLine());
            content.append("\n");
        }
        scanner.close();

        return content.toString();
    }
}

