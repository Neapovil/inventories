package com.github.neapovil.inventories;

import java.io.File;
import java.util.List;

import org.bukkit.attribute.Attribute;
import org.bukkit.block.data.type.Sign;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.file.FileConfig;
import com.github.neapovil.inventories.command.CreateCommand;
import com.github.neapovil.inventories.command.DeleteCommand;
import com.github.neapovil.inventories.command.GetCommand;
import com.github.neapovil.inventories.command.GiveCommand;
import com.github.neapovil.inventories.command.UpdateCommand;
import com.github.neapovil.inventories.util.SerializeInventory;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.md_5.bungee.api.ChatColor;

public final class Inventories extends JavaPlugin implements Listener
{
    private static Inventories instance;
    private FileConfig config;
    public static final String PERMISSION = "inventories.command";
    public static final String ADMIN_PERMISSION = "inventories.command.admin";

    @Override
    public void onEnable()
    {
        instance = this;

        this.saveResource("inventories.json", false);

        this.config = FileConfig.builder(new File(this.getDataFolder(), "inventories.json"))
                .autoreload()
                .autosave()
                .build();
        this.config.load();

        CreateCommand.register();
        GetCommand.register();
        DeleteCommand.register();
        GiveCommand.register();
        UpdateCommand.register();

        this.getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable()
    {
    }

    public static Inventories getInstance()
    {
        return instance;
    }

    public boolean exists(String name)
    {
        return this.config.get("inventories." + name) != null;
    }

    public void createInventory(String name, PlayerInventory playerInventory)
    {
        this.config.set("inventories." + name + ".inventory", SerializeInventory.playerInventoryToBase64(playerInventory));
    }

    public void setInventory(String name, Player player)
    {
        player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
        player.getActivePotionEffects().forEach(e -> player.removePotionEffect(e.getType()));

        try
        {
            player.getInventory().setContents(SerializeInventory.itemStackArrayFromBase64(this.config.get("inventories." + name + ".inventory")));
        }
        catch (Exception e)
        {
            player.sendMessage(ChatColor.RED + "Unable to set inventory.");
            this.getLogger().severe(e.getMessage());
        }
    }

    public void deleteInventory(String name)
    {
        this.config.remove("inventories." + name);
    }

    public List<String> getInventories()
    {
        final Config inv = this.config.get("inventories");
        return inv.entrySet().stream().map(i -> i.getKey()).toList();
    }

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

        final String name = ((TextComponent) sign.line(1)).content();

        this.setInventory(name, event.getPlayer());

        event.getPlayer().sendMessage("Set own inventory to " + name);
    }

    @EventHandler
    public void signChange(SignChangeEvent event)
    {
        if (!((TextComponent) event.line(0)).content().equalsIgnoreCase("[Inventories]"))
        {
            return;
        }

        final String name = ((TextComponent) event.line(1)).content();

        if (name.isBlank())
        {
            event.getPlayer().sendMessage(ChatColor.RED + "You must type the inventory name on the second line");
            event.getBlock().breakNaturally();
            return;
        }

        if (!this.exists(name))
        {
            event.getPlayer().sendMessage(ChatColor.RED + "Inventory " + name + " doesn't exist");
            event.getBlock().breakNaturally();
            return;
        }

        event.line(0, Component.text("[Inventories]", NamedTextColor.LIGHT_PURPLE, TextDecoration.BOLD));
    }
}
