package net.kunmc.lab.leadplugin;

import me.saharnooby.plugins.leadwires.LeadWires;
import me.saharnooby.plugins.leadwires.api.LeadWiresAPI;

import java.util.HashMap;
import java.util.UUID;

public class WireAPI {
    final private LeadPlugin lp;

    public WireAPI(LeadPlugin lp) {
        this.lp = lp;
        initWire();
        wireTask();
    }

    private void initWire() {
        lp.getServer().getScheduler().runTask(lp, new Runnable() {
            @Override
            public void run() {
                LeadWiresAPI wireApi = LeadWires.getApi();
                wireApi.getWires().forEach((u, w) -> {
                    wireApi.removeWire(u);
                });
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
                    PlayerInfo tInfo = infoMap.get(pInfo.getPairName());
                    if(pInfo.isAddWire() && pInfo.isHolder()) {
                        if(pInfo.getWire() != null) {
                            wireAPI.removeWire(pInfo.getWire());
                        }
                        pInfo.setWire(wireAPI.addWire(p.getLocation().add(0, 1, 0), tInfo.getOrigin().getLocation().add(0, 1, 0)));
                        pInfo.setAddWire(false);
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
