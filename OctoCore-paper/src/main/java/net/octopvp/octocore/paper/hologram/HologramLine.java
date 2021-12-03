package net.octopvp.octocore.paper.hologram;

import net.octopvp.octocore.paper.utils.EntityUtils;

public class HologramLine {
  public void setText(String text) {
    this.text = text;
  }
  
  private final int skullId = EntityUtils.getFakeEntityId();
  
  public int getSkullId() {
    return this.skullId;
  }
  
  private final int horseId = EntityUtils.getFakeEntityId();
  
  private String text;
  
  public int getHorseId() {
    return this.horseId;
  }
  
  public String getText() {
    return this.text;
  }
  
  public HologramLine(String text) {
    this.text = text;
  }
}
