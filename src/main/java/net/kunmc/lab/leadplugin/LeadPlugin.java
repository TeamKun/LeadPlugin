package net.kunmc.lab.leadplugin;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.HashMap;

public final class LeadPlugin extends JavaPlugin implements Listener {
    private HashMap<String, PlayerInfo> infoMap;
    double holder_power = 0.8;
    double target_power = 0.2;
    double max_distance = 10;
    boolean lead_after_death = false;
    boolean lead_only_player = false;

    @Override
    public void onEnable() {
        // Plugin startup logic
        infoMap = new HashMap<String, PlayerInfo>();
        config();
        new CommandListener(this);
        getServer().getPluginManager().registerEvents(this, this);
        task();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void config() {
        FileConfiguration config = getConfig();
        try {
            holder_power = config.getDouble("holder_power");
            target_power = config.getDouble("target_power");
            max_distance = config.getDouble("max_distance");
            lead_after_death = config.getBoolean("lead_after_death");
            lead_only_player = config.getBoolean("lead_only_player");
        } catch (Exception ignored) {
        }
    }

    private void task() {
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                getServer().getOnlinePlayers().forEach(p -> {
                    if(!infoMap.containsKey(p.getName())) {
                        infoMap.put(p.getName(), new PlayerInfo(p));
                        return;
                    }
                    PlayerInfo pInfo = infoMap.get(p.getName());
                    if(pInfo.isLeashing()) {
                        PlayerInfo tInfo = infoMap.get(pInfo.getPairName());
                        if(lead_after_death) {
                            if(pInfo.isDead() || tInfo.isDead()) { return; }
                        }
                        if(pInfo.isHolder()) {
                            setParticle(p.getLocation(), tInfo.getOrigin().getLocation(), pInfo.getPairName());
                        }
                        double distance = p.getLocation().distance(tInfo.getOrigin().getLocation());
                        if(distance > max_distance * 2) {
                            p.teleport(tInfo.getOrigin().getLocation());
                            return;
                        }
                        if(distance > max_distance) {
                            pull(tInfo.getOrigin(), p,  1);
                        }
                    }
                });
            }
        }, 0L, 2L);
    }

    private void setParticle(Location pl, Location tl, String tName) {
        Vector dv = tl.toVector().subtract(pl.toVector()).normalize();
        outerLoop: for(double t = 0; t < max_distance; t++) {
            pl.add(dv);
            pl.add(0, 1, 0);
            pl.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, pl, 1, 0,0, 0, 0);
            for(Entity e : pl.getChunk().getEntities()) {
                if (e.getLocation().distance(pl) < 2 && (e instanceof LivingEntity)) {
                    boolean isTarget = false;
                    if(e instanceof Player) {
                        if(e.getName().equals(tName)) {isTarget = true; }
                    } else {
                        if(lead_only_player) { continue; }
                        if(("No" + e.getUniqueId()).equals(tName)) {isTarget = true; }
                    }
                    if(isTarget) {
                        break outerLoop;
                    }
                }
            }
            pl.subtract(0, 1, 0);
        }
    }

    private void clickWithLead(Player p, Entity t) {
        if(!(t instanceof LivingEntity)) { return; };
        String pName = p.getName();
        String tName;
        PlayerInfo pInfo = infoMap.get(pName);
        PlayerInfo tInfo;
        if(t instanceof Player) {
            tName = t.getName();
        } else {
            if(lead_only_player) { return; }
            tName = setEntityName(t);
        }
        tInfo = infoMap.get(tName);
        if(pInfo.isCoolTime()){return;}
        if(pInfo.isLeashing() && !pInfo.isHolder()) {return;}
        if(pInfo.isLeashing() && pInfo.isHolder()) {
            if(tInfo.isLeashing() && tInfo.getPairName() != null && tInfo.getPairName().equals(p.getName())) {
                release(pInfo, tInfo);
                setCoolTime(pInfo);
            }
            return;
        }
        if(tInfo.isLeashing()) {return;}
        leash(pInfo, tInfo, pName, tName);
        setCoolTime(pInfo);
    }

    private void clickWithHand(Player p) {
        PlayerInfo pInfo = infoMap.get(p.getName());
        if(pInfo.isLeashing()) {
            PlayerInfo tInfo = infoMap.get(pInfo.getPairName());
            double power = pInfo.isHolder() ? holder_power : target_power;
            pull(p, tInfo.getOrigin(), power);
            setCoolTime(pInfo);
        }
    }

    private void release(PlayerInfo pInfo, PlayerInfo tInfo) {
        pInfo.release();
        tInfo.release();
    }

    private void leash(PlayerInfo pInfo, PlayerInfo tInfo, String pName, String tName) {
        pInfo.leash(true, tName);
        tInfo.leash(false, pName);
    }

    private void setCoolTime(PlayerInfo pInfo) {
        pInfo.setCool(true);
        getServer().getScheduler().runTaskLater(this, new Runnable() {
            @Override
            public void run() {
                pInfo.setCool(false);
            }
        }, 2L);
    }

    private void pull(Entity p, Entity t, double power) {
        Vector pv = p.getLocation().toVector();
        Vector tv = t.getLocation().toVector();
        Vector velocity = pv.subtract(tv);
        t.setVelocity(velocity.normalize().multiply(power));
    }

    private void quit(PlayerInfo pInfo) {
        if(pInfo.isLeashing()) {
            PlayerInfo tInfo = infoMap.get(pInfo.getPairName());
            release(pInfo, tInfo);
        }
    }

    private void death(PlayerInfo pInfo) {
        if(pInfo.isLeashing()) {
            PlayerInfo tInfo = infoMap.get(pInfo.getPairName());
            if(lead_after_death && pInfo.getOrigin() instanceof Player) {
                pInfo.setDead(true);
                return;
            }
            release(pInfo, tInfo);
        }
    }

    private void respawn(PlayerInfo pInfo) {
        pInfo.setDead(false);
    }

    private String setEntityName(Entity e) {
        if(!infoMap.containsKey("No" + e.getUniqueId())) {
            infoMap.put("No" + e.getUniqueId(), new PlayerInfo(e));
        }
        return "No" + e.getUniqueId();
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
        Player p = e.getPlayer();
        Entity t = e.getRightClicked();
        if(p.getInventory().getItemInMainHand().getType() == Material.LEAD ||
        p.getInventory().getItemInOffHand().getType() == Material.LEAD) {
            clickWithLead(p, t);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if(e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
            if(p.getInventory().getItemInMainHand().getType() == Material.AIR) {
                clickWithHand(p);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        quit(infoMap.get(p.getName()));
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        Entity entity = e.getEntity();
        PlayerInfo pInfo;
        if(entity instanceof Player) {
            pInfo = infoMap.get(entity.getName());
        } else {
            if(lead_only_player){ return;}
            pInfo = infoMap.get(setEntityName(entity));
        }
        death(pInfo);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        Player p = e.getPlayer();
        if(lead_after_death) {
            respawn(infoMap.get(p.getName()));
        }
    }

    @EventHandler
    public void onPlayerLeash(PlayerLeashEntityEvent e) {
        if(!lead_only_player) { e.setCancelled(true);}
    }

    @EventHandler
    public void onPlayerUnLeash(PlayerUnleashEntityEvent e) {
        if(!lead_only_player) { e.setCancelled(true);}
    }

}