package net.kunmc.lab.leadplugin;

import org.bukkit.plugin.java.JavaPlugin;

public final class LeadPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("実行しました");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("終了しました");
    }
}
