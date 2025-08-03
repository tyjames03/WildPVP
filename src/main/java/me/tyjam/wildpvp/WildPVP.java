package me.tyjam.wildpvp;

import org.bukkit.plugin.java.JavaPlugin;

public class WildPVP extends JavaPlugin {
    private static WildPVP instance;

    @Override
    public void onEnable() {
        instance = this;
        this.getCommand("wildpvp").setExecutor(new WildPVPCommand());
        getServer().getPluginManager().registerEvents(new WildPVPListener(), this);
    }

    public static WildPVP getInstance() {
        return instance;
    }
}

