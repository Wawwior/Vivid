package me.wawwior.vivid.event;

import me.wawwior.vivid.Vivid;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class VividEventHandler implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        EntityDamageEvent deathEvent = event.getPlayer().getLastDamageCause();
        EntityDamageEvent.DamageCause deathCause = deathEvent.getCause();
        switch (deathCause) {
            case THORNS:
            case PROJECTILE:
            case ENTITY_SWEEP_ATTACK:
            case ENTITY_ATTACK: {
                if (((EntityDamageByEntityEvent) deathEvent).getDamager() instanceof Player) {
                    Vivid.VIVID.getHealthManager().modify((Player) ((EntityDamageByEntityEvent) deathEvent).getDamager(), Vivid.VIVID.getYamlConfig().getInt("amount.players"));
                    Vivid.VIVID.getHealthManager().modify(event.getPlayer(), Vivid.VIVID.getYamlConfig().getInt("amount.players") * -1);
                }
                break;
            }
            default: {
                Vivid.VIVID.getHealthManager().modify(event.getPlayer(), Vivid.VIVID.getYamlConfig().getInt("amount.environment") * -1);
            }
        }

    }

    @EventHandler
    public void onPlayerUseItem(PlayerInteractEvent event) {
        if (event.getHand().equals(EquipmentSlot.HAND)) {
            if (event.getPlayer().getInventory().getItem(EquipmentSlot.HAND).equals(Vivid.VIVID.ITEM)) {
                Vivid.VIVID.getHealthManager().modify(event.getPlayer(), 1);
                event.getPlayer().getInventory().setItemInMainHand(new ItemStack(Material.AIR));
                event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(), Sound.ITEM_TOTEM_USE, 1f, 1f);
                event.getPlayer().getWorld().spawnParticle(Particle.HEART, event.getPlayer().getLocation(), 20, 0.5, 1, 0.5);
            }
        }
        if (event.getHand().equals(EquipmentSlot.OFF_HAND)) {
            if (event.getPlayer().getInventory().getItem(EquipmentSlot.OFF_HAND).equals(Vivid.VIVID.ITEM)) {
                Vivid.VIVID.getHealthManager().modify(event.getPlayer(), 1);
                event.getPlayer().getInventory().setItemInOffHand(new ItemStack(Material.AIR));
                event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(), Sound.ITEM_TOTEM_USE, 1f, 1f);
                event.getPlayer().getWorld().spawnParticle(Particle.HEART, event.getPlayer().getLocation(), 20, 0.5, 1, 0.5);
            }
        }
    }

    @EventHandler
    public void onTotem(EntityResurrectEvent event) {
        EntityEquipment equipment = event.getEntity().getEquipment();
        boolean main = equipment.getItem(EquipmentSlot.HAND).equals(Vivid.VIVID.ITEM);
        boolean off = equipment.getItem(EquipmentSlot.OFF_HAND).equals(Vivid.VIVID.ITEM);
        boolean cancel = (main || off) && !((equipment.getItem(EquipmentSlot.HAND).getType().equals(Material.TOTEM_OF_UNDYING) && !main) || (equipment.getItem(EquipmentSlot.OFF_HAND).getType().equals(Material.TOTEM_OF_UNDYING) && !off));
        cancel = cancel || event.isCancelled();
        event.setCancelled(cancel);
        if (!cancel && main) {
            Bukkit.getScheduler().runTask(Vivid.VIVID, () -> {
                equipment.setItemInMainHand(Vivid.VIVID.ITEM, true);
                equipment.setItemInOffHand(new ItemStack(Material.AIR), true);
            });
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Vivid.VIVID.getHealthManager().update(event.getPlayer());
    }
}
