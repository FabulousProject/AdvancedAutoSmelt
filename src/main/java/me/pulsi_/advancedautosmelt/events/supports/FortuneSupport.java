package me.pulsi_.advancedautosmelt.events.supports;

import me.pulsi_.advancedautosmelt.commands.Commands;
import me.pulsi_.advancedautosmelt.managers.DataManager;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class FortuneSupport implements Listener {

    private final boolean isEFS;
    private final boolean useWhitelist;
    private final boolean isDCM;
    private final boolean isAutoPickupEnabled;
    private final boolean useLegacySupp;
    private final boolean isInvFullDrop;
    private final List<String> blackList;
    private final boolean isBlackListEnabled;
    private final List<String> whitelist;
    private final List<String> disabledWorlds;

    private final List<Material> bypassMats = new ArrayList<Material>() {{
       add(Material.CHEST);
       add(Material.TRAPPED_CHEST);
       add(Material.ENDER_CHEST);
       add(Material.SHULKER_BOX);
    }};

    public FortuneSupport(DataManager dm) {
        isEFS = dm.isEFS();
        useWhitelist = dm.useWhitelist();
        isDCM = dm.isDCM();
        isAutoPickupEnabled = dm.isAutoPickupEnabled();
        whitelist = dm.getWhiteList();
        disabledWorlds = dm.getWorldsBlackList();
        useLegacySupp = dm.isUseLegacySupp();
        isInvFullDrop = dm.isDropsItemsInvFull();
        blackList = dm.getBlackList();
        isBlackListEnabled = dm.isAutoPickupBlacklist();
    }

    private final Set<String> autoPickupOFF = Commands.autoPickupOFF;

    public void dropsItems(Player p, ItemStack i, Location location) {
        if (!p.getInventory().addItem(i).isEmpty()) {
            if (isInvFullDrop) {
                p.getWorld().dropItem(location.clone(), i);
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

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreakFortune(BlockBreakEvent e) {
        if (e.isCancelled()) return;
        if (!isEFS) return;

        Player p = e.getPlayer();
        String type = e.getBlock().getType().toString();

        System.out.println("blist " + blackList);

        if (!(p.hasPermission("advancedautosmelt.fortune"))) return;
        if (isDCM && p.getGameMode().equals(GameMode.CREATIVE)) return;
        if (disabledWorlds.contains(p.getWorld().getName())) return;
        if (isAutoPickupEnabled && blackList.contains(type)) return;

        if (type.contains("FURNACE") || type.contains("SHULKER") || type.contains("CHEST") || type.contains("HOPPER")) return;
        if (type.equals("IRON_ORE") || type.equals("GOLD_ORE")) {
            checkFNuggetSystem(e);
            return;
        }

        if (useWhitelist) {
            if (whitelist.contains(e.getBlock().getType().toString())) {
                if (p.getInventory().getItemInHand().containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS)) {
                    int fortuneLevel = p.getInventory().getItemInHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);
                    check(p, e, new Random().nextInt((fortuneLevel - 1) + 1) + 1);
                } else {
                    check(p, e, -1);
                }
            } else {
                check(p, e, -1);
            }

        } else {
            if (p.getInventory().getItemInHand().containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS)) {
                int fortuneLevel = p.getInventory().getItemInHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);
                check(p, e, new Random().nextInt((fortuneLevel - 1) + 1) + 1);
            } else {
                check(p, e, -1);
            }
        }
        removeDrops(e);
    }

    private void checkFNuggetSystem(BlockBreakEvent event) {
        // TODO: 25.04.2021
    }

    private void check(Player player, BlockBreakEvent event, int amount) {
        for (ItemStack drop : event.getBlock().getDrops()) {
            drop.setAmount(amount == -1 ? drop.getAmount() : amount);

            if (isAutoPickupEnabled) {
                if (!autoPickupOFF.contains(player.getName())) {
                    dropsItems(player, drop, event.getBlock().getLocation());
                } else {
                    player.getWorld().dropItem(event.getBlock().getLocation().clone(), drop);
                }
            } else {
                player.getWorld().dropItem(event.getBlock().getLocation().clone(), drop);
            }
        }
    }

}