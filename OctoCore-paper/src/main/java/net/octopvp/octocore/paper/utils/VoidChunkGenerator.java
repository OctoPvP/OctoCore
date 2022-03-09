package net.octopvp.octocore.paper.utils;

import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

import java.util.Random;

public class VoidChunkGenerator extends ChunkGenerator {
    public byte[][] generateBlockSections(final World world, final Random random, final int chunkX, final int chunkZ, final ChunkGenerator.BiomeGrid biomeGrid) {
        final byte[][] result = new byte[world.getMaxHeight() / 16][];
        if (chunkX == 0 && chunkZ == 0) {
            this.setBlock(result, 0, 64, 0, (byte) 7);
        }
        return result;
    }

    void setBlock(final byte[][] result, final int x, final int y, final int z, final byte blkid) {
        if (result[y >> 4] == null) {
            result[y >> 4] = new byte[4096];
        }
        result[y >> 4][(y & 0xF) << 8 | z << 4 | x] = blkid;
    }
}
