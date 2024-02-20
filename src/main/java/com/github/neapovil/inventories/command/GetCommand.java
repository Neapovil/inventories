package com.github.neapovil.inventories.command;

import com.github.neapovil.inventories.Inventories;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.arguments.StringArgument;

public final class GetCommand implements ICommand
{
    private final Inventories plugin = Inventories.instance();

    public void register()
    {
        new CommandAPICommand("inventories")
                .withPermission(Inventories.USER_PERMISSION)
                .withArguments(new LiteralArgument("get"))
                .withArguments(new StringArgument("name").replaceSuggestions(ArgumentSuggestions.strings(info -> plugin.suggestions())))
                .executesPlayer((player, args) -> {
                    final String name = (String) args.get("name");

                    if (!plugin.exists(name))
                    {
                        throw CommandAPI.failWithString("Inventory " + name + " doesn't exists.");
                    }

                    plugin.set(name, player);

                    player.sendMessage("Set own inventory to: " + name);
                })
                .register();
    }
}
