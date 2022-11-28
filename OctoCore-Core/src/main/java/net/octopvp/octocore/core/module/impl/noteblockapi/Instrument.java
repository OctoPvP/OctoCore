package net.octopvp.octocore.core.module.impl.noteblockapi;

import net.octopvp.octocore.core.module.impl.noteblockapi.model.Sound;
import net.octopvp.octocore.core.module.impl.noteblockapi.utils.InstrumentUtils;

/**
 * @deprecated {@link InstrumentUtils}
 */
@Deprecated
public class Instrument {

    /**
     * Returns the org.bukkit.Sound enum for the current server version
     *
     * @param instrument
     * @return Sound enum (for the current server version)
     * @see Sound
     */
    public static org.bukkit.Sound getInstrument(byte instrument) {
        return org.bukkit.Sound.valueOf(getInstrumentName(instrument));
    }

    /**
     * Returns the name of the org.bukkit.Sound enum for the current server version
     *
     * @param instrument
     * @return Sound enum name (for the current server version)
     * @see Sound
     */
    public static String getInstrumentName(byte instrument) {
        return InstrumentUtils.getInstrumentName(instrument);
    }

    /**
     * Returns the name of the org.bukkit.Instrument enum for the current server version
     *
     * @param instrument
     * @return Instrument enum (for the current server version)
     */
    public static org.bukkit.Instrument getBukkitInstrument(byte instrument) {
        return InstrumentUtils.getBukkitInstrument(instrument);
    }

    /**
     * If true, the byte given represents a custom instrument
     *
     * @param instrument
     * @return whether the byte represents a custom instrument
     */
    public static boolean isCustomInstrument(byte instrument) {
        return InstrumentUtils.isCustomInstrument(instrument);
    }

    /**
     * Gets the first index in which a custom instrument
     * can be added to the existing list of instruments
     *
     * @return index where an instrument can be added
     */
    public static byte getCustomInstrumentFirstIndex() {
        return InstrumentUtils.getCustomInstrumentFirstIndex();
    }

}
