package org.wayggstar.party;

import org.bukkit.plugin.java.JavaPlugin;

public final class Party extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("§a파티시스템 활성화");
        getCommand("파티");
    }

    @Override
    public void onDisable() {
        getLogger().info("§c파티시스템 활성화");
    }
}
