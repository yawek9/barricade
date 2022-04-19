/*
 * This file is part of Barricade, licensed under GNU GPLv3 license.
 * Copyright (C) 2022 yawek9
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package xyz.yawek.barricade.command.subcommand;

import com.velocitypowered.api.command.CommandSource;
import org.jetbrains.annotations.NotNull;
import xyz.yawek.barricade.Barricade;
import xyz.yawek.barricade.command.PermissibleCommand;

import java.util.Collections;
import java.util.List;

public class ReloadCommand extends PermissibleCommand {

    public ReloadCommand(Barricade barricade) {
        super(barricade, "barricade.reload");
    }

    @Override
    protected void handle(CommandSource source, String[] args) {
        barricade.reload();
        source.sendMessage(barricade.getConfig().configReloaded());
    }

    @Override
    protected @NotNull List<String> handleSuggestion(CommandSource source, String[] args) {
        return Collections.emptyList();
    }

}
