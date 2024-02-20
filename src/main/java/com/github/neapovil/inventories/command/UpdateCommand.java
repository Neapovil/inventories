package com.github.neapovil.inventories.command;

import java.io.IOException;

import com.github.neapovil.inventories.Inventories;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.arguments.StringArgument;

public final class UpdateCommand implements ICommand
{
    private final Inventories plugin = Inventories.instance();

    public void register()
    {
        new CommandAPICommand("inventories")
                .withPermission(Inventories.USER_PERMISSION)
                .withArguments(new LiteralArgument("update").withPermission(Inventories.ADMIN_PERMISSION))
                .withArguments(new StringArgument("name").replaceSuggestions(ArgumentSuggestions.strings(info -> plugin.suggestions())))
                .executesPlayer((player, args) -> {
                    final String name = (String) args.get("name");

                    if (!plugin.exists(name))
                    {
                        CommandAPI.failWithString("Inventory " + name + " doesn't exist");
                    }

                    try
                    {
                        plugin.create(name, player.getInventory());
                        player.sendMessage("Inventory content updated with name: " + name);
                    }
                    catch (IOException e)
                    {
                        plugin.getLogger().severe(e.getMessage());
                        throw CommandAPI.failWithString("Unable to update inventory content");
                    }
                })
                .register();
    }
}
