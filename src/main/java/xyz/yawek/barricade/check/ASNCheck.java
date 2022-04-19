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

package xyz.yawek.barricade.check;

import net.kyori.adventure.text.Component;
import xyz.yawek.barricade.Barricade;
import xyz.yawek.barricade.user.ConnectingUser;

import java.util.Optional;

public class ASNCheck extends AbstractCheck {

    public ASNCheck(Barricade barricade) {
        super(barricade, barricade.getConfig().asnBlockPriority());
    }

    @Override
    public Optional<Component> check(ConnectingUser connectingUser) {
        Optional<String> asnCodeOptional = barricade.getGeoDataProvider()
                        .getAsnCode(connectingUser.getInetAddress());
        if (asnCodeOptional.isPresent()
                && barricade.getConfig().asnBlockList().contains(asnCodeOptional.get())) {
            return Optional.of(barricade.getConfig().asnBlocked());
        }
        return Optional.empty();
    }

}
