package me.tyjam.wildpvp;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class WildPVPQueueGUI {
    public static void open(Player player) {
        Inventory gui = Bukkit.createInventory(null, 9, "WildPVP Queue");
        List<UUID> queue = WildPVPQueue.getQueue();
        int i = 0;
        for (UUID uuid : queue) {
            if (i >= 8) break;
            Player p = Bukkit.getPlayer(uuid);
            if (p == null) continue;
            ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
            SkullMeta meta = (SkullMeta) skull.getItemMeta();
            meta.setOwningPlayer(p);
            meta.setDisplayName(p.getName() + (p.equals(player) ? " (You)" : ""));
            skull.setItemMeta(meta);
            gui.setItem(i, skull);
            i++;
        }
        // Join/Leave button
        ItemStack joinItem = new ItemStack(Material.EMERALD_BLOCK);
        ItemMeta joinMeta = joinItem.getItemMeta();
        if (queue.contains(player.getUniqueId())) {
            joinMeta.setDisplayName("§cLeave Queue");
        } else {
            joinMeta.setDisplayName("§aJoin Queue");
        }
        joinItem.setItemMeta(joinMeta);
        gui.setItem(8, joinItem);

        player.openInventory(gui);
    }
}

