package net.octopvp.octocore.paper.module.impl.noteblockapi;

import net.octopvp.octocore.paper.module.impl.noteblockapi.model.Sound;

/**
 * @deprecated {@link net.octopvp.octocore.paper.module.impl.noteblockapi.model.CustomInstrument}
 */
@Deprecated
public class CustomInstrument {

    private final byte index;
    private final String name;
    private final String soundFileName;
    private org.bukkit.Sound sound;

    /**
     * Creates a CustomInstrument
     *
     * @param index
     * @param name
     * @param soundFileName
     */
    public CustomInstrument(byte index, String name, String soundFileName) {
        this.index = index;
        this.name = name;
        this.soundFileName = soundFileName.replaceAll(".ogg", "");
        if (this.soundFileName.equalsIgnoreCase("pling")) {
            this.sound = Sound.NOTE_PLING.bukkitSound();
        }
    }

    /**
     * Gets index of CustomInstrument
     *
     * @return index
     */
    public byte getIndex() {
        return index;
    }

    /**
     * Gets name of CustomInstrument
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the org.bukkit.Sound enum for this CustomInstrument
     *
     * @return org.bukkit.Sound enum
     */
    public org.bukkit.Sound getSound() {
        return sound;
    }

    /**
     * Gets file name of the sound
     *
     * @deprecated misleading name.
     */
    public String getSoundfile() {
        return soundFileName;
    }

}
