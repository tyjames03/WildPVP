package me.tyjam.wildpvp;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class WildPVPListener implements Listener {
    private static final Set<UUID> pvpDisabled = new HashSet<>();
    private static final Map<UUID, UUID> fighting = new HashMap<>();

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getView().getTitle().equals("WildPVP Queue")) {
            e.setCancelled(true);
            Player clicker = (Player) e.getWhoClicked();
            int slot = e.getRawSlot();
            if (slot < 0 || slot > 8) return;
            ItemStack clicked = e.getCurrentItem();
            if (clicked == null) return;

            if (slot == 8) {
                // Join/Leave button
                if (WildPVPQueue.isQueued(clicker.getUniqueId())) {
                    WildPVPQueue.remove(clicker.getUniqueId());
                } else {
                    WildPVPQueue.add(clicker.getUniqueId());
                }
                WildPVPQueueGUI.open(clicker);
                return;
            }

            // Player heads
            if (clicked.getType() == Material.PLAYER_HEAD) {
                SkullMeta meta = (SkullMeta) clicked.getItemMeta();
                OfflinePlayer target = meta.getOwningPlayer();
                if (target == null) return;
                if (target.getUniqueId().equals(clicker.getUniqueId())) {
                    // Remove self from queue
                    WildPVPQueue.remove(clicker.getUniqueId());
                    WildPVPQueueGUI.open(clicker);
                } else {
                    // Start fight
                    Player targetPlayer = Bukkit.getPlayer(target.getUniqueId());
                    if (targetPlayer == null) return;
                    if (!WildPVPQueue.isQueued(clicker.getUniqueId()) || !WildPVPQueue.isQueued(targetPlayer.getUniqueId())) return;
                    WildPVPQueue.remove(clicker.getUniqueId());
                    WildPVPQueue.remove(targetPlayer.getUniqueId());
                    startFight(clicker, targetPlayer);
                    clicker.closeInventory();
                    targetPlayer.closeInventory();
                }
            }
        }
    }

    private void startFight(Player p1, Player p2) {
        Location loc1 = getRandomLocation(p1.getWorld());
        Location loc2 = getRandomLocation(p2.getWorld());
        p1.teleport(loc1);
        p2.teleport(loc2);
        pvpDisabled.add(p1.getUniqueId());
        pvpDisabled.add(p2.getUniqueId());
        fighting.put(p1.getUniqueId(), p2.getUniqueId());
        fighting.put(p2.getUniqueId(), p1.getUniqueId());

        p1.sendMessage("§eYou have been teleported for a WildPVP fight! PVP will enable in 5 seconds.");
        p2.sendMessage("§eYou have been teleported for a WildPVP fight! PVP will enable in 5 seconds.");

        new BukkitRunnable() {
            int timer = 5;
            @Override
            public void run() {
                if (timer > 0) {
                    p1.sendTitle("§cPVP in " + timer, "", 0, 20, 0);
                    p2.sendTitle("§cPVP in " + timer, "", 0, 20, 0);
                    timer--;
                } else {
                    pvpDisabled.remove(p1.getUniqueId());
                    pvpDisabled.remove(p2.getUniqueId());
                    p1.sendTitle("§aFIGHT!", "", 0, 40, 10);
                    p2.sendTitle("§aFIGHT!", "", 0, 40, 10);
                    this.cancel();
                }
            }
        }.runTaskTimer(WildPVP.getInstance(), 0, 20);
    }

    private Location getRandomLocation(World world) {
        int radius = 1000;
        int x = (int) (Math.random() * radius * 2) - radius;
        int z = (int) (Math.random() * radius * 2) - radius;
        int y = world.getHighestBlockYAt(x, z) + 1;
        return new Location(world, x + 0.5, y, z + 0.5);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player) || !(e.getDamager() instanceof Player)) return;
        Player p1 = (Player) e.getEntity();
        Player p2 = (Player) e.getDamager();
        if (pvpDisabled.contains(p1.getUniqueId()) || pvpDisabled.contains(p2.getUniqueId())) {
            e.setCancelled(true);
        }
        // Only allow fighting between paired players
        if (fighting.containsKey(p1.getUniqueId())) {
            if (!fighting.get(p1.getUniqueId()).equals(p2.getUniqueId())) {
                e.setCancelled(true);
            }
        }
    }
}

