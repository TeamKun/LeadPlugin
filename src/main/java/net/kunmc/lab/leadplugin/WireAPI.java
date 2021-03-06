package net.kunmc.lab.leadplugin;

import me.saharnooby.plugins.leadwires.LeadWires;
import me.saharnooby.plugins.leadwires.api.LeadWiresAPI;
import me.saharnooby.plugins.leadwires.wire.Wire;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class WireAPI {
    private static WireAPI instance;
    private LeadPlugin plugin;

    public static WireAPI getInstance() {
        return instance;
    }

    public WireAPI() {
        plugin = LeadPlugin.getInstance();
        init();
    }

    public void init() {
        plugin.getServer().getScheduler().runTask(plugin, new Runnable() {
            @Override
            public void run() {
                LeadWiresAPI api = LeadWires.getApi();
                HashMap<UUID, Wire> wires = new HashMap<UUID,Wire>(api.getWires());
                wires.keySet().forEach(api::removeWire);
            }
        });
    }

    public void set(PlayerInfo hInfo, PlayerInfo tInfo) {
        plugin.getServer().getScheduler().runTask(plugin, new Runnable() {
            @Override
            public void run() {
                LeadWiresAPI api = LeadWires.getApi();
                ArrayList<UUID> wires = new ArrayList<UUID>(hInfo.getWires());
                wires.add(api.addWire(hInfo.getOrigin().getLocation().add(0,1,0), tInfo.getOrigin().getLocation().add(0,1,0)));
                hInfo.setWires(wires);
            }
        });
    }

    public void remove(PlayerInfo hInfo) {
        plugin.getServer().getScheduler().runTask(plugin, new Runnable() {
            @Override
            public void run() {
                LeadWiresAPI api = LeadWires.getApi();
                ArrayList<UUID> wires = new ArrayList<UUID>(hInfo.getWires());
                wires.forEach(api::removeWire);
                hInfo.setWires(new ArrayList<UUID>());
            }
        });
    }
}