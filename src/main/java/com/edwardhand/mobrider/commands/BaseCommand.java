/*
 * BaseCommand from RightLegRed
 */

package com.edwardhand.mobrider.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

public abstract class BaseCommand
{
    protected String permission;
    protected String usage;
    protected int minArgs;
    protected int maxArgs;
    protected List<String> identifiers = new ArrayList<String>();;

    public abstract void execute(CommandSender commandSender, String[] args);

    public String[] validate(String input, StringBuilder identifier)
    {
        String match = matchIdentifier(input);

        if (match != null) {
            identifier = identifier.append(match);
            int i = identifier.length();
            String[] args = input.substring(i).trim().split(" ");
            if (args[0].isEmpty()) {
                args = new String[0];
            }
            int l = args.length;
            if ((l >= this.minArgs) && (l <= this.maxArgs)) {
                return args;
            }
        }
        return null;
    }

    public String matchIdentifier(String input)
    {
        String lower = input.toLowerCase();

        int index = -1;
        int n = this.identifiers.size();
        for (int i = 0; i < n; i++) {
            String identifier = ((String) this.identifiers.get(i)).toLowerCase();
            if (lower.matches(identifier + "(\\s+.*|\\s*)")) {
                index = i;
            }
        }

        if (index != -1) {
            return (String) this.identifiers.get(index);
        }
        return null;
    }

    public List<String> getIdentifiers()
    {
        return this.identifiers;
    }

    public void setIdentifiers(List<String> identifiers)
    {
        this.identifiers = identifiers;
    }

    public String getUsage()
    {
        return this.usage;
    }

    public String getPermission()
    {
        return this.permission;
    }
}
