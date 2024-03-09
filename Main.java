package be.nateoncaprisun.mtgangs;

import be.nateoncaprisun.mtgangs.commands.GangCommand;
import be.nateoncaprisun.mtgangs.listeners.DamageListener;
import be.nateoncaprisun.mtgangs.utils.ConfigurationFile;
import nl.minetopiasdb.api.API;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import nl.minetopiasdb.api.playerdata.objects.SDBPlayer;

import java.util.ArrayList;
import java.util.HashMap;

public final class Main extends JavaPlugin {

    private ConfigurationFile gangs;
    private SDBPlayer sdbPlayer;
    private static Main instance;
    private HashMap<String, Player> memberRequest;

    @Override
    public void onEnable() {
        gangs = new ConfigurationFile(this, "gangs.yml", true);
        gangs.saveConfig();
        instance = this;
        getCommand("gang").setExecutor(new GangCommand());
        memberRequest = new HashMap<>();
        Bukkit.getPluginManager().registerEvents(new DamageListener(), this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public ConfigurationFile getGangs(){
        return gangs;
    }

    public static Main getInstance(){
        return instance;
    }

    public SDBPlayer getSdbPlayer() {
        return sdbPlayer;
    }
    public HashMap<String, Player> getMemberRequest(){
        return memberRequest;
    }
}
