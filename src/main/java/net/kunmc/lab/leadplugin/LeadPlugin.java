package net.kunmc.lab.leadplugin;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.UUID;

public final class LeadPlugin extends JavaPlugin {
    private static LeadPlugin instance;
    private HashMap<UUID,PlayerInfo> infoMap;
    private ConfigManager config;
    private WireAPI wireAPI;

    public static LeadPlugin getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        task();
        getServer().getPluginManager().registerEvents(new EventListener(),this);
    }

    private void task() {
        infoMap = new HashMap<UUID,PlayerInfo>();
        config = new ConfigManager();
        wireAPI = new WireAPI();
        new CommandListener();
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                getServer().getOnlinePlayers().forEach(h -> {
                    if(shouldPutPlayerInfoMap(h)) { return; }
                    PlayerInfo hInfo = infoMap.get(h.getUniqueId());
                    hInfo.setMultiple(h.hasPermission("leadplugin.multiple"));
                    wireAPI.remove(hInfo);
                    if(hInfo.shouldRelease()) {
                        quitEvent(h);
                        joinEvent(h);
                        return;
                    }
                    HashMap<UUID,Boolean> tMap = new HashMap<UUID,Boolean>(hInfo.getTargetMap());
                    tMap.keySet().forEach(tId -> {
                        PlayerInfo tInfo = infoMap.get(tId);
                        if(tInfo.shouldRelease() && !(tInfo.getOrigin() instanceof Player)) {
                            quitEvent(tInfo.getOrigin());
                            return;
                        }
                        if(!tInfo.isTarget(h.getUniqueId())) {
                            return;
                        }
                        adjustPosition(hInfo,tInfo);
                        if(config.particle_mode) {
                            setParticle(hInfo, tInfo);
                            return;
                        }
                        wireAPI.set(hInfo, tInfo);
                    });
                });
            }
        }, 0L, 2L);
    }

    private boolean shouldPutPlayerInfoMap(Player p) {
        if(!infoMap.containsKey(p.getUniqueId())) {
            p.setCollidable(false);
            infoMap.put(p.getUniqueId(), new PlayerInfo(p));
            return true;
        }
        return false;
    }

    private void adjustPosition(PlayerInfo hInfo, PlayerInfo tInfo) {
        LivingEntity h = hInfo.getOrigin();
        LivingEntity t = tInfo.getOrigin();
        double distance = h.getLocation().distance(t.getLocation());
        if(distance > config.force_teleport_distance) {
            h.teleport(t.getLocation());
            return;
        }
        if(distance > config.max_distance) {
            double diff = distance - config.max_distance;
            pull(h, t, calcPower(diff, true));
            pull(t, h, calcPower(diff, false));
        }
    }

    private double calcPower(double diff, boolean isHolder) {
        double power = isHolder ? config.holder_power : config.target_power;
        return (power * diff) > 10 ? 10 : (power * diff);
    }

    private void pull(LivingEntity h, LivingEntity t, double power) {
        Vector hv = h.getLocation().toVector();
        Vector tv = t.getLocation().toVector();
        Vector velocity = hv.subtract(tv).normalize().multiply(power);
        t.setVelocity(t.getVelocity().add(velocity));
    }

    private void setParticle(PlayerInfo hInfo, PlayerInfo tInfo) {
        LivingEntity h = hInfo.getOrigin();
        LivingEntity t = tInfo.getOrigin();
        Location hl = h.getLocation();
        Location tl = t.getLocation();
        Vector dv = tl.toVector().subtract(hl.toVector()).normalize().multiply(0.5);
        outerLoop: for(double i = 0; i < config.max_distance; i += 0.5) {
            hl.add(dv);
            hl.add(0,1,0);
            h.getWorld().spawnParticle(config.particle,hl,1,0,0,0,0);
            for(Entity e : hl.getChunk().getEntities()) {
                if(e.getLocation().distance(hl) < 2 && (e instanceof LivingEntity)) {
                    if(t.getUniqueId().equals(e.getUniqueId())) {
                        break outerLoop;
                    }
                }
            }
            hl.subtract(0,1,0);
        }
    }

    public void clickWithLeadEvent(Player h, LivingEntity t) {
        PlayerInfo hInfo = infoMap.get(h.getUniqueId());
        if(hInfo.isCool()){return;}
        if(!infoMap.containsKey(t.getUniqueId())) {
            infoMap.put(t.getUniqueId(),new PlayerInfo(t));
        }
        PlayerInfo tInfo = infoMap.get(t.getUniqueId());
        if(tInfo.isTarget(h.getUniqueId())) {
            release(hInfo, tInfo);
            setCoolTimes(hInfo);
            return;
        }
        if(hInfo.isLeading() && !hInfo.isMultiple()) {
            return;
        }
        if(tInfo.isHolder(h.getUniqueId())) {
            return;
        }
        if(config.lead_only_player && !(t instanceof Player)) {
            return;
        }
        if(config.possessive_mode && hInfo.isLeashing()) {
            return;
        }
        leash(hInfo,tInfo);
        h.getWorld().playSound(t.getLocation(), Sound.ENTITY_LEASH_KNOT_PLACE, 1, 1);
        setCoolTimes(hInfo);
    }

    private void release(PlayerInfo hInfo, PlayerInfo tInfo) {
        hInfo.setLeading(false);
        tInfo.setLeashing(false);
        hInfo.getTargetMap().put(tInfo.getOrigin().getUniqueId(), false);
        tInfo.getHolderMap().put(hInfo.getOrigin().getUniqueId(), false);
    }

    private void leash(PlayerInfo hInfo, PlayerInfo tInfo) {
        hInfo.setLeading(true);
        tInfo.setLeashing(true);
        hInfo.getTargetMap().put(tInfo.getOrigin().getUniqueId(), true);
        tInfo.getHolderMap().put(hInfo.getOrigin().getUniqueId(), true);
    }

    private void setCoolTimes(PlayerInfo pInfo) {
        pInfo.setCool(true);
        getServer().getScheduler().runTaskLater(this, new Runnable() {
            @Override
            public void run() {
                pInfo.setCool(false);
            }
        }, 2L);
    }

    public void quitEvent(LivingEntity e) {
        if(infoMap.containsKey(e.getUniqueId())) {
            PlayerInfo pInfo = infoMap.get(e.getUniqueId());
            pInfo.init();
            wireAPI.remove(pInfo);
            resetMap(pInfo);
        }
    }

    public void joinEvent(Player p) {
        p.setCollidable(false);
        infoMap.put(p.getUniqueId(), new PlayerInfo(p));
    }

    public void resetMap(PlayerInfo pInfo) {
        HashMap<UUID,PlayerInfo> infoMap = new HashMap<UUID,PlayerInfo>(instance.infoMap);
        UUID myId = pInfo.getOrigin().getUniqueId();
        infoMap.keySet().forEach(id -> {
            if(id.equals(myId)) {return;}
            PlayerInfo tInfo = infoMap.get(id);
            pInfo.getHolderMap().put(id,false);
            tInfo.getHolderMap().put(myId,false);
            pInfo.getTargetMap().put(id,false);
            tInfo.getTargetMap().put(myId, false);
            }
        );
    }

}
