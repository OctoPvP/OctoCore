package net.octopvp.octocore.paper.hologram;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.octopvp.octocore.common.object.tuple.Pair;
import net.octopvp.octocore.paper.hologram.packets.HologramPacket;
import net.octopvp.octocore.paper.hologram.packets.HologramPacketProvider;
import net.octopvp.octocore.paper.hologram.packets.v1_8.Minecraft18HologramPacketProvider;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class BaseHologram implements Hologram {
  private Collection<UUID> viewers;
  
  protected Location location;
  
  protected Collection<UUID> getViewers() {
    return this.viewers;
  }
  
  protected List<HologramLine> lastLines = new ArrayList<>();
  
  protected List<HologramLine> lines = new ArrayList<>();
  
  protected final Set<UUID> currentWatchers;
  
  protected static final double distance = 0.23D;
  
  public void send() {
    Collection<UUID> viewers = this.viewers;
    if (viewers == null)
      viewers = (Collection<UUID>)ImmutableList.copyOf(Bukkit.getServer().getOnlinePlayers()).stream().map(Entity::getUniqueId).collect(Collectors.toSet()); 
    for (UUID uuid : viewers) {
      Player player = Bukkit.getPlayer(uuid);
      if (player != null && player.isOnline())
        show(player); 
    } 
    HologramRegistry.getHolograms().add(this);
  }
  
  public void destroy() {
    Collection<UUID> viewers = this.viewers;
    if (viewers == null)
      viewers = (Collection<UUID>)ImmutableList.copyOf(Bukkit.getServer().getOnlinePlayers()).stream().map(Entity::getUniqueId).collect(Collectors.toSet()); 
    for (UUID uuid : viewers) {
      Player player = Bukkit.getPlayer(uuid);
      if (player != null && player.isOnline())
        destroy0(player); 
    } 
    if (this.viewers != null)
      this.viewers.clear(); 
    HologramRegistry.getHolograms().remove(this);
  }
  
  public void addLines(String... lines) {
    for (String line : lines)
      this.lines.add(new HologramLine(line)); 
    update();
  }
  
  public void setLine(int index, String line) {
    if (index > this.lines.size() - 1) {
      this.lines.add(new HologramLine(line));
    } else if (this.lines.get(index) != null) {
      ((HologramLine)this.lines.get(index)).setText(line);
    } else {
      this.lines.set(index, new HologramLine(line));
    } 
    update();
  }
  
  public void setLines(Collection<String> lines) {
    Collection<UUID> viewers = this.viewers;
    if (viewers == null)
      viewers = (Collection<UUID>)ImmutableList.copyOf(Bukkit.getServer().getOnlinePlayers()).stream().map(Entity::getUniqueId).collect(Collectors.toSet()); 
    for (UUID uuid : viewers) {
      Player player = Bukkit.getPlayer(uuid);
      if (player != null && player.isOnline())
        destroy0(player); 
    } 
    this.lines.clear();
    for (String line : lines)
      this.lines.add(new HologramLine(line)); 
    update();
  }
  
  public List<String> getLines() {
    List<String> lines = new ArrayList<>();
    for (HologramLine line : this.lines)
      lines.add(line.getText()); 
    return lines;
  }
  
  public Location getLocation() {
    return this.location;
  }
  
  protected List<HologramLine> rawLines() {
    return this.lines;
  }
  
  protected BaseHologram(HologramBuilder builder) {
    this.currentWatchers = new HashSet<>();
    if (builder.getLocation() == null)
      throw new IllegalArgumentException("Please provide a location for the hologram using HologramBuilder#at(Location)"); 
    this.viewers = builder.getViewers();
    this.location = builder.getLocation();
    for (String line : builder.getLines())
      this.lines.add(new HologramLine(line)); 
  }
  
  protected void show(Player player) {
    if (!player.getLocation().getWorld().equals(this.location.getWorld()))
      return; 
    Location first = this.location.clone().add(0.0D, this.lines.size() * 0.23D, 0.0D);
    for (HologramLine line : this.lines) {
      showLine(player, first.clone(), line);
      first.subtract(0.0D, 0.23D, 0.0D);
    } 
    this.currentWatchers.add(player.getUniqueId());
  }
  
  protected Pair<Integer, Integer> showLine(Player player, Location loc, HologramLine line) {
    HologramPacketProvider packetProvider = getPacketProviderForPlayer(player);
    HologramPacket hologramPacket = packetProvider.getPacketsFor(loc, line);
    if (hologramPacket != null) {
      hologramPacket.sendToPlayer(player);
      return new Pair(hologramPacket.getEntityIds().get(0), hologramPacket.getEntityIds().get(1));
    } 
    return null;
  }
  
  protected void destroy0(Player player) {
    List<Integer> ints = new ArrayList<>();
    for (HologramLine line : this.lines) {
      if (line.getHorseId() == -1337) {
        ints.add(line.getSkullId());
        continue;
      } 
      ints.add(line.getSkullId());
      ints.add(line.getHorseId());
    } 
    PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(convertIntegers(ints));
    (((CraftPlayer)player).getHandle()).playerConnection.sendPacket(packet);
    this.currentWatchers.remove(player.getUniqueId());
  }
  
  protected int[] convertIntegers(List<Integer> integers) {
    int[] ret = new int[integers.size()];
    for (int i = 0; i < ret.length; i++)
      ret[i] = ((Integer)integers.get(i)).intValue(); 
    return ret;
  }
  
  public void update() {
    Collection<UUID> viewers = getViewers();
    if (viewers == null)
      viewers = (Collection<UUID>)ImmutableList.copyOf(Bukkit.getServer().getOnlinePlayers()).stream().map(Entity::getUniqueId).collect(Collectors.toSet());
    for (UUID uuid : viewers) {
      Player player = Bukkit.getPlayer(uuid);
      if (player != null && player.isOnline())
        update(player); 
    } 
    this.lastLines.addAll(this.lines);
  }
  
  public void update(Player player) {
    if (!player.getLocation().getWorld().equals(this.location.getWorld()))
      return; 
    if (this.lastLines.size() != this.lines.size()) {
      destroy0(player);
      show(player);
      return;
    } 
    for (int index = 0; index < rawLines().size(); index++) {
      HologramLine line = rawLines().get(index);
      String text = ChatColor.translateAlternateColorCodes('&', line.getText());
      //boolean is18 = TabUtils.is18(player);
      boolean is18 = true;
      try {
        PacketContainer container = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
        container.getIntegers().write(0, Integer.valueOf(is18 ? line.getSkullId() : line.getHorseId()));
        WrappedDataWatcher wrappedDataWatcher = new WrappedDataWatcher();
        if (is18) {
          wrappedDataWatcher.setObject(2, text);
        } else {
          wrappedDataWatcher.setObject(10, text);
        } 
        List<WrappedWatchableObject> watchableObjects = Arrays.asList(Iterators.toArray(wrappedDataWatcher.iterator(), WrappedWatchableObject.class));
        container.getWatchableCollectionModifier().write(0, watchableObjects);
        try {
          ProtocolLibrary.getProtocolManager().sendServerPacket(player, container);
        } catch (Exception exception) {}
      } catch (IndexOutOfBoundsException e) {
        destroy0(player);
        show(player);
      } 
    } 
  }
  
  private HologramPacketProvider getPacketProviderForPlayer(Player player) {
    //return ((((CraftPlayer)player).getHandle()).playerConnection.networkManager.getVersion() > 5) ? (HologramPacketProvider)new Minecraft18HologramPacketProvider() : (HologramPacketProvider)new Minecraft17HologramPacketProvider();
    return new Minecraft18HologramPacketProvider();
  }
}
