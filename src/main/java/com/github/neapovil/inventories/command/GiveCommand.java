package com.github.neapovil.inventories.command;

import org.bukkit.entity.Player;

import com.github.neapovil.inventories.Inventories;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.arguments.StringArgument;

public final class GiveCommand
{
    private static final Inventories plugin = Inventories.getInstance();

    public static final void register()
    {
        new CommandAPICommand("inventories")
                .withPermission(Inventories.PERMISSION)
                .withArguments(new LiteralArgument("give").withPermission(Inventories.ADMIN_PERMISSION))
                .withArguments(new PlayerArgument("player"))
                .withArguments(new StringArgument("name").replaceSuggestions(info -> plugin.getInventories().toArray(String[]::new)))
                .executesPlayer((player, args) -> {
                    final Player player1 = (Player) args[0];
                    final String name = (String) args[1];

                    if (!plugin.exists(name))
                    {
                        CommandAPI.fail("Inventory " + name + " doesn't exists");
                    }

                    plugin.setInventory(name, player1);

                    player.sendMessage("Gave inventory " + name + " to " + player1.getName());
                })
                .register();
    }
}
