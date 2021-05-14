package net.octopvp.octocore.paper.module.impl.auth;

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
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class AuthMapManager extends MapRenderer {
    private Player player;
    private byte[] bytes;
    public AuthMapManager(Player player,byte[] bytes){
        this.player = player;
        this.bytes = bytes;
    }
    public void giveMap(){
        ItemStack i = new ItemStack(Material.MAP, 1);
        MapView view = Bukkit.createMap(player.getWorld());
        view.getRenderers().clear();
        view.addRenderer(this);
        i.setDurability(view.getId());
    }

    @Override
    public void render(MapView mapView, MapCanvas mapCanvas, Player player) {
        Image image = null;
        try {
            image = ImageIO.read(new ByteArrayInputStream(bytes));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(!(image == null))
            mapCanvas.drawImage(0,0,image);
        else mapCanvas.drawText(1,1, MinecraftFont.Font,"Error!");
    }
}
