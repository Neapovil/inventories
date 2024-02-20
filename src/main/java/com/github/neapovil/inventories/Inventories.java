package com.github.neapovil.inventories;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Optional;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.neapovil.inventories.command.CreateCommand;
import com.github.neapovil.inventories.command.DeleteCommand;
import com.github.neapovil.inventories.command.GetCommand;
import com.github.neapovil.inventories.command.GiveCommand;
import com.github.neapovil.inventories.command.UpdateCommand;
import com.github.neapovil.inventories.gson.InventoriesObject;
import com.github.neapovil.inventories.gson.InventoriesObject.Inventory;
import com.github.neapovil.inventories.listener.SignListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class Inventories extends JavaPlugin
{
    private static Inventories instance;
    public static final String USER_PERMISSION = "inventories.command";
    public static final String ADMIN_PERMISSION = "inventories.command.admin";
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private InventoriesObject inventoriesObject;

    @Override
    public void onEnable()
    {
        instance = this;

        this.saveResource("inventories.json", false);

        try
        {
            this.load();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        new CreateCommand().register();
        new GetCommand().register();
        new DeleteCommand().register();
        new GiveCommand().register();
        new UpdateCommand().register();

        this.getServer().getPluginManager().registerEvents(new SignListener(), this);
    }

    @Override
    public void onDisable()
    {
    }

    public static Inventories instance()
    {
        return instance;
    }

    public void load() throws IOException
    {
        final String string = Files.readString(this.getDataFolder().toPath().resolve("inventories.json"));
        this.inventoriesObject = this.gson.fromJson(string, InventoriesObject.class);
    }

    public void save() throws IOException
    {
        final String string = this.gson.toJson(this.inventoriesObject);
        Files.write(this.getDataFolder().toPath().resolve("inventories.json"), string.getBytes());
    }

    public Optional<Inventory> find(String name)
    {
        return this.inventoriesObject.inventories.stream().filter(i -> i.name.equalsIgnoreCase(name)).findFirst();
    }

    public void create(String name, PlayerInventory playerInventory) throws IOException
    {
        final InventoriesObject.Inventory inventory = new InventoriesObject.Inventory();

        inventory.items.addAll(Arrays.asList(playerInventory.getContents()));
        inventory.name = name;

        this.inventoriesObject.inventories.removeIf(i -> i.name.equalsIgnoreCase(name));
        this.inventoriesObject.inventories.add(inventory);

        this.save();
        this.load();
    }

    public void set(String name, Player player)
    {
        this.find(name).ifPresent(i -> this.set(i, player));
    }

    public void set(Inventory inventory, Player player)
    {
        player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
        player.getActivePotionEffects().forEach(e -> player.removePotionEffect(e.getType()));
        player.getInventory().setContents(inventory.items.toArray(ItemStack[]::new));
    }

    public boolean delete(String name) throws IOException
    {
        final boolean removed = this.inventoriesObject.inventories.removeIf(i -> i.name.equalsIgnoreCase(name));

        if (removed)
        {
            this.save();
        }

        return removed;
    }

    public boolean exists(String name)
    {
        return this.inventoriesObject.inventories.stream().anyMatch(i -> i.name.equalsIgnoreCase(name));
    }

    public String[] suggestions()
    {
        return this.inventoriesObject.inventories.stream().map(i -> i.name).toArray(String[]::new);
    }

    public InventoriesObject inventories()
    {
        return this.inventoriesObject;
    }
}
