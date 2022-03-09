package net.octopvp.octocore.paper.module.impl.auth;

import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.map.MinecraftFont;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class AuthMapManager extends MapRenderer {
    private final Player player;
    private final byte[] bytes;

    public AuthMapManager(Player player, byte[] bytes) {
        this.player = player;
        this.bytes = bytes;
    }

    public void giveMap() {
        ItemStack i = new ItemBuilder(Material.MAP).name(CC.GREEN + "2fa QR Code").build();
        MapView view = Bukkit.createMap(player.getWorld());
        for (MapRenderer renderer : view.getRenderers()) {
            view.removeRenderer(renderer);
        }
        view.addRenderer(this);
        i.setDurability(view.getId());
        player.getInventory().addItem(i);
    }

    @Override
    public void render(MapView mapView, MapCanvas mapCanvas, Player player) {
        BufferedImage image = null;
        BufferedImage resized = null;
        try {
            image = ImageIO.read(new ByteArrayInputStream(bytes));
            resized = new BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB);
            final Graphics2D g = resized.createGraphics();
            g.drawImage(image, 0, 0, 128, 128, null);
            g.dispose();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!(resized == null))
            mapCanvas.drawImage(0, 0, resized);
        else mapCanvas.drawText(1, 1, MinecraftFont.Font, "Error!");
    }
}
