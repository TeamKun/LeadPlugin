package net.kunmc.lab.leadplugin;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public class PlayerInfo {
    private final Entity myOriginInfo;
    private String holder;
    private String target;
    private boolean isHolder;
    private boolean isTarget;
    private double power;
    private boolean isCoolTime;
    private Location loc;
    private LivingEntity dummy;

    public PlayerInfo(Entity myOriginInfo, Location loc) {
        this.myOriginInfo = myOriginInfo;
        holder = null;
        target = null;
        isHolder = false;
        isTarget = false;
        power = 0.8;
        isCoolTime = false;
        this.loc = loc;
        dummy = null;
    }

    public Entity getMyOriginInfo() {
        return myOriginInfo;
    }

    public void setHolderName(String holder) {
        this.holder = holder;
    }

    public String getHolderName() {
        return holder;
    }

    public void setTargetName(String target) {
        this.target = target;
    }

    public String getTargetName() {
        return target;
    }

    public void setIsHolder(boolean isHolder) {
        this.isHolder = isHolder;
    }

    public boolean isHolder() {
        return isHolder;
    }

    public void setIsTarget(boolean isTarget) {
        this.isTarget = isTarget;
    }

    public boolean isTarget() {
        return isTarget;
    }

    public void setPower(double power) {
        this.power = power;
    }

    public double getPower() {
        return power;
    }

    public void setIsCoolTime(boolean isCoolTime) {
        this.isCoolTime = isCoolTime;
    }

    public boolean isCoolTime() {
        return isCoolTime;
    }

    public void setLoc(Location loc) {
        this.loc = loc;
    }

    public Location getLoc() {
        return loc;
    }

    public void setDummy(LivingEntity dummy) {
        this.dummy = dummy;
    }

    public LivingEntity getDummy() {
        return dummy;
    }
}