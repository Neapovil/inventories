package com.github.neapovil.inventories.command;

import java.io.IOException;

import com.github.neapovil.inventories.Inventories;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.arguments.StringArgument;

public final class DeleteCommand implements ICommand
{
    private final Inventories plugin = Inventories.instance();

    public void register()
    {
        new CommandAPICommand("inventories")
                .withPermission(Inventories.USER_PERMISSION)
                .withArguments(new LiteralArgument("delete").withPermission(Inventories.ADMIN_PERMISSION))
                .withArguments(new StringArgument("name").replaceSuggestions(ArgumentSuggestions.strings(info -> plugin.suggestions())))
                .executes((sender, args) -> {
                    final String name = (String) args.get("name");

                    if (!plugin.exists(name))
                    {
                        throw CommandAPI.failWithString("Inventory " + name + " doesn't exist.");
                    }

                    try
                    {
                        plugin.delete(name);
                        sender.sendMessage("Inventory deleted with name: " + name);
                    }
                    catch (IOException e)
                    {
                        plugin.getLogger().severe(e.getMessage());
                        throw CommandAPI.failWithString("Unable to delete inventory");
                    }
                })
                .register();
    }
}
