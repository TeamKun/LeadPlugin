package net.kunmc.lab.leadplugin;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.player.*;

public class EventListener implements Listener {
    private final LeadPlugin plugin;
    private final ConfigManager config;

    public EventListener() {
        plugin = LeadPlugin.getInstance();
        config = ConfigManager.getInstance();
    }

    @EventHandler
    public void onPlayerInteractLivingEntityWithLead(PlayerInteractEntityEvent e) {
        if(!(e.getRightClicked() instanceof LivingEntity) || (e.getRightClicked().getType() == EntityType.SILVERFISH)) {return;}
        if(e.getPlayer().getInventory().getItemInMainHand().getType() != Material.LEAD &&
                e.getPlayer().getInventory().getItemInOffHand().getType() != Material.LEAD) {
            return;
        }
        Player p = e.getPlayer();
        LivingEntity t = (LivingEntity) e.getRightClicked();
        plugin.clickWithLeadEvent(p, t);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        plugin.quitEvent(e.getEntity());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        plugin.quitEvent(e.getPlayer());
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        plugin.joinEvent(e.getPlayer());
    }

    @EventHandler
    public void onPLayerJoin(PlayerJoinEvent e) {
        plugin.joinEvent(e.getPlayer());
    }

    @EventHandler
    public void onPlayerLeash(PlayerLeashEntityEvent e) {
        if(!config.lead_only_player) { e.setCancelled(true);}
    }

    @EventHandler
    public void onPlayerUnLeash(PlayerUnleashEntityEvent e) {
        if(!config.lead_only_player) { e.setCancelled(true);}
    }
}
