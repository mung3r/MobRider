/*
 * This file is part of MobRider.
 *
 * Copyright (c) 2011-2015, R. Ramos <http://github.com/mung3r/>
 * MobRider is licensed under the GNU Lesser General Public License.
 *
 * MobRider is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MobRider is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.edwardhand.mobrider.commands;

import java.util.List;

import org.bukkit.command.CommandSender;

public interface Command {

    void cancelInteraction(CommandSender executor);

    boolean execute(CommandSender executor, String identifier, String[] args);

    String getDescription();

    List<String> getIdentifiers();

    int getMaxArguments();

    int getMinArguments();

    String getName();

    List<String> getNotes();

    String getPermission();

    String getUsage();

    boolean isIdentifier(CommandSender executor, String input);

    boolean isNotInProgress(CommandSender executor);

    boolean isInteractive();

    boolean isShownOnHelpMenu();

}
