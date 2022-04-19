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
import xyz.yawek.barricade.config.Config;
import xyz.yawek.barricade.user.ConnectingUser;

import java.util.Optional;

public class NicknameCheck extends AbstractCheck {

    public NicknameCheck(Barricade barricade) {
        super(barricade, barricade.getConfig().nicknameBlockPriority());
    }

    @Override
    public Optional<Component> check(ConnectingUser connectingUser) {
        Config config = barricade.getConfig();
        return config.restrictedNicknames()
                .stream()
                .anyMatch(s -> connectingUser.getNickname().toLowerCase()
                        .contains(s.toLowerCase()))
                ? Optional.of(config.restrictedNickname()) : Optional.empty();
    }

}
