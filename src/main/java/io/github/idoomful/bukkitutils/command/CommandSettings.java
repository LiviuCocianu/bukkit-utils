package io.github.idoomful.bukkitutils.command;

import io.github.idoomful.bukkitutils.statics.TextUtils;
import org.apache.commons.lang.ArrayUtils;

/*

    This class serves as a wrapper for the settings of {@link ModularCommand}
    {@link CommandSettings}'s fields are defined with default values for convenience

    @param name  The name of the command

    @author Liviu Cocianu
    @version October 11, 2019

*/

public class CommandSettings {
    private final String name;
    public String[] aliases;
    public String permission = "";
    public String permMessage = TextUtils.color("&cYou have no permission!");
    public CommandRunner cmdRunner = CommandRunner.BOTH;

    public CommandSettings(String name) {
        this.name = name;
        this.aliases = new String[10];
        this.aliases[0] = name;
    }

    /*
        Returns the very same instance of this {@link CommandSettings}

        @param aliases  The list of aliases that can trigger the same command
    */
    public CommandSettings setAliases(String[] aliases) {
        this.aliases = (String[]) ArrayUtils.add(aliases, 0, this.name);
        return this;
    }

    /*
        Returns the very same instance of this {@link CommandSettings}

        @param permission  The permission required to execute the command
    */
    public CommandSettings setPermission(String permission) {
        this.permission = permission;
        return this;
    }

    /*
        Returns the very same instance of this {@link CommandSettings}

        @param permMessage  The permission message that will be displayed to the sender
                            if they don't have the required permission
    */
    public CommandSettings setPermissionMessage(String permMessage) {
        this.permMessage = permMessage;
        return this;
    }

    /*
        Returns the very same instance of this {@link CommandSettings}

        @param cmdRunner  The type of sender that is allowed to run this command
    */
    public CommandSettings workOnlyFor(CommandRunner cmdRunner) {
        this.cmdRunner = cmdRunner;
        return this;
    }
}
