package net.octopvp.octocore.common.mfa;

public interface MFAProvider<T extends MFAData> {
    boolean isEnabled();

    /**
     * Makes sure a code is valid (valid as in length, characters, etc, not as in if it's correct)
     *
     * @param code
     * @return
     */
    boolean isValid(String code);

    boolean isCorrect(String code, T data);

    MFAType getType();
}
