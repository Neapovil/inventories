package com.github.neapovil.inventories.gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.bukkit.inventory.ItemStack;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.JsonAdapter;

public class InventoriesObject
{
    public List<Inventory> inventories = new ArrayList<>();

    public static class Inventory
    {
        @JsonAdapter(ItemsAdapter.class)
        public List<ItemStack> items = new ArrayList<>();
        public String name;
    }

    class ItemsAdapter implements JsonSerializer<List<ItemStack>>, JsonDeserializer<List<ItemStack>>
    {
        @Override
        public List<ItemStack> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
        {
            final List<ItemStack> items = new ArrayList<>();

            for (JsonElement i : json.getAsJsonArray())
            {
                if (i.getAsString().equals("null"))
                {
                    items.add(null);
                }
                else
                {
                    final byte[] bytes = Base64.getDecoder().decode(i.getAsString());
                    items.add(ItemStack.deserializeBytes(bytes));
                }
            }

            return items;
        }

        @Override
        public JsonElement serialize(List<ItemStack> src, Type typeOfSrc, JsonSerializationContext context)
        {
            final JsonArray array = new JsonArray();

            for (ItemStack i : src)
            {
                if (i == null || i.getType().isAir())
                {
                    array.add("null");
                }
                else
                {
                    array.add(Base64.getEncoder().encodeToString(i.serializeAsBytes()));
                }
            }

            return array;
        }
    }
}
