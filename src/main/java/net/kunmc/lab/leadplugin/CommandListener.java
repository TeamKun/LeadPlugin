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
    final private LeadPlugin lp;
    final private WireAPI wa;

    final private String holder_power = "holder_power";
    final private String target_power = "target_power";
    final private String max_distance = "max_distance";
    final private String force_pull_power = "force_pull_power";
    final private String force_teleport_distance = "force_teleport_distance";
    final private String lead_after_death = "lead_after_death";
    final private String lead_only_player = "lead_only_player";
    final private String particle_mode = "particle_mode";
    final private String particle_type = "particle_type";

    public CommandListener(LeadPlugin lp, WireAPI wa) {
        this.lp = lp;
        this.wa = wa;
        Bukkit.getPluginCommand("lead").setExecutor(this);
        Bukkit.getPluginCommand("lead").setTabCompleter(this);
    }
    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[]a) {
        if(c.getName().equals("lead")) {
            /*
            if(a[0].equals("right")) {
                if(a[1].equals("multiple")) {
                    try {
                        boolean isMultiple = Boolean.parseBoolean(a[3]);
                        if(a[2].equals("@a")) {
                            lp.getServer().getOnlinePlayers().forEach(p -> {
                                if(lp.getInfoMap().containsKey(p.getName())) {
                                    PlayerInfo pInfo = lp.getInfoMap().get(p.getName());
                                    lp.quit(pInfo);
                                    pInfo.release();
                                    pInfo.setMultiple(isMultiple);
                                }
                            });
                            s.sendMessage("§a全てのプレイヤーのmultipleを" + isMultiple + "にしました");
                            return true;
                        }
                        if(lp.getInfoMap().containsKey(a[2])) {
                            PlayerInfo pInfo = lp.getInfoMap().get(a[2]);
                            lp.quit(pInfo);
                            pInfo.release();
                            pInfo.setMultiple(isMultiple);
                            s.sendMessage("§a" + a[2] + "のmultipleを" + isMultiple + "にしました");
                            return true;
                        }
                    } catch (Exception ignored) {
                    }
                }
                s.sendMessage("無効な値です");
                return true;
            }
             */
            if(a[0].equals("config")) {
                if(a[1].equals("confirm")) {
                    s.sendMessage("§a" + holder_power + " : " + lp.holder_power);
                    s.sendMessage("§a" + target_power + " : " + lp.target_power);
                    s.sendMessage("§a" + force_pull_power + " : " + lp.force_pull_power);
                    s.sendMessage("§a" + max_distance + " : " + lp.max_distance);
                    s.sendMessage("§a" + force_teleport_distance + " : " + lp.force_teleport_distance);
                    s.sendMessage("§a" + lead_after_death + " : " + lp.lead_after_death);
                    s.sendMessage("§a" + lead_only_player + " : " + lp.lead_only_player);
                    s.sendMessage("§a" + particle_mode + " : " + lp.particle_mode);
                    s.sendMessage("§a" + particle_type + " : " + lp.getParticle());
                    return true;
                }
                if(a[1].equals("power")) {
                    if (a[2].equals(holder_power)) {
                        try {
                            lp.holder_power = Double.parseDouble(a[3]);
                            s.sendMessage("§a" + holder_power + "を" + a[3] + "にしました");
                        } catch (Exception e) {
                            s.sendMessage("無効な値です");
                        }
                        return true;
                    }
                    if (a[2].equals(target_power)) {
                        try {
                            lp.target_power = Double.parseDouble(a[3]);
                            s.sendMessage("§a" + target_power + "を" + a[3] + "にしました");
                        } catch (Exception e) {
                            s.sendMessage("無効な値です");
                        }
                        return true;
                    }
                    if(a[2].equals(force_pull_power)) {
                        try {
                            lp.force_pull_power = Double.parseDouble(a[3]);
                            s.sendMessage("§a" + force_pull_power +"を" + a[3] + "にしました");
                        } catch (Exception e) {
                            s.sendMessage("無効な値です");
                        }
                        return true;
                    }
                }
                if(a[1].equals("distance")) {
                    if (a[2].equals(max_distance)) {
                        try {
                            lp.max_distance = Double.parseDouble(a[3]);
                            s.sendMessage("§a" + max_distance + "を" + a[3] + "にしました");
                        } catch (Exception e) {
                            s.sendMessage("無効な値です");
                        }
                        return true;
                    }
                    if (a[2].equals(force_teleport_distance)) {
                        try {
                            lp.force_teleport_distance = Double.parseDouble(a[3]);
                            s.sendMessage("§a" + force_teleport_distance + "を" + a[3] + "にしました");
                        } catch (Exception e) {
                            s.sendMessage("無効な値です");
                        }
                        return true;
                    }
                }
                if(a[1].equals("option")) {
                    if (a[2].equals(lead_after_death)) {
                        try {
                            lp.lead_after_death = Boolean.parseBoolean(a[3]);
                            s.sendMessage("§a" + lead_after_death + "を" + a[3] + "にしました");
                        } catch (Exception e) {
                            s.sendMessage("無効な値です");
                        }
                        return true;
                    }
                    if (a[2].equals(lead_only_player)) {
                        try {
                            lp.lead_only_player = Boolean.parseBoolean(a[3]);
                            s.sendMessage("§a" + lead_only_player + "を" + a[3] + "にしました");
                        } catch (Exception e) {
                            s.sendMessage("無効な値です");
                        }
                        return true;
                    }
                }
                if(a[1].equals("extra")) {
                    if (a[2].equals(particle_mode)) {
                        try {
                            lp.particle_mode = Boolean.parseBoolean(a[3]);
                            s.sendMessage("§a" + particle_mode + "を" + a[3] + "にしました");
                            if(lp.particle_mode) {
                                wa.initWire();
                            }
                        } catch (Exception e) {
                            s.sendMessage("無効な値です");
                        }
                        return true;
                    }
                    if (a[2].equals(particle_type)) {
                        try {
                            if (!lp.setParticleType(a[3])) {
                                throw new Exception("");
                            }
                            s.sendMessage("§a" + particle_type + "を" + a[3] + "にしました");
                        } catch (Exception e) {
                            s.sendMessage("無効な値です");
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender s, Command c, String l, String[] a) {
        if(c.getName().equals("lead")) {
            if(a.length == 1) {
                // return Stream.of("config", "right").filter(e -> e.startsWith(a[0])).collect(Collectors.toList());
                return Stream.of("config").filter(e -> e.startsWith(a[0])).collect(Collectors.toList());
            }
            if(a.length == 2 && a[0].equals("config")) {
                return Stream.of("confirm", "power", "distance", "option", "extra")
                        .filter(e -> e.startsWith(a[1])).collect(Collectors.toList());
            }
            /*
            if(a.length == 2 && a[0].equals("right")) {
                return Stream.of("multiple").filter(e -> e.startsWith(a[1])).collect(Collectors.toList());
            }
            if(a.length == 3 && a[1].equals("multiple")) {
                ArrayList<String> players = new ArrayList<String>();
                lp.getServer().getOnlinePlayers().forEach(p -> players.add(p.getName()));
                players.add("@a");
                return players.stream().filter(e -> e.startsWith(a[2])).collect(Collectors.toList());
            }
             */
            if(a.length == 3 && a[1].equals("power")) {
                return Stream.of(holder_power, target_power, force_pull_power)
                        .filter(e -> e.startsWith(a[2])).collect(Collectors.toList());
            }
            if(a.length == 3 && a[1].equals("distance")) {
                return Stream.of(max_distance, force_teleport_distance)
                        .filter(e -> e.startsWith(a[2])).collect(Collectors.toList());
            }
            if(a.length == 3 && a[1].equals("option")) {
                return Stream.of(lead_after_death, lead_only_player)
                        .filter(e -> e.startsWith(a[2])).collect(Collectors.toList());
            }
            if(a.length == 3 && a[1].equals("extra")) {
                return Stream.of(particle_mode, particle_type)
                        .filter(e -> e.startsWith(a[2])).collect(Collectors.toList());
            }
            if(a.length == 4 && (a[2].equals(holder_power) || a[2].equals(target_power) || a[2].equals(force_pull_power) || a[2].equals(max_distance)
                    || a[2].equals(force_teleport_distance))) {
                return Collections.singletonList("数値");
            }
            /*
            if(a.length == 4 && (a[2].equals(lead_after_death) || a[2].equals(lead_only_player) || a[2].equals(particle_mode) || a[0].equals("right"))) {
                return Stream.of("true", "false").filter(e -> e.startsWith(a[3])).collect(Collectors.toList());
            }
             */
            if(a.length == 4 && (a[2].equals(lead_after_death) || a[2].equals(lead_only_player) || a[2].equals(particle_mode))) {
                return Stream.of("true", "false").filter(e -> e.startsWith(a[3])).collect(Collectors.toList());
            }
            if(a.length == 4 && a[2].equals(particle_type)) {
                ArrayList<String> particleNames = new ArrayList<String>();
                Arrays.stream(Particle.values()).forEach( e -> particleNames.add(e.toString()));
                return particleNames.stream().filter(e -> e.startsWith(a[3])).collect(Collectors.toList());
            }
        }
        return null;
    }

}