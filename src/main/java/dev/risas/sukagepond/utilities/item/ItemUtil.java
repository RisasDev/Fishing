package dev.risas.sukagepond.utilities.item;

import lombok.experimental.UtilityClass;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class ItemUtil {

    public String getCustomDisplayName(ItemStack itemStack) {
        String itemType = itemStack.getType().name();

        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return itemType;

        int amount = itemStack.getAmount();
        String displayName = itemMeta.hasDisplayName() ? itemMeta.getDisplayName() : itemType;
        return amount > 1 ? extractInitialFormatCodes(displayName) + amount + " " + displayName : displayName;
    }

    public String extractInitialFormatCodes(String input) {
        Pattern pattern = Pattern.compile("^(&[0-9a-fk-orA-FK-OR])+");
        Matcher matcher = pattern.matcher(input);
        return matcher.find() ? matcher.group(0) : "";
    }
}
