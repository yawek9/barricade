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
import xyz.yawek.barricade.config.Config;
import xyz.yawek.barricade.user.StoredAddress;
import xyz.yawek.barricade.user.StoredUser;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class InfoCommand extends PermissibleCommand {

    public InfoCommand(Barricade barricade) {
        super(barricade, "barricade.info");
    }

    @Override
    public void handle(CommandSource source, String[] args) {
        Config config = barricade.getConfig();
        if (args.length >= 1) {
            Optional<StoredAddress> storedAddressOptional =
                    barricade.getAddressManager().getOptional(args[0]);
            if (storedAddressOptional.isPresent()) {
                StoredAddress storedAddress = storedAddressOptional.get();

                source.sendMessage(config.addressInfo(
                        storedAddress.getAddress(),
                        storedAddress.isWhitelisted(),
                        storedAddress.isBlacklisted(),
                        storedAddress.getNicknames()));
                return;
            }

            Optional<StoredUser> storedUserOptional =
                    barricade.getStoredUserManager().getOptional(args[0]);
            if (storedUserOptional.isPresent()) {
                StoredUser storedUser = storedUserOptional.get();

                source.sendMessage(config.playerInfo(
                        storedUser.getNickname(),
                        storedUser.isWhitelisted(),
                        storedUser.isBlacklisted(),
                        storedUser.getAddresses()));
                return;
            }
            source.sendMessage(config.wrongAddressPlayer(args[0]));
            return;
        }
        source.sendMessage(config.infoUsage());
    }

    @Override
    protected @NotNull List<String> handleSuggestion(CommandSource source, String[] args) {
        return Collections.emptyList();
    }

}
