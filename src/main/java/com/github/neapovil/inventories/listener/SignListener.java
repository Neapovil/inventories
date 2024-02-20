package com.github.neapovil.inventories.listener;

import org.bukkit.block.data.type.Sign;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.github.neapovil.inventories.Inventories;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.md_5.bungee.api.ChatColor;

public final class SignListener implements Listener
{
    private final Inventories plugin = Inventories.instance();

    @EventHandler
    private void playerInteract(PlayerInteractEvent event)
    {
        if (event.getClickedBlock() == null)
        {
            return;
        }

        if (!(event.getClickedBlock().getBlockData() instanceof Sign || event.getClickedBlock().getBlockData() instanceof WallSign))
        {
            return;
        }

        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
        {
            return;
        }

        final org.bukkit.block.Sign sign = (org.bukkit.block.Sign) event.getClickedBlock().getState();

        if (!((TextComponent) sign.line(0)).content().startsWith("[Inventories]"))
        {
            return;
        }

        final String inventoryname = ((TextComponent) sign.line(1)).content();

        plugin.find(inventoryname).ifPresent(i -> {
            plugin.set(i, event.getPlayer());
            event.getPlayer().sendMessage("Set own inventory to " + inventoryname);
        });
    }

    @EventHandler
    private void signChange(SignChangeEvent event)
    {
        if (!((TextComponent) event.line(0)).content().equalsIgnoreCase("[Inventories]"))
        {
            return;
        }

        final String inventoryname = ((TextComponent) event.line(1)).content();

        if (inventoryname.isBlank())
        {
            event.getPlayer().sendMessage(ChatColor.RED + "You must type the inventory name on the second line");
            event.getBlock().breakNaturally();
            return;
        }

        plugin.find(inventoryname).ifPresentOrElse(i -> {
            event.line(0, Component.text("[Inventories]", NamedTextColor.LIGHT_PURPLE, TextDecoration.BOLD));
        }, () -> {
            event.getPlayer().sendRichMessage("<red>Inventory " + inventoryname + " doesn't exist");
            event.getBlock().breakNaturally();
        });
    }
}
