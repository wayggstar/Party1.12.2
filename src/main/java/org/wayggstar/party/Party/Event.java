package org.wayggstar.party.Party;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;
import java.util.UUID;

public class Event implements Listener {

    private final PartyManager partyManager;

    public Event(PartyManager partyManager){
        this.partyManager = partyManager;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        UUID senderUUID = e.getPlayer().getUniqueId();
        String partyName = partyManager.getPlayerParty(senderUUID);
        if (partyName == null) {
            return;
        }
        if (partyManager.removeMember(partyName, senderUUID)) {
            List<UUID> members = partyManager.getPartyMembers(partyName);
            if (members == null || members.isEmpty()){
                return;
            }
            for (UUID memberID : members) {
                Player Pmember = Bukkit.getPlayer(memberID);
                if (Pmember.isOnline()) {
                    Pmember.sendMessage(ChatColor.RED + "'" + e.getPlayer().getName() + "'이(가) 파티를 떠났습니다.");
                }
            }
        }
    }
}
