package com.github.neapovil.inventories.command;

import com.github.neapovil.inventories.Inventories;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.arguments.StringArgument;

public final class CreateCommand
{
    private static final Inventories plugin = Inventories.getInstance();

    public static final void register()
    {
        new CommandAPICommand("inventories")
                .withPermission("inventories.command")
                .withArguments(new LiteralArgument("create").withPermission("inventories.command.admin"))
                .withArguments(new StringArgument("name"))
                .executesPlayer((player, args) -> {
                    final String name = (String) args[0];

                    if (plugin.exists(name))
                    {
                        CommandAPI.fail("An inventory with this name already exist.");
                    }

                    plugin.createInventory(name, player.getInventory());

                    player.sendMessage("Inventory " + name + " created");
                })
                .register();
    }
}
