package org.wayggstar.party.Party;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class PPVP implements Listener {
    private final PartyManager partyManager;

    public PPVP(PartyManager partyManager){
        this.partyManager = partyManager;
    }

    @EventHandler
    public void onPVP(EntityDamageByEntityEvent e){
        if (e.getEntity().getType() == EntityType.PLAYER){return;}
        if (e.getDamager().getType() != EntityType.PLAYER){return;}
        Player victim = (Player) e.getEntity();
        Player attacker = (Player) e.getDamager();
        String VicParty = partyManager.getPlayerParty(victim.getUniqueId());
        String AtcParty = partyManager.getPlayerParty(attacker.getUniqueId());
        if (VicParty.equals(AtcParty)){
            if (!partyManager.isPartyPVPEnabled(AtcParty)){
                e.setCancelled(true);
                attacker.sendMessage("§c파티 PVP가 비활성화 상태입니다..");
            }
        }
    }
}
