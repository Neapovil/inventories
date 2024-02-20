package com.github.neapovil.inventories.command;

import org.bukkit.entity.Player;

import com.github.neapovil.inventories.Inventories;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.arguments.StringArgument;

public final class GiveCommand implements ICommand
{
    private final Inventories plugin = Inventories.instance();

    public void register()
    {
        new CommandAPICommand("inventories")
                .withPermission(Inventories.USER_PERMISSION)
                .withArguments(new LiteralArgument("give").withPermission(Inventories.ADMIN_PERMISSION))
                .withArguments(new PlayerArgument("player"))
                .withArguments(new StringArgument("name").replaceSuggestions(ArgumentSuggestions.strings(info -> plugin.suggestions())))
                .executesPlayer((player, args) -> {
                    final Player player1 = (Player) args.get("player");
                    final String name = (String) args.get("name");

                    if (!plugin.exists(name))
                    {
                        throw CommandAPI.failWithString("Inventory " + name + " doesn't exists");
                    }

                    plugin.set(name, player1);

                    player.sendMessage("Gave inventory " + name + " to " + player1.getName());
                })
                .register();
    }
}
