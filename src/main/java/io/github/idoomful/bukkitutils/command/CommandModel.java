package io.github.idoomful.bukkitutils.command;

import org.bukkit.command.CommandSender;

public interface CommandModel {
    void execute(CommandSender player, String[] args);
}
