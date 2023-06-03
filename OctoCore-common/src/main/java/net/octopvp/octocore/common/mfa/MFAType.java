package net.octopvp.octocore.common.mfa;

public enum MFAType {
    TOTP("totp"),
    SECURITY_KEY("security_key");

    private String type;
    MFAType(String type) {
        this.type = type;
    }

    public static MFAType findType(String key) {
        for (MFAType type : values()) {
            if (type.getType().equalsIgnoreCase(key) || type.name().equalsIgnoreCase(key)) {
                return type;
            }
        }
        return null;
    }

    public String getType() {
        return type;
    }
}
