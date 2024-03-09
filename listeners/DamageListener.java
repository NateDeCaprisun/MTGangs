package be.nateoncaprisun.mtgangs.listeners;

import be.nateoncaprisun.mtgangs.Main;
import be.nateoncaprisun.mtgangs.utils.GangUtils;
import be.nateoncaprisun.mtgangs.utils.Utils;
import com.jazzkuh.gunshell.api.events.FireableDamageEvent;
import com.jazzkuh.gunshell.api.events.FireableFireEvent;
import com.jazzkuh.gunshell.api.objects.GunshellFireable;
import com.jazzkuh.gunshell.api.objects.GunshellRayTraceResult;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.List;
import java.util.Optional;

public class DamageListener implements Listener {

    @EventHandler
    public void damageEvent(EntityDamageByEntityEvent event){
        if (!(event.getDamager() instanceof Player)) return;
        if (!(event.getEntity() instanceof Player)) return;
        ConfigurationSection gangSection = Main.getInstance().getGangs().getConfig().getConfigurationSection("gangs");
        if(gangSection == null) return;
        Player damager = (Player) event.getDamager();
        Player player = (Player) event.getEntity();
        String gangDamager = GangUtils.getGang(damager);
        String gangPlayer = GangUtils.getGang(player);
        if (gangPlayer.equals("niets") || gangDamager.equals("niets")) return;
        if (gangDamager.equals(gangPlayer)){
            event.setCancelled(true);
            damager.sendMessage(Utils.color("&cJe kan geen speler van je gang hitten!"));
        }
    }

    /*
    @EventHandler(priority = EventPriority.LOWEST)
    public void gunshellHitEvent(FireableDamageEvent event) {
        Player player = event.getPlayer();

        if (event.getRayTraceResult() == null) return;

        List<GunshellRayTraceResult> results = (List<GunshellRayTraceResult>) event.getRayTraceResult();

        for (GunshellRayTraceResult result : results) {
            Optional<LivingEntity> optional = result.getOptionalLivingEntity();
            if (!optional.isPresent()) continue;
            LivingEntity entity = optional.get();

            if (!(entity instanceof Player)) continue;
            Player victim = (Player) entity;

            ConfigurationSection gangSection = Main.getInstance().getGangs().getConfig().getConfigurationSection("gangs");
            if(gangSection == null) return;

            String gangDamager = GangUtils.getGang(victim);
            String gangPlayer = GangUtils.getGang(player);
            if (gangPlayer.equals("niets") || gangDamager.equals("niets")) return;

            if (GangUtils.getGang(victim).equals(GangUtils.getGang(player))) {
                event.setCancelled(true);
                player.sendMessage(Utils.color("&cJe kan geen speler van je gang hitten!"));
                return;
            }
        }
    }
     */



    @EventHandler
    public void gunshellDamageEvent(FireableDamageEvent event) {
        Player player = event.getPlayer();
        Optional<LivingEntity> optional = event.getRayTraceResult().getOptionalLivingEntity();
        if (optional.isPresent()) {
            LivingEntity entity = optional.get();
            if (entity instanceof Player) {
                Player victim = (Player)entity;
                if (GangUtils.getGang(victim).equals("niets") || GangUtils.getGang(player).equals("niets")) return;
                if (GangUtils.getGang(victim).equals(GangUtils.getGang(player))) {
                    event.setCancelled(true);
                    player.sendMessage(Utils.color("&cJe kan geen speler van je gang hitten!"));
                }
            }
        }
    }

}
