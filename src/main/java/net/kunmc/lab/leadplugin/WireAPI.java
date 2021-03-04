package net.kunmc.lab.leadplugin;

import me.saharnooby.plugins.leadwires.LeadWires;
import me.saharnooby.plugins.leadwires.api.LeadWiresAPI;
import me.saharnooby.plugins.leadwires.wire.Wire;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class WireAPI {
    final private LeadPlugin lp;

    public WireAPI(LeadPlugin lp) {
        this.lp = lp;
        initWire();
        wireTask();
    }

     public void initWire() {
        lp.getServer().getScheduler().runTask(lp, new Runnable() {
            @Override
            public void run() {
                LeadWiresAPI wireApi = LeadWires.getApi();
                HashMap<UUID, Wire> wires = new HashMap<UUID, Wire>(wireApi.getWires());
                wires.keySet().forEach(wireApi::removeWire);
            }
        });
    }

    private void  wireTask() {
        lp.getServer().getScheduler().scheduleSyncRepeatingTask(lp, new Runnable() {
            final LeadWiresAPI wireAPI = LeadWires.getApi();
            HashMap<String, PlayerInfo> infoMap;
            @Override
            public void run() {
                lp.getServer().getOnlinePlayers().forEach(p -> {
                    infoMap = lp.getInfoMap();
                    if(!infoMap.containsKey(p.getName())) {return;}
                    PlayerInfo pInfo = infoMap.get(p.getName());
                    if(!pInfo.isLeashing()) {return;}
                    if(!pInfo.isMultiple()) {
                        if (pInfo.isAddWire() && pInfo.isHolder()) {
                            PlayerInfo tInfo = infoMap.get(pInfo.getPairName());
                            if (pInfo.getWire() != null) {
                                wireAPI.removeWire(pInfo.getWire());
                            }
                            pInfo.setWire(wireAPI.addWire(p.getLocation().add(0, 1, 0), tInfo.getOrigin().getLocation().add(0, 1, 0)));
                            pInfo.setAddWire(false);
                        }
                        return;
                    }
                    if(pInfo.isHolder() && pInfo.isMultiple()) {
                        ArrayList<String> pairNames = new ArrayList<String>(pInfo.getPairNames());
                        pairNames.forEach(pairName -> {
                            PlayerInfo tInfo = infoMap.get(pairName);
                            HashMap<String, Boolean> pairAddWires = pInfo.getPairAddWires();
                            HashMap<String, UUID> pairWires = pInfo.getPairWires();
                            if(tInfo.isLeashing() && tInfo.getPairName() != null && tInfo.getPairName().equals(p.getName())) {
                                if(pairAddWires.containsKey(pairName)) {
                                    if(pairAddWires.get(pairName) && pairWires.containsKey(pairName)) {
                                        if(pairWires.get(pairName) != null) {
                                            wireAPI.removeWire(pairWires.get(pairName));
                                        }
                                        pairWires.put(pairName, wireAPI.addWire(p.getLocation().add(0, 1, 0), tInfo.getOrigin().getLocation().add(0, 1,0)));
                                        pairAddWires.put(pairName, false);
                                    }
                                }
                            }
                        });
                    }
                });
            }
        }, 0L, 2L);
    }

    public void removeWire(UUID wire) {
        lp.getServer().getScheduler().runTask(lp, new Runnable() {
            @Override
            public void run() {
                LeadWires.getApi().removeWire(wire);
            }
        });
    }

}