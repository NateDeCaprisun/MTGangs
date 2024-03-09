package be.nateoncaprisun.mtgangs.utils;

import be.nateoncaprisun.mtgangs.Main;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class GangUtils {

    public static boolean isGang(String gang){
        if (Main.getInstance().getGangs().getConfig().getConfigurationSection("gangs") == null) return false;
        if (Main.getInstance().getGangs().getConfig().getConfigurationSection("gangs").contains(gang)){
            return true;
        }
        return false;
    }

    public static String getGang(Player player){
        String playerUUID = player.getUniqueId().toString();
        ConfigurationSection gangsSection = Main.getInstance().getGangs().getConfig().getConfigurationSection("gangs");
        for (String gangName : gangsSection.getKeys(false)) {
            if (Main.getInstance().getGangs().getConfig().getString("gangs." + gangName + ".leider").equals(playerUUID) ||
                    Main.getInstance().getGangs().getConfig().getStringList("gangs." + gangName + ".members").contains(playerUUID)){
                return gangName;
            }
        }
        return "niets";
    }

    public static boolean checkGang(Player player) {
        String playerUUID = player.getUniqueId().toString();
        ConfigurationSection gangsSection = Main.getInstance().getGangs().getConfig().getConfigurationSection("gangs");
        if (gangsSection == null){
            return false;
        }
        for (String gangName : gangsSection.getKeys(false)) {
            if (Main.getInstance().getGangs().getConfig().getString("gangs." + gangName + ".leider").equals(playerUUID) ||
                    Main.getInstance().getGangs().getConfig().getStringList("gangs." + gangName + ".members").contains(playerUUID)){
                return true;
            }
        }

        return false; // Speler zit in geen enkele gang
    }

    public static boolean checkLeider(Player player){
        String playerUUID = player.getUniqueId().toString();
        ConfigurationSection gangsSection = Main.getInstance().getGangs().getConfig().getConfigurationSection("gangs");
        if (gangsSection == null){
            return false;
        }
        for (String gangName : gangsSection.getKeys(false)) {
            if (Main.getInstance().getGangs().getConfig().getString("gangs." + gangName + ".leider").equals(playerUUID)){
                return true;
            }
        }
        return false;
    }

    public static FileConfiguration gangCreate(String gangNaam, UUID leider, Player staff){
        FileConfiguration gangFile = Main.getInstance().getGangs().getConfig();
        ConfigurationSection gangSection = gangFile.getConfigurationSection("gangs");
        if (gangSection == null){
            gangFile.set("gangs", gangNaam);
            gangFile.set("gangs." + gangNaam + ".leider", leider.toString());
            Main.getInstance().getGangs().saveConfig();
            Player gangLeider = Bukkit.getPlayer(leider);
            gangLeider.sendMessage(Utils.color("&a" + gangNaam + " is aangemaakt met jou als leider!"));
            staff.sendMessage(Utils.color("&aJe hebt de gang " + gangNaam + " aangemaakt! Leider: " + gangLeider.getName()));
            return gangFile;
        }
        gangSection.addDefault("gangs", gangNaam);
        gangFile.set("gangs." + gangNaam + ".leider", leider.toString());
        Main.getInstance().getGangs().saveConfig();
        Player gangLeider = Bukkit.getPlayer(leider);
        gangLeider.sendMessage(Utils.color("&a" + gangNaam + " is aangemaakt met jou als leider!"));
        staff.sendMessage(Utils.color("&aJe hebt de gang " + gangNaam + " aangemaakt! Leider: " + gangLeider.getName()));
        return gangFile;
    }
    public static FileConfiguration gangDelete(String gangNaam, Player player){
        FileConfiguration gangFile = Main.getInstance().getGangs().getConfig();
        ConfigurationSection gangSection = Main.getInstance().getGangs().getConfig().getConfigurationSection("gangs");
        gangSection.set(gangNaam, null);
        Main.getInstance().getGangs().saveConfig();
        player.sendMessage(Utils.color("&cJe hebt de gang &4" + gangNaam + " &cverwijderd!"));
        return gangFile;
    }
    public static FileConfiguration addGangMember(UUID member, String gangNaam){
        FileConfiguration gangFile = Main.getInstance().getGangs().getConfig();
        ArrayList<String> gangList = (ArrayList<String>) gangFile.getStringList("gangs." + gangNaam + ".members");
        if (gangList == null) gangList = new ArrayList<>();
        gangList.add(member.toString());
        gangFile.set("gangs." + gangNaam + ".members", gangList);
        Main.getInstance().getGangs().saveConfig();
        return gangFile;
    }
    public static FileConfiguration removeGangMember(UUID member, String gangNaam){
        FileConfiguration gangFile = Main.getInstance().getGangs().getConfig();
        ArrayList<String> gangList = (ArrayList<String>) gangFile.getStringList("gangs." + gangNaam + ".members");
        gangList.remove(member.toString());
        gangFile.set("gangs." + gangNaam + ".members", gangList);
        Main.getInstance().getGangs().saveConfig();
        return gangFile;
    }
}
