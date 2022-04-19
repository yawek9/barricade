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

package xyz.yawek.barricade.manager;

import xyz.yawek.barricade.Barricade;
import xyz.yawek.barricade.user.ConnectingUser;
import xyz.yawek.barricade.user.StoredAddress;
import xyz.yawek.barricade.user.StoredUser;

import java.net.InetAddress;

public class ConnectingUserManager {

    private final Barricade barricade;

    public ConnectingUserManager(Barricade barricade) {
        this.barricade = barricade;
    }

    public ConnectingUser create(String nickName, InetAddress inetAddress) {
        boolean whitelisted = barricade.getStoredUserManager()
                .getOptional(nickName)
                .map(StoredUser::isWhitelisted)
                .orElse(false)
                || barricade.getAddressManager()
                .getOptional(inetAddress.getHostAddress())
                .map(StoredAddress::isWhitelisted)
                .orElse(false);
        boolean blacklisted = barricade.getStoredUserManager()
                .getOptional(nickName)
                .map(StoredUser::isBlacklisted)
                .orElse(false)
                || barricade.getAddressManager()
                .getOptional(inetAddress.getHostAddress())
                .map(StoredAddress::isBlacklisted)
                .orElse(false);
        return new ConnectingUser(nickName, inetAddress, whitelisted, blacklisted);
    }

    public void update(ConnectingUser connectingUser) {
        barricade.getDataProvider().updateConnectingUser(connectingUser);
    }

}
