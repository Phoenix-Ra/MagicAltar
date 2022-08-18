package me.fenixra.magic_altar.utils;


import me.fenixra.magic_altar.Main;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static String colorFormat(String s){
        if (Main.getInstance().getServerVersion() > 15) {
            try {
                s = translateHexCodes(s);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        s= ChatColor.translateAlternateColorCodes('&',s);
        return s;
    }
    public static final Pattern HEX_PATTERN = Pattern.compile("&#(\\w{5}[0-9a-f])");

    private static String translateHexCodes (String textToTranslate) {

        Matcher matcher = HEX_PATTERN.matcher(textToTranslate);
        StringBuffer buffer = new StringBuffer();

        while(matcher.find()) {
            matcher.appendReplacement(buffer, ChatColor.of("#" + matcher.group(1)).toString());
        }

        return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString());

    }
    public static String getProgressBar(long n, long n2, int n3, String string, String string2, String string3) {
        long n4;
        double f = (double)n / (double)n2;
        long n5 = (long)((double)n3 * f);
        long n6 = n3 - n5;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(org.bukkit.ChatColor.translateAlternateColorCodes('&', string2));
        for (n4 = 0; n4 < n5; ++n4) {
            stringBuilder.append(string);
        }
        stringBuilder.append(org.bukkit.ChatColor.translateAlternateColorCodes('&', string3));
        for (n4 = 0; n4 < n6; ++n4) {
            stringBuilder.append(string);
        }
        return stringBuilder.toString();
    }

    public static Color parseColor(String s){
        Color color;
        switch (s.toLowerCase()){
            case "aqua":
                color=Color.AQUA;
                break;
            case "red":
                color=Color.RED;
                break;
            case "green":
                color=Color.GREEN;
                break;
            case "blue":
                color=Color.BLUE;
                break;
            case "fuchsia":
                color=Color.FUCHSIA;
                break;
            case "gray":
                color=Color.GRAY;
                break;
            case "lime":
                color=Color.LIME;
                break;
            case "maroon":
                color=Color.MAROON;
                break;
            case "navy":
                color=Color.NAVY;
                break;
            case "olive":
                color=Color.OLIVE;
                break;
            case "orange":
                color=Color.ORANGE;
                break;
            case "purple":
                color=Color.PURPLE;
                break;
            case "silver":
                color=Color.SILVER;
                break;
            case "teal":
                color=Color.TEAL;
                break;
            case "yellow":
                color=Color.YELLOW;
                break;
            default:
                color=Color.WHITE;
                break;

        }
        return color;
    }
}
