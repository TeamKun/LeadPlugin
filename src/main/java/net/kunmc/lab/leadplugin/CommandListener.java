package net.kunmc.lab.leadplugin;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CommandListener implements TabExecutor {
    final private LeadPlugin lp;
    public CommandListener(LeadPlugin lp) {
        this.lp = lp;
        Bukkit.getPluginCommand("lead").setExecutor(this);
        Bukkit.getPluginCommand("lead").setTabCompleter(this);
    }
    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[]a) {
        if(a.length != 3){ return false;}
        if(c.getName().equals("lead")) {
            if(a[0].equals("config")) {
                if(a[1].equals("holder_power")) {
                    try {
                        lp.holder_power = Double.parseDouble(a[2]);
                        s.sendMessage("§a" + "holder_powerを" + a[2] + "にしました");
                    } catch (Exception e) {
                        s.sendMessage("無効な値です");
                    }
                    return true;
                }
                if(a[1].equals("target_power")) {
                    try {
                        lp.target_power = Double.parseDouble(a[2]);
                        s.sendMessage("§a" + "target_powerを" + a[2] + "にしました");
                    } catch (Exception e) {
                        s.sendMessage("無効な値です");
                    }
                    return true;
                }
                if(a[1].equals("max_distance")) {
                    try {
                        lp.max_distance = Double.parseDouble(a[2]);
                        s.sendMessage("§a" + "max_distanceを" + a[2] + "にしました");
                    } catch (Exception e) {
                        s.sendMessage("無効な値です");
                    }
                    return true;
                }
                if(a[1].equals("lead_after_death")) {
                    try {
                        lp.lead_after_death = Boolean.parseBoolean(a[2]);
                        s.sendMessage("§a" + "lead_after_deathを" + a[2] + "にしました");
                    } catch (Exception e) {
                        s.sendMessage("無効な値です");
                    }
                    return true;
                }
                if(a[1].equals("lead_only_player")) {
                    try {
                        lp.lead_only_player = Boolean.parseBoolean(a[2]);
                        s.sendMessage("§a" + "lead_only_playerを" + a[2] + "にしました");
                    } catch (Exception e) {
                        s.sendMessage("無効な値です");
                    }
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender s, Command c, String l, String[] a) {
        if(c.getName().equals("lead")) {
            if(a.length == 1) {
                return Stream.of("config").filter(e -> e.startsWith(a[0])).collect(Collectors.toList());
            }
            if(a.length == 2 && a[0].equals("config")) {
                return Stream.of("holder_power", "target_power", "max_distance", "lead_after_death", "lead_only_player")
                        .filter(e -> e.startsWith(a[1])).collect(Collectors.toList());
            }
            if(a.length == 3 && (a[1].equals("holder_power") || a[1].equals("target_power") || a[1].equals("max_distance"))) {
                return Collections.singletonList("数値");
            }
            if(a.length == 3 && (a[1].equals("lead_after_death") || a[1].equals("lead_only_player"))) {
                return Stream.of("true", "false").filter(e -> e.startsWith(a[2])).collect(Collectors.toList());
            }
        }
        return null;
    }

}
