package net.kunmc.lab.leadplugin;

import org.bukkit.entity.LivingEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class PlayerInfo {
    final private LivingEntity origin;
    private String pairName;
    private boolean isLeashing;
    private boolean isHolder;
    private boolean isCool;
    private boolean isDead;
    private boolean isAddWire;
    private UUID wire;

    private int world;

    private boolean isMultiple;
    private ArrayList<String> pairNames;
    private HashMap<String, Boolean> pairAddWires;
    private HashMap<String, UUID> pairWires;

    public PlayerInfo(LivingEntity origin) {
        this.origin = origin;
        pairName = null;
        isLeashing = false;
        isHolder = false;
        isCool = false;
        isDead = false;
        isAddWire = false;
        wire = null;
        world = checkBiome();
        isMultiple = origin.hasPermission("leadplugin.multiple");
        pairNames = new ArrayList<String>();
        pairAddWires = new HashMap<String, Boolean>();
        pairWires = new HashMap<String, UUID>();
    }

    public LivingEntity getOrigin() {
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
        pairNames = new ArrayList<String>();
        pairAddWires = new HashMap<String, Boolean>();
        pairWires = new HashMap<String, UUID>();
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

    // public void setMultiple(boolean multiple) { isMultiple = multiple; }

    public boolean isMultiple() {
        return isMultiple;
    }

    public void setPairNames(ArrayList<String> pairNames) {
        this.pairNames = pairNames;
    }

    public ArrayList<String> getPairNames() {
        return pairNames;
    }

    public HashMap<String, Boolean> getPairAddWires() {
        return pairAddWires;
    }

    public HashMap<String, UUID> getPairWires() {
        return pairWires;
    }

    public boolean holderCheck(HashMap<String, PlayerInfo> infoMap) {
        HashMap<String, PlayerInfo> im = new HashMap<String, PlayerInfo>(infoMap);
        ArrayList<String> pns = new ArrayList<String>(pairNames);
        boolean canRelease = true;
        for(String pn: pns) {
            PlayerInfo tInfo = im.get(pn);
            if(tInfo.isLeashing && tInfo.getPairName() != null && tInfo.getPairName().equals(origin.getName())) {
                canRelease = false;
                break;
            }
        }
        return canRelease;
    }

    public boolean multipleCheck() {
        if(isMultiple) {
            if(!origin.hasPermission("leadplugin.multiple")) {
                isMultiple = false;
                return true;
            }
        } else {
            if(origin.hasPermission("leadplugin.multiple")) {
                isMultiple = true;
                return true;
            }
        }
        return false;
    }

    public int getWorld() {
        return world;
    }

    public void setWorld(int world) {
        this.world = world;
    }

    public int checkBiome() {
        switch (origin.getLocation().getBlock().getBiome()) {
            case NETHER:
                return 1;
            case THE_END:
                return 2;
            default:
                return 0;
        }
    }
}