package org.wayggstar.party;

import org.bukkit.plugin.java.JavaPlugin;
import org.wayggstar.party.Party.Command;
import org.wayggstar.party.Party.PChat;
import org.wayggstar.party.Party.Party;
import org.wayggstar.party.Party.PartyManager;

public final class PartyMain extends JavaPlugin {

    private Command command;
    private Party party;
    private PartyManager partyManager;
    private PChat pChat;

    @Override
    public void onEnable() {
        partyManager = new PartyManager();
        command = new Command(partyManager, party, this);
        pChat = new PChat(partyManager);

        getLogger().info("§a파티시스템 활성화(by.wayggstar)");

        getServer().getPluginManager().registerEvents(pChat, this);
        getCommand("파티").setExecutor(command);
    }

    @Override
    public void onDisable() {
        getLogger().info("§c파티시스템 비활성화(by.wayggstar)");
    }
}
