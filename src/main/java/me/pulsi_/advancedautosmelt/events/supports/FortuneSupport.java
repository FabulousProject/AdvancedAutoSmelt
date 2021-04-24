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
        Player p = e.getPlayer();
        System.out.println("blist " + blackList);

        if (disabledWorlds.contains(p.getWorld().getName())) return;
        if (isAutoPickupEnabled && blackList.contains(e.getBlock().getType().toString()))
            return;

        if (e.isCancelled()) return;
        if (!isEFS) return;
        if (e.getBlock().getType() == Material.CHEST || e.getBlock().getType() == Material.FURNACE || e.getBlock().getType() == Material.ENDER_CHEST) return;
        if (!(p.hasPermission("advancedautosmelt.fortune"))) return;
        if (isDCM) {if (p.getGameMode().equals(GameMode.CREATIVE)) return;}

        if (useWhitelist) {
            if (whitelist.contains(e.getBlock().getType().toString())) {

                if (p.getInventory().getItemInHand().containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS)) {
                    int fortuneLevel = p.getInventory().getItemInHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);

                    Random r = new Random();
                    int min = 1;
                    int multiply = r.nextInt((fortuneLevel - min) + 1) + min;
                    for (ItemStack drops : e.getBlock().getDrops()) {
                        drops.setAmount(multiply);

                        if (isAutoPickupEnabled) {
                            if (!autoPickupOFF.contains(p.getName())) {
                                dropsItems(p, drops, e.getBlock().getLocation());
                            } else {
                                p.getWorld().dropItem(e.getBlock().getLocation().clone(), drops);
                            }
                        } else {
                           p.getWorld().dropItem(e.getBlock().getLocation(), drops);
                        }
                    }

                } else {

                    for (ItemStack drops : e.getBlock().getDrops()) {

                        if (isAutoPickupEnabled) {
                            if (!autoPickupOFF.contains(p.getName())) {
                                dropsItems(p, drops, e.getBlock().getLocation());
                            } else {
                                p.getWorld().dropItem(e.getBlock().getLocation().clone(), drops);
                            }
                        } else {
                            p.getWorld().dropItem(e.getBlock().getLocation(), drops);
                        }
                    }
                }
            } else {
                for (ItemStack drops : e.getBlock().getDrops()) {

                    if (isAutoPickupEnabled) {
                        if (!autoPickupOFF.contains(p.getName())) {
                            dropsItems(p, drops, e.getBlock().getLocation());
                        } else {
                            p.getWorld().dropItem(e.getBlock().getLocation().clone(), drops);
                        }
                    } else {
                        p.getWorld().dropItem(e.getBlock().getLocation(), drops);
                    }
                }
            }

        } else {

            if (p.getInventory().getItemInHand().containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS)) {
                int fortuneLevel = p.getInventory().getItemInHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);

                Random r = new Random();
                int min = 1;
                int multiply = r.nextInt((fortuneLevel - min) + 1) + min;
                for (ItemStack drops : e.getBlock().getDrops()) {
                    drops.setAmount(multiply);

                    if (isAutoPickupEnabled) {
                        if (!autoPickupOFF.contains(p.getName())) {
                            dropsItems(p, drops, e.getBlock().getLocation());
                        } else {
                            p.getWorld().dropItem(e.getBlock().getLocation().clone(), drops);
                        }
                    } else {
                        p.getWorld().dropItem(e.getBlock().getLocation(), drops);
                    }
                }
            } else {
                for (ItemStack drops : e.getBlock().getDrops()) {

                    if (isAutoPickupEnabled) {
                        if (!autoPickupOFF.contains(p.getName())) {
                            dropsItems(p, drops, e.getBlock().getLocation());
                        } else {
                            p.getWorld().dropItem(e.getBlock().getLocation().clone(), drops);
                        }
                    } else {
                        p.getWorld().dropItem(e.getBlock().getLocation(), drops);
                    }
                }
            }
        }
        removeDrops(e);
    }


}