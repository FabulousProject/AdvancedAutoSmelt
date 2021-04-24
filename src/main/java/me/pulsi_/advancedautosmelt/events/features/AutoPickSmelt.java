package me.pulsi_.advancedautosmelt.events.features;

import me.pulsi_.advancedautosmelt.commands.Commands;
import me.pulsi_.advancedautosmelt.managers.DataManager;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Set;

public class AutoPickSmelt implements Listener {

    private boolean isEFS;
    private final boolean isAutoSmeltDCM;
    private final boolean isAutoPickupEnabled;
    private final boolean useLegacySupp;
    private final boolean isInvFullDrop;
    private final List<String> worldsBlackList;
    private final boolean isAutoPickupBlacklist;
    private final List<String> blackList;

    public AutoPickSmelt(DataManager dm) {
        isEFS = dm.isEFS();
        isAutoSmeltDCM = dm.isDCM();
        isAutoPickupEnabled = dm.isAutoPickupEnabled();
        worldsBlackList = dm.getWorldsBlackList();
        useLegacySupp = dm.isUseLegacySupp();
        isAutoPickupBlacklist = dm.isAutoPickupBlacklist();
        blackList = dm.getBlackList();
        isEFS = dm.isEFS();
        isInvFullDrop = dm.isDropsItemsInvFull();
    }

    private final Set<String> autoPickupOFF = Commands.autoPickupOFF;

    public void dropsItems(Player p, ItemStack i) {
        if (!p.getInventory().addItem(i).isEmpty()) {
            if (isInvFullDrop) {
                p.getWorld().dropItem(p.getLocation(), i);
            }
        }
    }

    public void removeDrops(BlockBreakEvent e) {
        if (useLegacySupp) {
            e.getBlock().setType(Material.AIR);
        } else {
            e.setDropItems(false);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void autoPickup(BlockBreakEvent e) {
        Player p = e.getPlayer();

        if (e.isCancelled()) return;
        if (!p.hasPermission("advancedautosmelt.autopickup")) return;
        for (String disabledWorlds : worldsBlackList)
            if (disabledWorlds.equalsIgnoreCase(p.getWorld().getName())) return;

        if (isEFS) return;
        if (isAutoSmeltDCM) {if (p.getGameMode().equals(GameMode.CREATIVE)) return;}
        if (autoPickupOFF.contains(p.getName())) return;
        if (!isAutoPickupEnabled) return;
        if (e.getBlock().getType().toString().contains("ORE") ||
                e.getBlock().getType() ==(Material.STONE) ||
                e.getBlock().getType() == (Material.CHEST) ||
                e.getBlock().getType() == (Material.FURNACE) ||
                e.getBlock().getType() == (Material.ENDER_CHEST)) return;
        if (isAutoPickupBlacklist) {
            if (blackList.contains(e.getBlock().getType().toString())) return;

            /*for (ItemStack drops : e.getBlock().getDrops()) {
                dropsItems(p, drops);
                removeDrops(e);
            }*/
        } else {
            for (ItemStack drops : e.getBlock().getDrops()) {
                dropsItems(p, drops);
                removeDrops(e);
            }
        }
    }

}