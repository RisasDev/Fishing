package dev.risas.sukagepond.utilities.chat;

import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@UtilityClass
public class ChatUtil {

    public final String NORMAL_LINE = "&7&m-----------------------------";
    private final char COLOR_CHAR = ChatColor.COLOR_CHAR;
    private final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");
    private final int CENTER_PX = 154;

    public String translate(String text) {
        Matcher matcher = HEX_PATTERN.matcher(text);
        StringBuilder builder = new StringBuilder(text.length() + 4 * 8);

        while (matcher.find()) {
            String group = matcher.group(1);
            matcher.appendReplacement(builder, COLOR_CHAR + "x"
                    + COLOR_CHAR + group.charAt(0) + COLOR_CHAR + group.charAt(1)
                    + COLOR_CHAR + group.charAt(2) + COLOR_CHAR + group.charAt(3)
                    + COLOR_CHAR + group.charAt(4) + COLOR_CHAR + group.charAt(5)
            );
        }

        String message = matcher.appendTail(builder).toString();

        if (message.contains("[center]")) {
            message = message.replace("[center]", "");
            message = getCenteredText(message);
        }

        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public String[] translate(String[] array) {
        for (int i = 0; i < array.length; i++) {
            array[i] = translate(array[i]);
        }
        return array;
    }

    public List<String> translate(List<String> list) {
        return list.stream().map(ChatUtil::translate).collect(Collectors.toList());
    }

    public void sendMessage(CommandSender sender, String text) {
        if (!text.isEmpty()) {
            sender.sendMessage(translate(text));
        }
    }

    public void sendMessage(CommandSender sender, String[] array) {
        if (array.length != 0) {
            sender.sendMessage(translate(array));
        }
    }

    public void sendMessageComponent(Player player, TextComponent text) {
        if (text != null) {
            player.spigot().sendMessage(text);
        }
    }

    public void broadcast(String text) {
        if (!text.isEmpty()) {
            Bukkit.broadcastMessage(translate(text));
        }
    }

    public String capitalize(String text) {
        StringBuilder builder = new StringBuilder();

        for (String word : text.split(" ")) {
            builder
                    .append(word.substring(0, 1).toUpperCase())
                    .append(word.substring(1))
                    .append(" ");
        }

        return builder.toString().trim();
    }

    public String getCenteredText(String message) {
        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;

        for (char c : message.toCharArray()) {
            if (c == '§') {
                previousCode = true;
            }
            else if (previousCode) {
                previousCode = false;
                isBold = c == 'l' || c == 'L';
            }
            else {
                DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
                messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
                messagePxSize++;
            }
        }

        int halvedMessageSize = messagePxSize / 2;
        int toCompensate = CENTER_PX - halvedMessageSize;
        int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
        int compensated = 0;
        StringBuilder sb = new StringBuilder();

        while(compensated < toCompensate){
            sb.append(" ");
            compensated += spaceLength;
        }

        return sb + message;
    }
}
