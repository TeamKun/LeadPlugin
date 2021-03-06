package net.kunmc.lab.leadplugin;

import org.bukkit.entity.LivingEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class PlayerInfo {
    final private LivingEntity origin;
    private int biomes;
    private boolean isCool;
    private boolean isLeading;
    private boolean isLeashing;
    private boolean isMultiple;
    private HashMap<UUID,Boolean> targetMap;
    private HashMap<UUID,Boolean> holderMap;
    private ArrayList<UUID> wires;

    public PlayerInfo(LivingEntity origin) {
        this.origin = origin;
        biomes = checkBiomes();
        isCool = false;
        isLeading = false;
        isLeading = false;
        isMultiple = origin.hasPermission("leadplugin.multiple");
        targetMap = new HashMap<UUID,Boolean>();
        holderMap = new HashMap<UUID,Boolean>();
        wires = new ArrayList<UUID>();
    }

    public LivingEntity getOrigin() {
        return origin;
    }

    public void setCool(boolean cool) {
        isCool = cool;
    }

    public boolean isCool() {
        return isCool;
    }

    public boolean isLeading() {
        return isLeading;
    }

    public void setLeading(boolean leading) {
        isLeading = leading;
    }

    public boolean isLeashing() {
        return isLeashing;
    }

    public void setLeashing(boolean leashing) {
        isLeashing = leashing;
    }

    public boolean isMultiple() {
        return isMultiple;
    }

    public void setMultiple(boolean multiple) {
        isMultiple = multiple;
    }

    public HashMap<UUID, Boolean> getTargetMap() {
        return targetMap;
    }

    public HashMap<UUID, Boolean> getHolderMap() {
        return holderMap;
    }

    public ArrayList<UUID> getWires() {
        return wires;
    }

    public void setWires(ArrayList<UUID> wires) {
        this.wires = wires;
    }

    private int checkBiomes() {
        switch (origin.getLocation().getBlock().getBiome()) {
            case NETHER:
                return 1;
            case THE_END:
                return 2;
            default:
                return 0;
        }
    }

    public void init() {
        biomes = checkBiomes();
        isCool = false;
        isLeading = false;
        isLeashing = false;
    }

    public boolean shouldRelease() {
        return !origin.isValid() || origin.isDead() || biomes != checkBiomes();
    }

    public boolean isTarget(UUID holderId) {
        if(holderMap.containsKey(holderId)) {
            return holderMap.get(holderId);
        }
        return false;
    }

    public boolean isHolder(UUID targetId) {
        if(targetMap.containsKey(targetId)) {
            return targetMap.get(targetId);
        }
        return false;
    }
}