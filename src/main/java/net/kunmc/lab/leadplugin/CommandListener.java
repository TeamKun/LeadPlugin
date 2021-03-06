package net.kunmc.lab.leadplugin;

import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CommandListener implements TabExecutor {
    private ConfigManager config;

    final private String holder_power = "holder_power";
    final private String target_power = "target_power";
    final private String max_distance = "max_distance";
    final private String force_teleport_distance = "force_teleport_distance";
    final private String lead_only_player = "lead_only_player";
    final private String possessive_mode = "possessive_mode";
    final private String particle_mode = "particle_mode";
    final private String particle_type = "particle_type";

    public CommandListener() {
        config = ConfigManager.getInstance();
        Bukkit.getPluginCommand("lead").setExecutor(this);
        Bukkit.getPluginCommand("lead").setTabCompleter(this);
    }

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        if(c.getName().equals("lead")) {
            try {
                if (a[0].equals("show")) {
                    if (a[1].equals("config")) {
                        s.sendMessage("§a" + holder_power + " : " + config.holder_power);
                        s.sendMessage("§a" + target_power + " : " + config.target_power);
                        s.sendMessage("§a" + max_distance + " : " + config.max_distance);
                        s.sendMessage("§a" + force_teleport_distance + " : " + config.force_teleport_distance);
                        s.sendMessage("§a" + lead_only_player + " : " + config.lead_only_player);
                        s.sendMessage("§a" + possessive_mode + " : " + config.possessive_mode);
                        s.sendMessage("§a" + particle_mode + " : " + config.particle_mode);
                        s.sendMessage("§a" + particle_type + " : " + config.particle);
                        return true;
                    }
                    s.sendMessage("/lead show config");
                    return true;
                }
                if (a[0].equals("set")) {
                    if (a[1].equals("config")) {
                        if (a[2].equals("main")) {
                            if (a[3].equals(holder_power)) {
                                try {
                                    config.holder_power = Double.parseDouble(a[4]);
                                    s.sendMessage("§a" + holder_power + "を" + a[4] + "にしました");
                                    return true;
                                } catch (Exception e) {
                                    s.sendMessage("/lead set config main " + holder_power + " 数値");
                                }
                                return true;
                            }
                            if (a[3].equals(target_power)) {
                                try {
                                    config.target_power = Double.parseDouble(a[4]);
                                    s.sendMessage("§a" + target_power + "を" + a[4] + "にしました");
                                    return true;
                                } catch (Exception e) {
                                    s.sendMessage("/lead set config main " + target_power + " 数値");
                                }
                                return true;
                            }
                            if (a[3].equals(max_distance)) {
                                try {
                                    config.max_distance = Double.parseDouble(a[4]);
                                    s.sendMessage("§a" + max_distance + "を" + a[4] + "にしました");
                                    return true;
                                } catch (Exception e) {
                                    s.sendMessage("/lead set config main " + max_distance + " 数値");
                                }
                                return true;
                            }
                            if (a[3].equals(force_teleport_distance)) {
                                try {
                                    config.force_teleport_distance = Double.parseDouble(a[4]);
                                    s.sendMessage("§a" + force_teleport_distance + "を" + a[4] + "にしました");
                                    return true;
                                } catch (Exception e) {
                                    s.sendMessage("/lead set config main " + force_teleport_distance + " 数値");
                                }
                                return true;
                            }
                        }
                        if (a[2].equals("extra")) {
                            if (a[3].equals(lead_only_player)) {
                                try {
                                    config.lead_only_player = Boolean.parseBoolean(a[4]);
                                    s.sendMessage("§a" + lead_only_player + "を" + a[4] + "にしました");
                                    return true;
                                } catch (Exception e) {
                                    s.sendMessage("/lead set config extra " + lead_only_player + " 真偽値");
                                }
                                return true;
                            }
                            if (a[3].equals(possessive_mode)) {
                                try {
                                    config.possessive_mode = Boolean.parseBoolean(a[4]);
                                    s.sendMessage("§a" + possessive_mode + "を" + a[4] + "にしました");
                                    return true;
                                } catch (Exception e) {
                                    s.sendMessage("/lead set config extra " + possessive_mode + " 真偽値");
                                }
                                return true;
                            }
                            if (a[3].equals(particle_mode)) {
                                try {
                                    config.particle_mode = Boolean.parseBoolean(a[4]);
                                    s.sendMessage("§a" + particle_mode + "を" + a[4] + "にしました");
                                    return true;
                                } catch (Exception e) {
                                    s.sendMessage("/lead set config extra " + particle_mode + " 真偽値");
                                }
                                return true;
                            }
                            if (a[3].equals(particle_type)) {
                                try {
                                    if (!config.setParticleType(a[4])) {
                                        throw new Exception("");
                                    }
                                    s.sendMessage("§a" + particle_type + "を" + a[4] + "にしました");
                                    return true;
                                } catch (Exception e) {
                                    s.sendMessage("/lead set config extra " + particle_type + " Particle");
                                }
                                return true;
                            }
                        }
                    }
                }
            } catch (Exception ignored){
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender s, Command c, String l, String[] a) {
        if(c.getName().equals("lead")) {
            if(a.length == 1) {
                return Stream.of("show", "set").filter(e -> e.startsWith(a[0])).collect(Collectors.toList());
            }
            if(a.length == 2 && (a[0].equals("show") || a[0].equals("set"))) {
                return Stream.of("config").filter(e -> e.startsWith(a[1])).collect(Collectors.toList());
            }
            if(a.length == 3 && (a[0].equals("set") && a[1].equals("config"))) {
                return Stream.of("main", "extra").filter(e -> e.startsWith(a[2])).collect(Collectors.toList());
            }
            if(a.length == 4 && (a[0].equals("set") && a[1].equals("config") && a[2].equals("main"))) {
                return Stream.of(holder_power,target_power,max_distance,force_teleport_distance)
                        .filter(e -> e.startsWith(a[3])).collect(Collectors.toList());
            }
            if(a.length == 4 && (a[0].equals("set") && a[1].equals("config") && a[2].equals("extra"))) {
                return Stream.of(lead_only_player,possessive_mode,particle_mode,particle_type)
                        .filter(e -> e.startsWith(a[3])).collect(Collectors.toList());
            }
            if(a.length == 5 && (a[3].equals(holder_power) || a[3].equals(target_power) || a[3].equals(max_distance)
                    || a[3].equals(force_teleport_distance))) {
                return Collections.singletonList("数値");
            }
            if(a.length == 5 && ( a[3].equals(lead_only_player) || a[3].equals(particle_mode) || a[3].equals(possessive_mode))) {
                return Stream.of("true", "false").filter(e -> e.startsWith(a[4])).collect(Collectors.toList());
            }
            if(a.length == 5 && a[3].equals(particle_type)) {
                ArrayList<String> particleNames = new ArrayList<String>();
                Arrays.stream(Particle.values()).forEach(e -> particleNames.add(e.toString()));
                return particleNames.stream().filter(e -> e.startsWith(a[4])).collect(Collectors.toList());
            }
        }
        return null;
    }

}