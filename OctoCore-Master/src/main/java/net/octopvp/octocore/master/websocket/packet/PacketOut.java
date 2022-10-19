package net.octopvp.aetheriacoremaster.websocket.packet;

import com.google.gson.JsonElement;

public abstract class PacketOut extends Packet {
    @Override
    public PacketDirection getDirection() {
        return PacketDirection.OUT;
    }

}
