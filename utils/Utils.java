package be.nateoncaprisun.mtgangs.utils;

import be.nateoncaprisun.mtgangs.nbteditor.NBTEditor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
;
import java.util.Collection;

public class Utils {

    private static ItemBuilder ib;

    public static String color(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public static Boolean isGunshellWeapon(ItemStack weapon){
        Collection<String> key = NBTEditor.getKeys(weapon);
        if (key.contains("mtcustom")){
            return true;
        }
        return false;
    }

    public static ItemStack genSkull(String player, String naam, String lore){
        ib = new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3);
        ib.setColoredName(naam);
        ib.setLore(Utils.color(lore));

        ItemStack finalItem = ib.toItemStack();

        SkullMeta meta = (SkullMeta) finalItem.getItemMeta();
        meta.setOwner(player);
        finalItem.setItemMeta(meta);

        return finalItem;
    }

    public static boolean isInt(String s, Player sender) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

}
