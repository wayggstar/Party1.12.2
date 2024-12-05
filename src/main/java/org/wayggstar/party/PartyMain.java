package org.wayggstar.party;

import org.bukkit.plugin.java.JavaPlugin;
import org.wayggstar.party.Party.*;

public final class PartyMain extends JavaPlugin {

    private Command command;
    private Party party;
    private PartyManager partyManager;
    private PChat pChat;
    private PPVP ppvp;

    @Override
    public void onEnable() {
        partyManager = new PartyManager();
        command = new Command(partyManager, party, this);
        pChat = new PChat(partyManager);
        ppvp = new PPVP(partyManager);

        getLogger().info("§a파티시스템 활성화(by.wayggstar)");

        getServer().getPluginManager().registerEvents(pChat, this);
        getServer().getPluginManager().registerEvents(ppvp, this);
        getCommand("파티").setExecutor(command);
    }

    @Override
    public void onDisable() {
        getLogger().info("§c파티시스템 비활성화(by.wayggstar)");
    }
}
