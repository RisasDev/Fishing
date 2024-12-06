package dev.risas.sukagepond.utilities;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class SerializeUtil {

    public String serializeBlockLocation(Location location) {
        if (location == null || location.getWorld() == null) return null;
        return location.getWorld().getName() + ", " +
                location.getBlockX() + ", " +
                location.getBlockY() + ", " +
                location.getBlockZ();
    }

    public Location deserializeBlockLocation(String data) {
        if (data == null || data.isEmpty()) return null;

        String[] splittedData = data.split(", ");

        if (splittedData.length < 4) return null;

        World world = Bukkit.getWorld(splittedData[0]);
        if (world == null) return null;

        int x = Integer.parseInt(splittedData[1]);
        int y = Integer.parseInt(splittedData[2]);
        int z = Integer.parseInt(splittedData[3]);

        return new Location(world, x, y, z);
    }

    public List<String> serializeBlockLocations(List<Location> locations) {
        if (locations == null || locations.isEmpty()) return new ArrayList<>();
        return locations.stream().map(SerializeUtil::serializeBlockLocation).toList();
    }

    public List<Location> deserializeBlockLocations(List<String> data) {
        List<Location> locations = new ArrayList<>();
        if (data == null || data.isEmpty()) return locations;

        for (String location : data) {
            locations.add(deserializeBlockLocation(location));
        }

        return locations;
    }

    public ItemStack deserializeItemStack(String data) {
        if (data == null || data.isEmpty()) return null;

        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack item = (ItemStack) dataInput.readObject();

            dataInput.close();
            return item;
        }
        catch (Exception e) {
            throw new IllegalStateException("Unable to deserialize ItemStack", e);
        }
    }

    public String serializeItemStack(ItemStack item) {
        if (item == null) return "";

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            dataOutput.writeObject(item);
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        }
        catch (Exception e) {
            throw new IllegalStateException("Unable to serialize ItemStack", e);
        }
    }

    public ItemStack[] deserializeItemStackArray(String data) {
        if (data == null || data.isEmpty()) return new ItemStack[0];

        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack[] items = new ItemStack[dataInput.readInt()];

            for (int i = 0; i < items.length; i++) {
                items[i] = (ItemStack) dataInput.readObject();
            }

            dataInput.close();
            return items;
        }
        catch (Exception e) {
            throw new IllegalStateException("Unable to deserialize ItemStack Array", e);
        }
    }

    public String serializeItemStackArray(ItemStack[] items) {
        if (items == null) return "";

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            dataOutput.writeInt(items.length);

            for (ItemStack item : items) {
                dataOutput.writeObject(item);
            }

            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        }
        catch (Exception e) {
            throw new IllegalStateException("Unable to serialize ItemStack Array", e);
        }
    }
}
