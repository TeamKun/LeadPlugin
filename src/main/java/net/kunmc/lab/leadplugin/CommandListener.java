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
                if (a[0].equals("config")) {
                    try {
                        if (a[1].equals("show")) {
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
                    } catch (Exception ignored) {
                        return true;
                    }
                    try {
                        if (a[1].equals("set")) {
                            if (a[2].equals("main")) {
                                if (a[3].equals(holder_power)) {
                                    try {
                                        double d = Double.parseDouble(a[4]);
                                        if(d < 0) {
                                            throw new Exception("");
                                        }
                                        config.holder_power = d;
                                        s.sendMessage("§a" + holder_power + "を" + d + "にしました");
                                        return true;
                                    } catch (Exception e) {
                                        s.sendMessage("/lead config set main " + holder_power + " 数値 (0以上) ");
                                    }
                                    return true;
                                }
                                if (a[3].equals(target_power)) {
                                    try {
                                        double d = Double.parseDouble(a[4]);
                                        if(d < 0) {
                                            throw new Exception("");
                                        }
                                        config.target_power = d;
                                        s.sendMessage("§a" + target_power + "を" + d + "にしました");
                                        return true;
                                    } catch (Exception e) {
                                        s.sendMessage("/lead config set main " + target_power + " 数値 (0以上) ");
                                    }
                                    return true;
                                }
                                if (a[3].equals(max_distance)) {
                                    try {
                                        double d = Double.parseDouble(a[4]);
                                        if(d < 0) {
                                            throw new Exception("");
                                        }
                                        config.max_distance = d;
                                        s.sendMessage("§a" + max_distance + "を" + a[4] + "にしました");
                                        return true;
                                    } catch (Exception e) {
                                        s.sendMessage("/lead config set main " + max_distance + " 数値 (0以上) ");
                                    }
                                    return true;
                                }
                                if (a[3].equals(force_teleport_distance)) {
                                    try {
                                        double d = Double.parseDouble(a[4]);
                                        if(d < 0) {
                                            throw new Exception("");
                                        }
                                        config.force_teleport_distance = d;
                                        s.sendMessage("§a" + force_teleport_distance + "を" + d + "にしました");
                                        return true;
                                    } catch (Exception e) {
                                        s.sendMessage("/lead config set main " + force_teleport_distance + " 数値 (0以上) ");
                                    }
                                    return true;
                                }
                                s.sendMessage("コンフィグの値を変更するとき");
                                s.sendMessage("--" + holder_power + "/" + target_power + "/" + max_distance + "/" + force_teleport_distance + "--");
                                s.sendMessage("§a/lead config set main " + holder_power + "/" + target_power + "/" + max_distance + "/" + force_teleport_distance + " 数値（0以上) ");
                                return true;
                            }
                            if (a[2].equals("extra")) {
                                if (a[3].equals(lead_only_player)) {
                                    try {
                                        config.lead_only_player = Boolean.parseBoolean(a[4]);
                                        s.sendMessage("§a" + lead_only_player + "を" + a[4] + "にしました");
                                        return true;
                                    } catch (Exception e) {
                                        s.sendMessage("/lead config set extra " + lead_only_player + " true or false ");
                                    }
                                    return true;
                                }
                                if (a[3].equals(possessive_mode)) {
                                    try {
                                        config.possessive_mode = Boolean.parseBoolean(a[4]);
                                        s.sendMessage("§a" + possessive_mode + "を" + a[4] + "にしました");
                                        return true;
                                    } catch (Exception e) {
                                        s.sendMessage("/lead config set extra " + possessive_mode + " true or false ");
                                    }
                                    return true;
                                }
                                if (a[3].equals(particle_mode)) {
                                    try {
                                        config.particle_mode = Boolean.parseBoolean(a[4]);
                                        s.sendMessage("§a" + particle_mode + "を" + a[4] + "にしました");
                                        return true;
                                    } catch (Exception e) {
                                        s.sendMessage("/lead config set extra " + particle_mode + " true or false ");
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
                                        s.sendMessage("/lead config set extra " + particle_type + " Particle");
                                    }
                                    return true;
                                }
                                s.sendMessage("コンフィグの値を変更するとき");
                                s.sendMessage("--" + lead_only_player + "/" + possessive_mode + "/" + particle_mode + "--");
                                s.sendMessage("§a/lead config set extra " + lead_only_player + "/" + possessive_mode + "/" + particle_mode +  " true or false ");
                                s.sendMessage("--" + particle_type +  "--");
                                s.sendMessage("§a/lead config set extra " + particle_type + " Particle");
                                return true;
                            }
                        }
                    } catch (Exception ignored) {
                        s.sendMessage("コンフィグの値を変更するとき");
                        s.sendMessage("--" + holder_power + ", " + target_power + ", " + max_distance + ", " + force_teleport_distance + "--");
                        s.sendMessage("§a/lead config set main 項目名 数値（0以上) ");
                        s.sendMessage("--" + lead_only_player + ", " + possessive_mode + ", " + particle_mode + "--");
                        s.sendMessage("§a/lead config set extra 項目名 true or false ");
                        s.sendMessage("--" + particle_type +  "--");
                        s.sendMessage("§a/lead config set extra  項目名 Particle");
                        return true;
                    }
                }
                if(a[0].equals("help")) {
                    s.sendMessage("--使い方--");
                    s.sendMessage("コンフィグの値を閲覧するとき");
                    s.sendMessage("§a/lead config show");
                    s.sendMessage("コンフィグの値を変更するとき");
                    s.sendMessage("--" + holder_power + ", " + target_power + ", " + max_distance + ", " + force_teleport_distance + "--");
                    s.sendMessage("§a/lead config set main 項目名 数値（0以上) ");
                    s.sendMessage("--" + lead_only_player + ", " + possessive_mode + ", " + particle_mode + "--");
                    s.sendMessage("§a/lead config set extra 項目名 true or false ");
                    s.sendMessage("--" + particle_type +  "--");
                    s.sendMessage("§a/lead config set extra  項目名 Particle");
                    return true;
                }
            } catch (Exception ignored) {
            }
        }
        s.sendMessage("/lead help" + " で使い方を参照してください");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender s, Command c, String l, String[] a) {
        if(c.getName().equals("lead")) {
            if(a.length == 1) {
                return Stream.of("config", "help").filter(e -> e.startsWith(a[0])).collect(Collectors.toList());
            }
            if(a.length == 2 && a[0].equals("config")) {
                return Stream.of("show", "set").filter(e -> e.startsWith(a[1])).collect(Collectors.toList());
            }
            if(a.length == 3 && (a[0].equals("config") && a[1].equals("set"))) {
                return Stream.of("main", "extra").filter(e -> e.startsWith(a[2])).collect(Collectors.toList());
            }
            if(a.length == 4 && (a[1].equals("set") && a[0].equals("config") && a[2].equals("main"))) {
                return Stream.of(holder_power,target_power,max_distance,force_teleport_distance)
                        .filter(e -> e.startsWith(a[3])).collect(Collectors.toList());
            }
            if(a.length == 4 && (a[1].equals("set") && a[0].equals("config") && a[2].equals("extra"))) {
                return Stream.of(lead_only_player,possessive_mode,particle_mode,particle_type)
                        .filter(e -> e.startsWith(a[3])).collect(Collectors.toList());
            }
            if(a.length == 5 && (a[3].equals(holder_power) || a[3].equals(target_power) || a[3].equals(max_distance)
                    || a[3].equals(force_teleport_distance))) {
                return Collections.singletonList("数値 (0以上) ");
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