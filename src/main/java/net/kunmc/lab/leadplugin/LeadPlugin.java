package net.kunmc.lab.leadplugin;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerUnleashEntityEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.HashMap;

public final class LeadPlugin extends JavaPlugin implements Listener {
    private HashMap<String, PlayerInfo> players;
    private double power_holder = 0.8;
    private double power_target = 0.2;

    @Override
    public void onEnable() {
        // Plugin startup logic
        FileConfiguration config = getConfig();
        try {
            power_holder = config.getDouble("holderPower");
            power_target = config.getDouble("targetPower");
        } catch (Exception e) {
            getLogger().info("configが正しく読まれませんでした");
        }

        players = new HashMap<String, PlayerInfo>();
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                getServer().getOnlinePlayers().forEach(player -> {

                    if(!players.containsKey(player.getName())) {
                        players.put(player.getName(), new PlayerInfo(player, player.getLocation()));
                    }
                    PlayerInfo playerInfo = players.get(player.getName());

                    if(playerInfo.isHolder()) {
                        playerInfo.setIsHolder(false);
                        players.forEach((targetName, targetInfo) -> {
                            if(player.getName().equals(targetName)) {
                                return;
                            }
                            if(targetInfo.getMyOriginInfo().getType() == EntityType.RABBIT) {
                                return;
                            }
                            if(!targetInfo.isTarget()) {
                                release(targetInfo);
                                return;
                            }
                            if(targetInfo.getHolderName() != null && targetInfo.getHolderName().equals(player.getName())) {
                                playerInfo.setIsHolder(true);
                                adjustLoc(player, playerInfo, targetName);
                            }
                        });
                        return;
                    }

                    if(playerInfo.isTarget()) {
                        if(playerInfo.getHolderName() != null && !players.containsKey(playerInfo.getHolderName())) {
                            release(playerInfo);
                            return;
                        }
                        if(playerInfo.getMyOriginInfo().getType() == EntityType.RABBIT) {
                            return;
                        }
                        if(playerInfo.getHolderName() != null) {
                            PlayerInfo holderInfo = players.get(playerInfo.getHolderName());
                            adjustLoc(holderInfo.getMyOriginInfo(), holderInfo, player.getName());
                            return;
                        }
                        release(playerInfo);
                    }

                });
            }
        }, 0L, 2L);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity target = event.getRightClicked();

        if(target.getType() == EntityType.RABBIT) {
            return;
        }

        String targetName;

        if(event.getRightClicked() instanceof Player) {
            targetName = target.getName();
        } else {
            if(!players.containsKey(target.getCustomName())) {
                int r = (int) (Math.random() * 1000 + 1);
                target.setCustomNameVisible(false);
                target.setCustomName(player.getName() + "_" + ((target.getEntityId() + 1) * r + "号"));
                targetName = target.getCustomName();
                players.put(target.getCustomName(), new PlayerInfo(target, target.getLocation()));
            } else {
                targetName = target.getCustomName();
            }
        }

        if(!players.containsKey(player.getName())) {
            players.put(player.getName(), new PlayerInfo(player, player.getLocation()));
        }

        if(!players.containsKey(targetName)) {
            players.put(targetName, new PlayerInfo(target, target.getLocation()));
        }

        PlayerInfo playerInfo = players.get(player.getName());
        if (playerInfo.isCoolTime()) {
            return;
        }
        PlayerInfo targetInfo = players.get(targetName);

        if(canLeashOrRelease(player, playerInfo, targetInfo, false)) {
            playerInfo.setIsHolder(true);
            targetInfo.setIsHolder(false);

            playerInfo.setIsTarget(false);
            targetInfo.setIsTarget(true);

            playerInfo.setHolderName(null);
            targetInfo.setHolderName(player.getName());

            playerInfo.setLoc(player.getLocation());
            targetInfo.setLoc(target.getLocation());

            playerInfo.setPower(power_holder);
            targetInfo.setPower(power_target);

            LivingEntity dummy = (LivingEntity) player.getWorld().spawnEntity(target.getLocation(), EntityType.RABBIT);
            dummy.setLeashHolder(player);
            addPotionEffect(dummy);
            targetInfo.setDummy(dummy);

            setCoolTime(playerInfo);
            return;
        }

        if(canLeashOrRelease(player, playerInfo, targetInfo, true)) {
            release(targetInfo);
            setCoolTime(playerInfo);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if(!players.containsKey(player.getName())) {
            players.put(player.getName(), new PlayerInfo(player, player.getLocation()));
        }
        if(event.getAction() != Action.LEFT_CLICK_AIR && event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }

        PlayerInventory pi = player.getInventory();

        if(pi.getItemInMainHand().getType() != Material.AIR) {
            return;
        }

        PlayerInfo playerInfo = players.get(player.getName());
        if(playerInfo.isCoolTime()) {
            return;
        }

        if(playerInfo.isHolder()) {
            players.forEach((targetName, targetInfo) -> {
                if(!targetInfo.isTarget()) {
                    return;
                }
                if(targetInfo.getHolderName() != null && targetInfo.getHolderName().equals(player.getName())) {
                    pull(player, targetInfo.getMyOriginInfo(), playerInfo.getPower());
                }
            });
            setCoolTime(playerInfo);
            return;
        }
        if(playerInfo.isTarget()) {
            if(playerInfo.getHolderName() != null && !players.containsKey(playerInfo.getHolderName())) {
                release(playerInfo);
                return;
            }
            if(playerInfo.getHolderName() != null) {
                PlayerInfo holderInfo = players.get(playerInfo.getHolderName());
                pull(player, holderInfo.getMyOriginInfo(), playerInfo.getPower());
                setCoolTime(playerInfo);
            }
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if(event.getEntity().getType() == EntityType.RABBIT) {
            event.setShouldPlayDeathSound(false);
            return;
        }
        String playerName;
        if(event.getEntity() instanceof Player) {
            playerName = event.getEntity().getName();
        } else {
            playerName = event.getEntity().getCustomName();
        }
        playerDeathOrQuit(playerName);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        playerDeathOrQuit(event.getPlayer().getName());
    }

    @EventHandler
    public void onPlayerLeash(PlayerLeashEntityEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerUnLeash(PlayerUnleashEntityEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onItemSpawn(ItemSpawnEvent event) {
        Material material = event.getEntity().getItemStack().getType();
        if(material == Material.RABBIT || material == Material.RABBIT_FOOT || material == Material.RABBIT_HIDE) {
            event.setCancelled(true);
        }
    }


    private boolean canLeashOrRelease(Player player, PlayerInfo playerInfo, PlayerInfo targetInfo, boolean isRelease) {
        if(isRelease) {
            if (playerInfo.isTarget() || !targetInfo.isTarget()) {
                return false;
            }
            return playerInfo.isHolder();
        } else {
            if (playerInfo.isTarget() || targetInfo.isTarget()) {
                return false;
            }
            if (targetInfo.isHolder()) {
                return false;
            }
        }
        PlayerInventory pi = player.getInventory();
        return pi.getItemInMainHand().getType() == Material.LEAD || pi.getItemInOffHand().getType() == Material.LEAD;
    }

    private void setCoolTime(PlayerInfo playerInfo) {
        playerInfo.setIsCoolTime(true);
        getServer().getScheduler().runTaskLater(this, new Runnable() {
            @Override
            public void run() {
                playerInfo.setIsCoolTime(false);
            }
        }, 2L);
    }

    private void adjustLoc(Entity player, PlayerInfo playerInfo, String targetName) {
        PlayerInfo targetInfo = players.get(targetName);
        Entity target = targetInfo.getMyOriginInfo();

        if(player.getLocation().distance(target.getLocation()) >= 15) {
            if(targetInfo.isTarget()) {
                release(targetInfo);
                return;
            }
            return;
        }

        if(player.getLocation().distance(target.getLocation()) >= 9.9) {
            player.teleport(playerInfo.getLoc());
            target.teleport(targetInfo.getLoc());
            targetInfo.getDummy().teleport(target.getLocation());
            addPotionEffect(targetInfo.getDummy());
            return;
        }

        if(player.getLocation().distance(target.getLocation()) < 9.9) {
            playerInfo.setLoc(player.getLocation());
            targetInfo.setLoc(target.getLocation());
        }

        targetInfo.getDummy().teleport(target.getLocation());
        addPotionEffect(targetInfo.getDummy());
    }

    private void release(PlayerInfo targetInfo) {
        targetInfo.setIsHolder(false);
        targetInfo.setHolderName(null);
        targetInfo.setIsTarget(false);

        LivingEntity dummy = targetInfo.getDummy();
        if(dummy != null) {
            targetInfo.setDummy(null);
            dummy.setLeashHolder(null);
            dummy.setHealth(0);
        }
    }

    private void playerDeathOrQuit(String playerName) {
        if(!players.containsKey(playerName)) {
            return;
        }
        PlayerInfo playerInfo = players.get(playerName);
        if(playerInfo.isHolder()) {
            players.forEach((targetName, targetInfo) -> {
                if(!targetInfo.isTarget()) {
                    return;
                }
                if(targetInfo.getHolderName() != null && targetInfo.getHolderName().equals(playerName)) {
                    release(targetInfo);
                }
            });
            return;
        }
        if(playerInfo.isTarget()) {
            release(playerInfo);
        }
    }

    private void addPotionEffect(LivingEntity dummy) {
        dummy.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 1200, 6, false));
        dummy.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 1200, 6, false));
        dummy.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1200, 6, false));
    }

    private void pull(Entity puller, Entity target, double power) {
        Vector pv = puller.getLocation().toVector();
        Vector tv = target.getLocation().toVector();
        Vector velocity = pv.subtract(tv);
        target.setVelocity(velocity.normalize().multiply(power));
    }
}
