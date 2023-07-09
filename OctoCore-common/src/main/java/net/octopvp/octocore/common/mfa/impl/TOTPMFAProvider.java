package net.octopvp.octocore.common.mfa.impl;

import com.google.gson.JsonObject;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import net.octopvp.octocore.common.mfa.MFAData;
import net.octopvp.octocore.common.mfa.MFAProvider;
import net.octopvp.octocore.common.mfa.MFAType;

public class TOTPMFAProvider implements MFAProvider<TOTPMFAProvider.TOTPMFAData> {

    private final GoogleAuthenticator gAuth = new GoogleAuthenticator();
    private final GoogleAuthenticatorKey key = gAuth.createCredentials();

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public boolean isValid(String code) {
        // check if code is a integer
        try {
            Integer.parseInt(code.replace(" ", ""));
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public boolean isCorrect(String code, TOTPMFAData data) {
        return false;
    }

    @Override
    public MFAType getType() {
        return MFAType.TOTP;
    }

    public static class TOTPMFAData implements MFAData {

        @Override
        public MFAType getType() {
            return MFAType.TOTP;
        }

        @Override
        public JsonObject serialize() {
            return null;
        }

        @Override
        public MFAData deserialize(JsonObject object) {
            return null;
        }
    }
}
