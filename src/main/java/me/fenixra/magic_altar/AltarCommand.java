package me.fenixra.magic_altar;

import com.google.common.collect.Maps;
import me.fenixra.magic_altar.utils.FenixCommand;
import me.fenixra.magic_altar.utils.Utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class AltarCommand extends FenixCommand {
    private final Main plugin;
    private final Map<String, Method> methods = Maps.newLinkedHashMap();

    public AltarCommand(Main plugin){
        this.plugin = plugin;
        this.setAllowConsole(true);
        this.setPermission("altar.admin");
        this.setUsage("/altar help");
        this.setPrefix("§bMagic§6Altar");
        for (final Method method : this.getClass().getMethods()) {
            final SubCommand subCommand = method.getAnnotation(SubCommand.class);
            if (subCommand != null) {
                this.methods.put(subCommand.name().isEmpty() ? method.getName() : subCommand.name(), method);
            }
        }
    }

    @Override
    public void execute(){
        final String subCommand = (this.getArgLength() > 0) ? this.getArg(0) : "help";
        final Method method = this.methods.get(subCommand.toLowerCase());
        if (method == null) {
            this.reply(false, "§cUnknown command");
            return;
        }
        final SubCommand info = method.getAnnotation(SubCommand.class);
        if (this.getArgLength() < info.minArgs() + 1) {
            this.reply(false,"§cNot enough arguments");
            return;
        }
        try {
            method.invoke(this);
        }catch (InvocationTargetException | IllegalAccessException e){
            replyException(e,"§cUnexpected error occurred while trying to execute the command!");
        }

    }

    @SubCommand(description = "commands list", minArgs = -1, usage = "/altar help")
    public void help() {
        String[] s = {"help", "create","edit", "remove","reload","changestate"};
        this.reply("Available commands:");
        for (String entry : s) {
            final SubCommand info = methods.get(entry).getAnnotation(SubCommand.class);
            final String usage = info.usage().isEmpty() ? "" : (" " + (info.usage()));
            final String desc = info.description();
            this.sender.sendMessage(Utils.colorFormat("&e"+usage + " &7- &f" + desc));
        }


    }

    @SubCommand(description = "Create the altar", minArgs = 3, usage = "/altar create [id] [radius] [frequency in seconds]")
    public void create() {
        if(!this.isPlayer) {
            this.reply("&cConsole cannot execute that command!");
            return;
        }
        String id= this.getArg(1);
        if(!this.getArg(2).replaceAll("[0-9]","").equals("")) {
            this.reply("§cRADIUS value contains prohibited symbols!");
            return;
        }
        if(!this.getArg(3).replaceAll("[0-9]","").equals("")) {
            this.reply("§cFREQUENCY value contains prohibited symbols!");
            return;
        }
        int radius=this.getArgAsInt(2);
        int frequency=this.getArgAsInt(3);
        plugin.getAltarM().giveSetupWand(player, id, radius, frequency);
        this.reply("&aSuccess! We gave you a special stick. Click with that stick on the block u want to make an altar");


    }
    @SubCommand(description = "Edit the altar position", minArgs = -1, usage = "/altar edit [id]")
    public void edit() {
        if(!this.isPlayer) {
            this.reply("&cConsole cannot execute that command!");
            return;
        }
        String id= this.getArg(1);
        Altar altar = plugin.getAltarM().getAltar(id);
        if(altar==null) {
            this.reply("&cAltar with given id doesn't exist");
            return;
        }
        plugin.getAltarM().removeAltar(id);
        altar.prepareForEdit();
        plugin.getAltarM().giveSetupWand(player, altar);
        this.reply("&aSuccess! We gave you a special stick. Click with that stick on the block u want to make an altar");


    }
    @SubCommand(description = "Remove existing altar", minArgs = -1, usage = "/altar remove")
    public void remove() {
        String id= this.getArg(1);
        Altar altar = plugin.getAltarM().getAltar(id);
        if(altar==null) {
            this.reply("&cAltar with given id doesn't exist");
            return;
        }
        plugin.getAltarM().removeAltar(id);
        this.reply("&aAltar was successfully removed");

    }

    @SubCommand(description = "Reload the plugin", minArgs = -1, usage = "/altar reload")
    public void reload() {
        plugin.reload();
        this.reply("&aSuccessfully reloaded");

    }
    @SubCommand(description = "Change pvp state", minArgs = 1, usage = "/altar changestate [id]")
    public void changestate() {
        String id= this.getArg(1);
        Altar altar = plugin.getAltarM().getAltar(id);
        if(altar==null) {
            this.reply("&cAltar with given id doesn't exist");
            return;
        }
        altar.getPvpChanger().changeState();
        this.reply("Successfully changed pvp state of an altar");

    }

}
