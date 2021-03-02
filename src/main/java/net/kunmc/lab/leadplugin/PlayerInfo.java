package net.kunmc.lab.leadplugin;

import org.bukkit.entity.Entity;

import java.util.UUID;

public class PlayerInfo {
    final private Entity origin;
    private String pairName;
    private boolean isLeashing;
    private boolean isHolder;
    private boolean isCool;
    private boolean isDead;
    private boolean isAddWire;
    private UUID wire;

    public PlayerInfo(Entity origin) {
        this.origin = origin;
        pairName = null;
        isLeashing = false;
        isHolder = false;
        isCool = false;
        isDead = false;
        isAddWire = false;
        wire = null;
    }

    public Entity getOrigin() {
        return origin;
    }

    public String getPairName() {
        return pairName;
    }

    public boolean isLeashing() {
        return isLeashing;
    }

    public boolean isHolder() {
        return isHolder;
    }

    public void setCool(boolean cool) {
        isCool = cool;
    }

    public boolean isCoolTime() {
        return isCool;
    }

    public void release() {
        pairName = null;
        isLeashing = false;
        isHolder = false;
        isCool = false;
        isDead = false;
        isAddWire = false;
    }

    public void leash(boolean isHolder, String pairName) {
        this.pairName = pairName;
        if(isHolder){this.isHolder=true;}
        isLeashing = true;
    }

    public void setDead(boolean dead) {
        isDead = dead;
    }

    public boolean isDead() {
        return isDead;
    }

    public void setAddWire(boolean addWire) {
        isAddWire = addWire;
    }

    public boolean isAddWire() {
        return isAddWire;
    }

    public void setWire(UUID wire) {
        this.wire = wire;
    }

    public UUID getWire() {
        return wire;
    }
}