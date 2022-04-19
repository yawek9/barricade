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
import xyz.yawek.barricade.manager.AddressManager;
import xyz.yawek.barricade.manager.StoredUserManager;
import xyz.yawek.barricade.user.StoredAddress;
import xyz.yawek.barricade.user.StoredUser;

import java.util.*;

public class WhitelistCommand extends PermissibleCommand {

    public WhitelistCommand(Barricade barricade) {
        super(barricade, "barricade.whitelist");
    }

    @Override
    public void handle(CommandSource source, String[] args) {
        Config config = barricade.getConfig();
        if (args.length >= 2) {
            AddressManager addressManager = barricade.getAddressManager();
            StoredUserManager storedUserManager = barricade.getStoredUserManager();
            if (args[0].equalsIgnoreCase("add")) {
                Optional<StoredAddress> storedAddressOptional =
                        barricade.getAddressManager().getOptional(args[1]);
                if (storedAddressOptional.isPresent()) {
                    StoredAddress storedAddress = storedAddressOptional.get();
                    if (storedAddress.isWhitelisted()) {
                        source.sendMessage(config.addressAlreadyWhitelisted(args[1]));
                        return;
                    }
                    storedAddress.setWhitelisted(true);
                    addressManager.update(storedAddress);
                    source.sendMessage(config.addressWhitelisted(args[1]));
                    return;
                }

                Optional<StoredUser> storedUserOptional =
                        barricade.getStoredUserManager().getOptional(args[1]);
                if (storedUserOptional.isPresent()) {
                    StoredUser storedUser = storedUserOptional.get();
                    if (storedUser.isWhitelisted()) {
                        source.sendMessage(config.playerAlreadyWhitelisted(args[1]));
                        return;
                    }
                    storedUser.setWhitelisted(true);
                    storedUserManager.update(storedUser);
                    source.sendMessage(config.playerWhitelisted(args[1]));
                    return;
                }
                source.sendMessage(config.wrongAddressPlayer(args[1]));
                return;
            } else if (args[0].equalsIgnoreCase("remove")) {
                Optional<StoredAddress> storedAddressOptional =
                        barricade.getAddressManager().getOptional(args[1]);
                if (storedAddressOptional.isPresent()) {
                    StoredAddress storedAddress = storedAddressOptional.get();
                    if (!storedAddress.isWhitelisted()) {
                        source.sendMessage(config.addressNotWhitelisted(args[1]));
                        return;
                    }
                    storedAddress.setWhitelisted(false);
                    addressManager.update(storedAddress);
                    source.sendMessage(config.addressWhitelistRemoved(args[1]));
                    return;
                }

                Optional<StoredUser> storedUserOptional =
                        barricade.getStoredUserManager().getOptional(args[1]);
                if (storedUserOptional.isPresent()) {
                    StoredUser storedUser = storedUserOptional.get();
                    if (!storedUser.isWhitelisted()) {
                        source.sendMessage(config.playerNotWhitelisted(args[1]));
                        return;
                    }
                    storedUser.setWhitelisted(false);
                    storedUserManager.update(storedUser);
                    source.sendMessage(config.playerWhitelistRemoved(args[1]));
                    return;
                }
                source.sendMessage(config.wrongAddressPlayer(args[1]));
                return;
            }
        }
        source.sendMessage(config.whitelistUsage());
    }

    @Override
    protected @NotNull List<String> handleSuggestion(CommandSource source, String[] args) {
        if (args.length == 1) {
            return new ArrayList<>(Arrays.asList("add", "remove"));
        }
        return Collections.emptyList();
    }

}
