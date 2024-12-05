package org.wayggstar.party.Party;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PartyManager {
    public Map<String, Party> partyMap = new HashMap<>();
    public Map<String, List<UUID>> partyMembers = new HashMap<>();
    public Map<UUID, String> playerPartyMap = new HashMap<>();
    public Map<UUID, UUID> playerInvites = new HashMap<>();
    public Map<UUID, Boolean> PartyChat = new HashMap<>();
    public Map<String, Boolean> PartyPVP = new HashMap<>();

    public boolean createParty(String name, UUID leader){
        if (partyMap.containsKey(name)){
            return false;
        }
        partyMap.put(name, new Party(name, leader));
        return true;
    }

    public boolean removeParty(String name){
        if (!partyMap.containsKey(name)){
            return false;
        }
        partyMap.remove(name);
        return true;
    }

    public boolean addMember(String name, UUID member){
        Party party = partyMap.get(name);
        if (party == null){
            return false;
        }
        party.addMember(member);
        return true;
    }

    public boolean removeMember(String name, UUID member){
        Party party = partyMap.get(name);
        if (party == null){
            return false;
        }
        party.removeMember(member);
        if (party.getMembers().isEmpty()){
            removeParty(name);
        }
        return true;
    }

    public Map<String, Party> getAllParties(){
        return partyMap;
    }

    public String getPlayerParty(UUID player) {
        return playerPartyMap.get(player);
    }

    public boolean isPlayerInParty(UUID target){
        return playerPartyMap.containsKey(target);
    }


    public boolean invitePlayerToParty(UUID inviter, UUID invitee) {
        Player target = Bukkit.getPlayer(invitee);
        Player sender = Bukkit.getPlayer(inviter);
        if (playerInvites.containsKey(invitee)) {
            return false;
        }
        playerInvites.put(invitee, inviter);
        target.sendMessage(ChatColor.GREEN + sender.getName() + "§a님에게 파티초대가 왔습니다. /파티 [수락|거절]");

        return true;
    }

    public boolean isInviteExpired(UUID invitee) {
        return !playerInvites.containsKey(invitee);
    }

    public void removeInvite(UUID invitee) {
        playerInvites.remove(invitee);
    }

    public boolean isPartyChatEnabled(UUID player) {
        return PartyChat.getOrDefault(player, false);
    }

    public boolean isPartyPVPEnabled(String partyName) {
        return PartyPVP.getOrDefault(partyName, false);
    }

    public List<UUID> getPartyMembers(String partyName) {
        Party party = partyMap.get(partyName);
        if (party == null) {
            return null;
        }
        return party.getMembers();
    }
}
