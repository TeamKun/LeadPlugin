package net.kunmc.lab.leadplugin;

import org.bukkit.Particle;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {
    private static ConfigManager instance;
    private final LeadPlugin plugin;
    double holder_power = 0.8;
    double target_power = 0.2;
    double max_distance = 10;
    double force_teleport_distance = 200;
    boolean lead_only_player = false;
    boolean possessive_mode = false;
    boolean particle_mode = false;
    Particle particle = Particle.CRIT;

    public static ConfigManager getInstance() {
        return instance;
    }

    public ConfigManager() {
        instance = this;
        plugin = LeadPlugin.getInstance();
        config();
    }

    private void config() {
        FileConfiguration config = plugin.getConfig();
        try {
            holder_power = config.getDouble("holder_power");
            target_power = config.getDouble("target_power");
            max_distance = config.getDouble("max_distance");
            force_teleport_distance = config.getDouble("force_teleport_distance");
            lead_only_player = config.getBoolean("lead_only_player");
            possessive_mode = config.getBoolean("possessive_mode");
            particle_mode = config.getBoolean("particle_mode");
            setParticleType(config.getString("particle_type"));
        } catch (Exception ignored) {
        }
    }

    public boolean setParticleType(String particle_type) {
        try {
            particle = Particle.valueOf(particle_type);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
