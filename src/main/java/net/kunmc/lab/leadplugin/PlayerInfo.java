package net.kunmc.lab.leadplugin;

import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.HashMap;
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

    private boolean isMultiple;
    private ArrayList<String> pairNames;
    private HashMap<String, Boolean> pairAddWires;
    private HashMap<String, UUID> pairWires;

    public PlayerInfo(Entity origin) {
        this.origin = origin;
        pairName = null;
        isLeashing = false;
        isHolder = false;
        isCool = false;
        isDead = false;
        isAddWire = false;
        wire = null;
        isMultiple = false;
        pairNames = new ArrayList<String>();
        pairAddWires = new HashMap<String, Boolean>();
        pairWires = new HashMap<String, UUID>();
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

    public void setMultiple(boolean multiple) {
        isMultiple = multiple;
    }

    public boolean isMultiple() {
        return isMultiple;
    }

    public void setPairNames(ArrayList<String> pairNames) {
        this.pairNames = pairNames;
    }

    public ArrayList<String> getPairNames() {
        return pairNames;
    }

    public void setPairAddWires(HashMap<String, Boolean> pairAddWires) {
        this.pairAddWires = pairAddWires;
    }

    public HashMap<String, Boolean> getPairAddWires() {
        return pairAddWires;
    }

    public void setPairWires(HashMap<String, UUID> pairWires) {
        this.pairWires = pairWires;
    }

    public HashMap<String, UUID> getPairWires() {
        return pairWires;
    }

    public boolean check(HashMap<String, PlayerInfo> infoMap) {
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
}