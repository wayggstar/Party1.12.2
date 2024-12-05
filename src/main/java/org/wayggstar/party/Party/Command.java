package org.wayggstar.party.Party;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.UUID;

public class Command implements CommandExecutor {

    private final PartyManager partyManager;
    private final JavaPlugin plugin;

    public Command(PartyManager partyManager,Party party,JavaPlugin plugin) {
        this.partyManager = partyManager;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (label.equalsIgnoreCase("파티")) {

                if (args.length == 2 && args[0].equalsIgnoreCase("생성")) {
                    String partyName = args[1];
                    UUID playerUUID = player.getUniqueId();

                    if (partyManager.isPlayerInParty(playerUUID)) {
                        player.sendMessage("§c이미 다른 파티에 가입 중입니다.");
                        return true;
                    }
                    if (partyManager.createParty(partyName, playerUUID)) {
                        player.sendMessage(partyName + "§a파티가 생성되었습니다.");
                    } else {
                        player.sendMessage("§c이미 존재하는 파티 이름이거나 파티를 생성할 수 없습니다.");
                    }
                }

                else if (args.length == 2 && args[0].equalsIgnoreCase("초대")) {
                    String targetName = args[1];
                    Player targetPlayer = Bukkit.getPlayer(targetName);
                    if (targetPlayer == null) {
                        player.sendMessage("§c해당 플레이어는 온라인이 아닙니다.");
                        return false;
                    }

                    UUID targetUUID = targetPlayer.getUniqueId();

                    if (partyManager.isPlayerInParty(targetUUID)) {
                        player.sendMessage("§c해당 플레이어는 이미 다른 파티에 가입 중입니다.");
                        return true;
                    }
                    if (partyManager.invitePlayerToParty(player.getUniqueId(), targetUUID)) {
                        targetPlayer.sendMessage("§a" + player.getName() + "님이 파티에 초대했습니다. /파티 수락 또는 /파티 거절로 응답하세요.");
                        player.sendMessage("§a" + targetPlayer.getName() + "님에게 파티 초대를 보냈습니다.");
                        Bukkit.getScheduler().runTaskLater(plugin, () -> {
                            if (partyManager.isInviteExpired(targetPlayer.getUniqueId())) {
                                partyManager.removeInvite(targetPlayer.getUniqueId());
                                targetPlayer.sendMessage("§c30초가 지나 초대가 자동으로 취소되었습니다.");
                                player.sendMessage("§c" + ChatColor.RED + targetPlayer.getName() + "§c님의 초대가 자동으로 취소되었습니다.");
                            }
                        }, 600); // 600 ticks = 30초
                    } else {
                        player.sendMessage("§c초대에 실패했습니다.");
                    }
                }

                else if (args.length == 1 && args[0].equalsIgnoreCase("수락")) {
                    UUID inviterUUID = partyManager.playerInvites.get(player.getUniqueId());
                    if (inviterUUID == null) {
                        player.sendMessage("§c당신에게 초대가 없습니다.");
                        return true;
                    }

                    String partyName = partyManager.getPlayerParty(Bukkit.getPlayer(inviterUUID).getUniqueId());
                    if (partyName != null && partyManager.addMember(partyName, player.getUniqueId())) {
                        player.sendMessage("§a파티에 가입되었습니다!");
                        Player inviterPlayer = Bukkit.getPlayer(inviterUUID);
                        if (inviterPlayer != null) {
                            inviterPlayer.sendMessage("§a" + player.getName() + "님이 파티에 가입했습니다.");
                        }
                    } else {
                        player.sendMessage("§c초대에 실패했습니다.");
                    }
                    partyManager.playerInvites.remove(player.getUniqueId());
                }

                else if (args.length == 1 && args[0].equalsIgnoreCase("거절")) {
                    UUID inviterUUID = partyManager.playerInvites.get(player.getUniqueId());
                    if (inviterUUID == null) {
                        player.sendMessage("§c당신에게 초대가 없습니다.");
                        return true;
                    }
                    player.sendMessage("§c파티 초대를 거절했습니다.");
                    Player inviterPlayer = Bukkit.getPlayer(inviterUUID);
                    if (inviterPlayer != null) {
                        inviterPlayer.sendMessage("§c" + player.getName() + "님이 파티 초대를 거절했습니다.");
                    }
                    partyManager.playerInvites.remove(player.getUniqueId());
                }
                else if (args.length == 1 && args[0].equalsIgnoreCase("채팅")){
                    if (partyManager.isPartyChatEnabled(player.getUniqueId())) {
                        player.sendMessage("§a파티채팅모드가 켜졌습니다.");
                        partyManager.PartyChat.put(player.getUniqueId(), true);
                    } else{
                        partyManager.PartyChat.put(player.getUniqueId(), false);
                        player.sendMessage("§a파티채팅모드가 꺼졌습니다.");
                    }
                }
                else if (args.length == 1 && args[0].equalsIgnoreCase("전투")){
                   String partyName = partyManager.getPlayerParty(player.getUniqueId());
                   Party playerparty = partyManager.partyMap.get(partyName);
                   if (playerparty == null){
                       player.sendMessage("§c당신은 파티에 속해있지 않습니다.");
                       return true;
                   }
                   if (!player.equals(playerparty.getLeader())){
                       player.sendMessage("§c당신은 파티장이 아닙니다.");
                       return true;
                   }

                   if (partyManager.isPartyPVPEnabled(partyName)) {
                       partyManager.PartyPVP.put(partyName, true);
                       List<UUID> members = partyManager.getPartyMembers(partyName);
                       if (members == null || members.isEmpty()){
                           return true;
                       }

                       for (UUID memberID : members) {
                           Player Pmember = Bukkit.getPlayer(memberID);
                           if (player.isOnline()) {
                               Pmember.sendMessage(" ");
                               Pmember.sendMessage("§c파티장이 파티 PVP를 활성화 했습니다.");
                               Pmember.sendMessage(" ");

                           }
                       }
                   }
                   else {
                       partyManager.PartyPVP.put(partyName, false);
                       List<UUID> members = partyManager.getPartyMembers(partyName);
                       if (members == null || members.isEmpty()){
                           return true;
                       }

                       for (UUID memberID : members) {
                           Player Pmember = Bukkit.getPlayer(memberID);
                           if (player.isOnline()) {
                               Pmember.sendMessage(" ");
                               Pmember.sendMessage("§a파티장이 파티 PVP를 비활성화 했습니다.");
                               Pmember.sendMessage(" ");

                           }
                       }
                   }
                }
                else if (args.length == 2 && args[0].equalsIgnoreCase("양도")) {
                    UUID senderUUID = player.getUniqueId();
                    String partyName = partyManager.getPlayerParty(senderUUID);
                    if (partyName == null) {
                        player.sendMessage(ChatColor.RED + "당신은 어떤 파티에도 속해있지 않습니다.");
                        return true;
                    }
                    Party party = partyManager.partyMap.get(partyName);
                    if (party == null || !party.getLeader().equals(senderUUID)) {
                        player.sendMessage(ChatColor.RED + "당신은 이 파티의 리더가 아닙니다.");
                        return true;
                    }
                    String newLeaderName = args[1];
                    Player newLeaderPlayer = Bukkit.getPlayer(newLeaderName);
                    if (newLeaderPlayer == null) {
                        player.sendMessage(ChatColor.RED + "플레이어 '" + newLeaderName + "'을(를) 찾을 수 없습니다.");
                        return true;
                    }

                    UUID newLeaderUUID = newLeaderPlayer.getUniqueId();
                    if (!party.isMember(newLeaderUUID)) {
                        player.sendMessage(ChatColor.RED + "'" + newLeaderName + "'은(는) 파티 멤버가 아닙니다.");
                        return true;
                    }
                    if (partyManager.changeLeader(senderUUID, newLeaderUUID)) {
                        player.sendMessage(ChatColor.GREEN + "리더가 '" + newLeaderName + "'(으)로 변경되었습니다.");
                        newLeaderPlayer.sendMessage(ChatColor.GREEN + "당신이 이제 '" + partyName + "' 파티의 새로운 리더입니다!");
                    } else {
                        player.sendMessage(ChatColor.RED + "리더 변경에 실패했습니다.");
                    }

                    return true;
                }
                else if (args.length == 2 && args[0].equalsIgnoreCase("추방")) {
                    UUID senderUUID = player.getUniqueId();
                    String partyName = partyManager.getPlayerParty(senderUUID);
                    if (partyName == null) {
                        player.sendMessage(ChatColor.RED + "당신은 어떤 파티에도 속해있지 않습니다.");
                        return true;
                    }
                    Party party = partyManager.partyMap.get(partyName);
                    if (party == null || !party.getLeader().equals(senderUUID)) {
                        player.sendMessage(ChatColor.RED + "당신은 이 파티의 리더가 아닙니다.");
                        return true;
                    }
                    String Target = args[1];
                    Player Targetplayer = Bukkit.getPlayer(Target);
                    if (Targetplayer == null) {
                        player.sendMessage(ChatColor.RED + "플레이어 '" + Target + "'을(를) 찾을 수 없습니다.");
                        return true;
                    }

                    UUID newLeaderUUID = Targetplayer.getUniqueId();
                    if (!party.isMember(newLeaderUUID)) {
                        player.sendMessage(ChatColor.RED + "'" + Target + "'은(는) 파티 멤버가 아닙니다.");
                        return true;
                    }
                    if (partyManager.removeMember(partyName, Targetplayer.getUniqueId())) {
                        List<UUID> members = partyManager.getPartyMembers(partyName);
                        if (members == null || members.isEmpty()){
                            return true;
                        }
                        for (UUID memberID : members) {
                            Player Pmember = Bukkit.getPlayer(memberID);
                            if (player.isOnline()) {
                                Pmember.sendMessage(ChatColor.RED + "'" + Target + "'이(가) 추방되었습니다.");
                            }
                        }
                    }
                    player.sendMessage(ChatColor.RED + "파티에서 탈퇴하셨습니다.");
                    return true;
                }

                else if (args.length == 1 && args[0].equalsIgnoreCase("탈퇴")) {
                    UUID senderUUID = player.getUniqueId();
                    String partyName = partyManager.getPlayerParty(senderUUID);
                    if (partyName == null) {
                        player.sendMessage(ChatColor.RED + "당신은 어떤 파티에도 속해있지 않습니다.");
                        return true;
                    }
                    if (partyManager.removeMember(partyName, player.getUniqueId())) {
                        List<UUID> members = partyManager.getPartyMembers(partyName);
                        if (members == null || members.isEmpty()){
                            return true;
                        }
                        for (UUID memberID : members) {
                            Player Pmember = Bukkit.getPlayer(memberID);
                            if (player.isOnline()) {
                                Pmember.sendMessage(ChatColor.RED + "'" + player.getName() + "'이(가) 파티를 탈퇴했습니다.");
                            }
                        }
                    }
                }
                else {
                    player.sendMessage("§7===========§b§l파티 §a§l시스템§r§7===========");
                    player.sendMessage("§r§7/파티 생성 [이름] §r§7- §b파티§r§7를 생성합니다.");
                    player.sendMessage("§r§7/파티 탈퇴 §r§7- §b파티§7에서 탈퇴합니다.");
                    player.sendMessage("§r§7/파티 초대 [플레이어] §r§7- 자신의 §b파티§r§7에 플레이어를 초대합니다.");
                    player.sendMessage("§r§7/파티 [수락|거절] §r§7- §b파티 §r§7초대를 §a수락§r§7혹은 §c거절§r§7합니다.");
                    player.sendMessage("§r§7/파티 채팅 §r§7- §b파티§r§7채팅모드로 전환합니다.(§c파티장§7)");
                    player.sendMessage("§r§7/파티 전투 §r§7- §b파티§r§7PVP모드로 전환합니다.(§c파티장§7)");
                    player.sendMessage("§r§7/파티 양도 [플레이어] §r§7- §b파티장§7권한을 다른플레이어에게 양도합니다.(§c파티장§7)");
                    player.sendMessage("§r§7/파티 추방 [플레이어] §r§7- §b파티§7에서 플레이어를 추방합니다.(§c파티장§7)");
                    player.sendMessage("============================================");
                }
            }
        }
        return false;
    }
}
