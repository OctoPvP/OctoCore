package net.octopvp.octocore.common.mfa;

import lombok.Getter;
import net.octopvp.octocore.common.mfa.impl.TOTPMFAProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MFAManager {
    @Getter
    private static final MFAManager instance = new MFAManager();

    private MFAManager() {
    }

    private final List<MFAProvider<? extends MFAData>> providers = new ArrayList<>(Collections.singletonList(
            new TOTPMFAProvider()
    ));

    public <T extends MFAData> MFAProvider<T> getProvider(MFAType type) {
        for (MFAProvider<? extends MFAData> provider : providers) {
            if (provider.getType() == type) {
                return (MFAProvider<T>) provider;
            }
        }
        return null;
    }

    public <T extends MFAData> boolean validateCode(String code, List<T> dataList) {
        for (T mfaData : dataList) {
            MFAProvider<T> provider = getProvider(mfaData.getType());
            boolean correct = false;
            if (provider != null && provider.isValid(code)) {
                correct = provider.isCorrect(code, mfaData);
                if (correct) return true;
                continue;
            }
        }
        return false;
    }
}
