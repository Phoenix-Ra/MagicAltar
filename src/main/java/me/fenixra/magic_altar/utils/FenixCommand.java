package me.fenixra.magic_altar.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.logging.Level;

public abstract class FenixCommand implements  CommandExecutor {
    private String usage;
    private String permission;
    private boolean allowConsole;
    private int minArgs;
    private String prefix;
    protected CommandSender sender;
    protected Player player;
    protected boolean isPlayer;
    private String[] args;
    private boolean adminCmd = false;

    public FenixCommand() {
        this.usage = null;
        this.permission = null;
        this.allowConsole = true;
        this.minArgs = 0;
        this.prefix = "";

    }
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.METHOD })
    public @interface SubCommand {
        String name() default "";

        String description() default "";

        String usage() default "";

        String permission() default "";

        int minArgs() default 0;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final org.bukkit.command.Command command, final String label, final String[] args) {
        this.isPlayer = (sender instanceof Player);
        this.sender = sender;
        this.args = args;
        if (!this.allowConsole && !this.isPlayer) {
            this.reply(false, "§cYou have to be a player to execute this command!");
            return true;
        }
        if (this.isPlayer) {
            this.player = (Player) sender;
        }
        if (this.permission != null && !sender.hasPermission(this.permission)) {
            if (adminCmd) {
                this.reply(false, "Unknown command. Type '/help' for help.");
            } else {
                this.reply(false, "§cYou don't have a permission!");
            }
            return true;
        }
        if (args.length < this.minArgs) {
            this.reply(false, "§cThat command requires at least " + this.minArgs + " arguments!");
            this.reply("§7Command Usage:");
            this.reply("§7" + this.usage);
            return true;
        }
        try {
            this.execute();
        } catch (Exception e) {
            replyException(e,"§cUnexpected error occurred while trying to execute the command!");
        }
        return true;
    }

    public abstract void execute();

    protected void reply(final String message, final Object... args) {
        this.reply(true, message, args);
    }

    protected void reply(final boolean success, final String message, final Object... args) {
        this.reply(this.sender, success, message, args);
    }

    protected void reply(final CommandSender sender, final boolean success, final String message, final Object... args) {
        final String text = this.prefix + this.prefix +" "+ message.replaceAll("&", "§");
        sender.sendMessage(text);
    }

    protected void reply(final Player p, final String message) {
        final String text = this.prefix +" "+ message.replaceAll("&", "§");
        p.sendMessage(text);
    }
    protected void replyException(final Exception exception, final String messageToSender) {
        exception.printStackTrace();
        Bukkit.getLogger().log(Level.SEVERE, prefix+ " §cUnexpected error occurred while trying to execute the command! ", exception);

        final String text = this.prefix + this.prefix +" "+ messageToSender.replaceAll("&", "§");
        sender.sendMessage(text);
    }

    protected String getArg(final int index) {
        return this.args[index];
    }

    protected void setArg(final int index, String value) {
        if (args.length < index + 1) {
            String[] newarr = new String[index + 1];
            for (int i = 0; i < args.length; i++) {
                newarr[i] = args[i];
            }
            newarr[index] = value;
            args = newarr;
            return;
        }
        this.args[index] = value;
    }

    protected int getArgAsInt(final int index) {
        return Integer.parseInt(this.getArg(index));
    }

    protected Player getArgAsPlayer(final int index) {
        return Bukkit.getPlayer(this.getArg(index));
    }

    protected int getArgLength() {
        return this.args.length;
    }

    protected String getUsage() {
        return this.usage;
    }

    protected void setUsage(final String usage) {
        this.usage = usage;
    }

    public String getPermission() {
        return this.permission;
    }

    protected void setPermission(final String permission) {
        this.permission = permission;
    }

    protected int getMinArgs() {
        return this.minArgs;
    }

    protected void setMinArgs(final int minArgs) {
        this.minArgs = minArgs;
    }

    protected boolean isAllowConsole() {
        return this.allowConsole;
    }

    protected void setAllowConsole(final boolean allowConsole) {
        this.allowConsole = allowConsole;
    }

    protected String getPrefix() {
        return this.prefix;
    }

    protected void setPrefix(final String prefix) {
        this.prefix = ChatColor.translateAlternateColorCodes('&', prefix);
    }

    protected void setIsCommandForAdmins(final boolean b) {
        adminCmd = b;
    }
}
