package org.wayggstar.party.Party;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PChat implements Listener {
    private final PartyManager partyManager;

    public PChat(PartyManager partyManager){
        this.partyManager = partyManager;
    }

    @EventHandler
    public void Pchat(AsyncPlayerChatEvent e){
        Player sender = e.getPlayer();
        String senderParty = partyManager.getPlayerParty(sender.getUniqueId());

        if (partyManager.PartyChat.get(sender.getUniqueId())){
            e.getRecipients().removeIf(recipient -> {
                return !partyManager.getPlayerParty(recipient.getUniqueId()).equals(senderParty);
            });

            e.setFormat("§7[§b§l파티 채팅§7]" +"%s: %s");
        }
    }
}
