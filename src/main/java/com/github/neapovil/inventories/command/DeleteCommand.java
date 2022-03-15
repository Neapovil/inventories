package com.github.neapovil.inventories.command;

import com.github.neapovil.inventories.Inventories;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.arguments.StringArgument;

public final class DeleteCommand
{
    private static final Inventories plugin = Inventories.getInstance();

    public static final void register()
    {
        new CommandAPICommand("inventories")
                .withPermission(Inventories.PERMISSION)
                .withArguments(new LiteralArgument("delete").withPermission(Inventories.ADMIN_PERMISSION))
                .withArguments(new StringArgument("name").replaceSuggestions(info -> plugin.getInventories().toArray(String[]::new)))
                .executes((sender, args) -> {
                    final String name = (String) args[0];

                    if (!plugin.exists(name))
                    {
                        CommandAPI.fail("Inventory " + name + " doesn't exist.");
                    }

                    plugin.deleteInventory(name);

                    sender.sendMessage("Inventory " + name + " deleted");
                })
                .register();
    }
}
