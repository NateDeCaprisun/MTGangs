package be.nateoncaprisun.mtgangs.commands;

import be.nateoncaprisun.mtgangs.Main;
import be.nateoncaprisun.mtgangs.utils.GangUtils;
import be.nateoncaprisun.mtgangs.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.bukkit.Bukkit.dispatchCommand;
import static org.bukkit.Bukkit.getLogger;

public class GangCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)){
            sender.sendMessage("Console kan dit commando niet uitvoeren!");
            return false;
        }
        ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
        Player player = (Player) sender;
        if (args.length == 0){
            if (player.hasPermission("gangs.admin")){
                sendStaffHelp(player);
                return true;
            }
            sendHelp(player);
            return true;
        }
        if (args[0].equalsIgnoreCase("help")){
            if (player.hasPermission("gangs.admin")){
                sendStaffHelp(player);
                return true;
            }
            sendHelp(player);
            return true;
        }
        if (args[0].equalsIgnoreCase("create")){
            if (!player.hasPermission("gangs.admin")){
                player.sendMessage(Utils.color("&cJe hebt de permissie gangs.admin nodig om dit commando uit te voeren!"));
                return false;
            }
            if (args.length == 1){
                sendStaffHelp(player);
                return false;
            }
            if (args.length == 2){
                sendStaffHelp(player);
                return false;
            }
            String gangNaam = args[1];
            Player gangLeader = Bukkit.getPlayer(args[2]);
            if (gangLeader == null){
                player.sendMessage(Utils.color("&cGeef een geldige gang leader op!"));
                return false;
            }
            if (Main.getInstance().getGangs().getConfig().getConfigurationSection("gangs") != null &&
                    Main.getInstance().getGangs().getConfig().getConfigurationSection("gangs").contains(gangNaam)){
                player.sendMessage(Utils.color("&cEr bestaat al een gang met de naam " + gangNaam));
                return true;
            }
            if (GangUtils.checkGang(gangLeader)){
                player.sendMessage(Utils.color("&c" + gangLeader.getName() + " zit al in een gang!"));
                return false;
            }
            GangUtils.gangCreate(gangNaam, gangLeader.getUniqueId(), player);
            Bukkit.dispatchCommand(console, "prefix add " + gangLeader.getName() + " Lead."+gangNaam);
            return true;
        }
        if (args[0].equalsIgnoreCase("delete")){
            if (!player.hasPermission("gangs.admin")){
                player.sendMessage(Utils.color("&cJe hebt de permissie gangs.admin nodig om dit commando uit te voeren!"));
                return false;
            }
            if (args.length == 1){
                sendStaffHelp(player);
                return false;
            }
            String gang = args[1];
            if (Main.getInstance().getGangs().getConfig().getConfigurationSection("gangs") == null ||
                    !Main.getInstance().getGangs().getConfig().getConfigurationSection("gangs").contains(gang)){
                player.sendMessage(Utils.color("&cEr bestaat geen gang met deze naam!"));
                return true;
            }
            GangUtils.gangDelete(gang, player);
            return true;
        }
        if (args[0].equalsIgnoreCase("addmember")){
            if (args.length == 1){
                sendHelp(player);
                return true;
            }
            Player member = Bukkit.getPlayer(args[1]);
            if (member == null){
                player.sendMessage(Utils.color("&cGeef een geldige speler op!"));
                return true;
            }
            if (player == member){
                player.sendMessage(Utils.color("&cJe kan jezelf geen verzoek sturen!"));
                return true;
            }
            if (!GangUtils.checkLeider(player)){
                player.sendMessage(Utils.color("&cJe bent geen gang leider!"));
                return true;
            }
            if (GangUtils.checkGang(member)){
                player.sendMessage(Utils.color("&c" + member.getName()+ " zit al in een gang!"));
                return true;
            }
            Main.getInstance().getMemberRequest().put(GangUtils.getGang(player), member);
            player.sendMessage(Utils.color("&6Verzoek gestuurd naar &c" + member.getName()));
            member.sendMessage(Utils.color("&6Je hebt een gang verzoek gekregen van de gang: &c" + GangUtils.getGang(player)));
            TextComponent accept = new TextComponent("[ACCEPT]");
            accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/gang accept "+ GangUtils.getGang(player)));
            accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    new ComponentBuilder(Utils.color("&7Klik hier om het verzoek te &aaccepteren")).create()));
            accept.setColor(ChatColor.GREEN);
            TextComponent decline = new TextComponent("[DECLINE]");
            decline.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/gang decline " + GangUtils.getGang(player)));
            decline.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    new ComponentBuilder(Utils.color("&7Klik hier om het verzoek te &cweigeren")).create()));
            decline.setColor(ChatColor.RED);
            TextComponent of = new TextComponent(" of ");
            of.setColor(ChatColor.GRAY);
            member.spigot().sendMessage(accept,of, decline);
            new BukkitRunnable(){
                @Override
                public void run(){
                    if (Main.getInstance().getMemberRequest().containsKey(GangUtils.getGang(player))
                            && Main.getInstance().getMemberRequest().containsValue(member)){
                        Main.getInstance().getMemberRequest().remove(GangUtils.getGang(player), member);
                        member.sendMessage(Utils.color("&cverzoek verlopen"));
                        cancel();
                    }
                    cancel();
                }
            }.runTaskLater(Main.getInstance(), 20L*60);
        }
        if (args[0].equalsIgnoreCase("decline")){
            if (args.length == 1){
                sendHelp(player);
                return true;
            }
            String gang = args[1];
            if (!GangUtils.isGang(gang)){
                player.sendMessage(Utils.color("&cGeef een geldige gang op!"));
                return true;
            }
            if (!Main.getInstance().getMemberRequest().containsKey(gang) || !Main.getInstance().getMemberRequest().containsValue(player)){
                player.sendMessage(Utils.color("&cJe hebt geen gang invite gekregen van &4" + gang));
                return true;
            }
            Main.getInstance().getMemberRequest().remove(gang, player);
            player.sendMessage(Utils.color("&6Verzoek gedeclined!"));
            Player leader = Bukkit.getPlayer(UUID.fromString(Main.getInstance().getGangs().getConfig().getString("gangs." + gang + ".leider")));
            leader.sendMessage(Utils.color("&c"+player.getName()+ " &6heeft het verzoek gedeclined!"));
        }
        if (args[0].equalsIgnoreCase("accept")){
            if (args.length == 1){
                sendHelp(player);
                return true;
            }
            String gang = args[1];
            if (!GangUtils.isGang(gang)){
                player.sendMessage(Utils.color("&cGeef een geldige gang op!"));
                return true;
            }
            if (!Main.getInstance().getMemberRequest().containsKey(gang) || !Main.getInstance().getMemberRequest().containsValue(player)){
                player.sendMessage(Utils.color("&cJe hebt geen gang invite gekregen van &4" + gang));
                return true;
            }
            Main.getInstance().getMemberRequest().remove(gang, player);
            GangUtils.addGangMember(player.getUniqueId(), gang);
            player.sendMessage(Utils.color("&aJe bent de gang &2" + gang + " &agejoined!"));
            Player leader = Bukkit.getPlayer(UUID.fromString(Main.getInstance().getGangs().getConfig().getString("gangs." + gang + ".leider")));
            leader.sendMessage(Utils.color("&c"+player.getName()+ " &6is je gang gejoined!"));
            Bukkit.dispatchCommand(console, "prefix add " + player.getName() + " Member."+gang);
        }
        if (args[0].equalsIgnoreCase("leave")){
            if (GangUtils.getGang(player).equals("niets")){
                player.sendMessage(Utils.color("&cJe zit niet in een gang!"));
                return true;
            }
            player.sendMessage(Utils.color("&6Je bent de gang &c" + GangUtils.getGang(player) + " &6geleaved!"));
            Bukkit.dispatchCommand(console, "prefix remove " + player.getName() + " Member."+GangUtils.getGang(player));
            GangUtils.removeGangMember(player.getUniqueId(), GangUtils.getGang(player));
        }
        if (args[0].equalsIgnoreCase("removemember")){
            if (args.length == 1){
                sendHelp(player);
                return true;
            }
            Player member = Bukkit.getPlayer(args[1]);
            if (member == null){
                player.sendMessage(Utils.color("&cGeef een geldige speler op!"));
                return true;
            }
            if (player == member){
                player.sendMessage(Utils.color("&cJe kan jezelf geen verzoek sturen!"));
                return true;
            }
            if (!GangUtils.checkLeider(player)){
                player.sendMessage(Utils.color("&cJe bent geen gang leider!"));
                return true;
            }
            if (!GangUtils.getGang(member).equals(GangUtils.getGang(player))){
                player.sendMessage(Utils.color("&c"+member.getName()+" &6zit niet in je gang!"));
                return true;
            }
            player.sendMessage(Utils.color("&c" + member.getName() + " &6is gekickt van je gang!"));
            member.sendMessage(Utils.color("&6Je ben gekickt van de gang &c" + GangUtils.getGang(player)));
            Bukkit.dispatchCommand(console, "prefix remove " + member.getName() + " Member."+GangUtils.getGang(player));
            GangUtils.removeGangMember(member.getUniqueId(), GangUtils.getGang(player));
        }
        if (args[0].equalsIgnoreCase("info")){
            if (args.length == 1){
                sendHelp(player);
                return true;
            }
            String gang = args[1];
            if (!GangUtils.isGang(gang)){
                player.sendMessage(Utils.color("&cGeef een geldige gang op!"));
                return true;
            }
            Player leader = Bukkit.getPlayer(UUID.fromString(Main.getInstance().getGangs().getConfig().getString("gangs." + gang + ".leider")));
            ArrayList<String> memberList = (ArrayList<String>) Main.getInstance().getGangs().getConfig().getStringList("gangs." + gang + ".members");
            if (memberList == null){
                player.sendMessage(Utils.color("&6&m-------------------------------"));
                player.sendMessage("");
                player.sendMessage(Utils.color("&6Gang info: &c" + gang));
                player.sendMessage(Utils.color("&6Leider: &c" + leader.getName()));
                player.sendMessage(Utils.color("&6Members: &cNiemand"));
                player.sendMessage("");
                player.sendMessage(Utils.color("&6&m-------------------------------"));
                return true;
            }
            ArrayList<String> memberListT = new ArrayList<>();
            for (String uuid: memberList){
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
                String of = offlinePlayer.getName();
                memberListT.add(of);
            }
            String playerNames = String.join(", ", memberListT);
            player.sendMessage(Utils.color("&6&m-------------------------------"));
            player.sendMessage("");
            player.sendMessage(Utils.color("&6Gang info: &c" + gang));
            player.sendMessage(Utils.color("&6Leider: &c" + leader.getName()));
            player.sendMessage(Utils.color("&6Members: &c" + playerNames));
            player.sendMessage("");
            player.sendMessage(Utils.color("&6&m-------------------------------"));
        }
        if(args[0].equalsIgnoreCase("overdracht")){
            if (args.length == 1){
                sendHelp(player);
                return true;
            }
            Player overdrachtSpeler = Bukkit.getPlayer(args[1]);
            if (overdrachtSpeler == null){
                player.sendMessage(Utils.color("&cGeef een geldige speler op!"));
                return true;
            }
            String gangPlayer = GangUtils.getGang(overdrachtSpeler);
            String gangOverdrachter = GangUtils.getGang(player);
            if (!Main.getInstance().getGangs().getConfig().getString("gangs." + gangOverdrachter + ".leider").equals(player.getUniqueId().toString())){
                player.sendMessage(Utils.color("&cJe moet de leider van een gang zijn!"));
                return true;
            }
            if (gangPlayer != "niets"){
                player.sendMessage(Utils.color("&4"+overdrachtSpeler.getName()+" &czit nog in een gang!"));
                return true;
            }
            Bukkit.dispatchCommand(console, "prefix remove " + player.getName() + " Lead."+GangUtils.getGang(player));
            Bukkit.dispatchCommand(console, "prefix add " + overdrachtSpeler.getName() + " Lead."+GangUtils.getGang(player));
            player.sendMessage(Utils.color("&6Je hebt de gang &c" + gangOverdrachter + " &6overgedragen naar &c"+overdrachtSpeler.getName()));
            overdrachtSpeler.sendMessage(Utils.color("&6De gang &c" + gangOverdrachter + " &6is overgedragen naar jou!"));
            Main.getInstance().getGangs().getConfig().set("gangs." + gangOverdrachter+".leider", overdrachtSpeler.getUniqueId().toString());
            Main.getInstance().getGangs().saveConfig();

        }
        return false;
    }

    public void sendHelp(Player player){
        player.sendMessage(Utils.color("&6&m---------------------------------"));
        player.sendMessage(Utils.color("&e/gang help &7- &eDit help menu"));
        player.sendMessage(Utils.color("&e/gang info (gang) &7- &eKrijg de info van een gang!"));
        player.sendMessage(Utils.color("&e/gang addmember (speler) &7- &eVoeg een member toe aan je gang!"));
        player.sendMessage(Utils.color("&e/gang removemember (speler) &7- &eVerwijder een speler van je gang!"));
        player.sendMessage(Utils.color("&e/gang overdracht (speler) &7- &eDraag je gang over aan een andere speler!"));
        player.sendMessage(Utils.color("&e/gang accept (gang) &7- &eAccepteer een gang verzoek (joinen als member)!"));
        player.sendMessage(Utils.color("&e/gang leave &7- &eLeave de gang waar je in zit!"));
        player.sendMessage(Utils.color("&6&m---------------------------------"));
    }
    public void sendStaffHelp(Player player){
        player.sendMessage(Utils.color("&6&m---------------------------------"));
        player.sendMessage(Utils.color("&eVoor vragen dm &6eddiefreddie (NateOnCaprisun)"));
        player.sendMessage(Utils.color("&6&m---------------------------------"));
        player.sendMessage(Utils.color("&e/gang help &7- &eDit help menu"));
        player.sendMessage(Utils.color("&e/gang info (gang) &7- &eKrijg de info van een gang!"));
        player.sendMessage(Utils.color("&e/gang create (gang naam) (speler) &7- &eMaak een gang aan!"));
        player.sendMessage(Utils.color("&e/gang addmember (speler) &7- &eVoeg een member toe aan je gang!"));
        player.sendMessage(Utils.color("&e/gang removemember (speler) &7- &eVerwijder een speler van je gang!"));
        player.sendMessage(Utils.color("&e/gang overdracht (speler) &7- &eDraag je gang over aan een andere speler!"));
        player.sendMessage(Utils.color("&e/gang leave &7- &eLeave de gang waar je in zit!"));
        player.sendMessage(Utils.color("&e/gang accept (gang) &7- &eAccepteer een gang verzoek (joinen als member)!"));
        player.sendMessage(Utils.color("&e/gang delete (gang) &7- &eDelete de opgegeven gang!"));
        player.sendMessage(Utils.color("&6&m---------------------------------"));
    }
}
