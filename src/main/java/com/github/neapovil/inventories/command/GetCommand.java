package com.github.neapovil.inventories.command;

import com.github.neapovil.inventories.Inventories;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.arguments.StringArgument;

public final class GetCommand
{
    private static final Inventories plugin = Inventories.getInstance();

    public static final void register()
    {
        new CommandAPICommand("inventories")
                .withPermission(Inventories.USER_PERMISSION)
                .withArguments(new LiteralArgument("get"))
                .withArguments(new StringArgument("name").replaceSuggestions(ArgumentSuggestions.strings(info -> plugin.getInventoriesAsStrings())))
                .executesPlayer((player, args) -> {
                    final String name = (String) args[0];

                    if (!plugin.exists(name))
                    {
                        throw CommandAPI.fail("Inventory " + name + " doesn't exists.");
                    }

                    plugin.setInventory(name, player);

                    player.sendMessage("Set own inventory to " + name);
                })
                .register();
    }
}
