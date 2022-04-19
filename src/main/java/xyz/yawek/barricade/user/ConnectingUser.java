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

package xyz.yawek.barricade.user;

import java.net.InetAddress;

public class ConnectingUser {

    private final String nickname;
    private final InetAddress inetAddress;
    private final boolean whitelisted;
    private final boolean blacklisted;

    public ConnectingUser(String nickname, InetAddress inetAddress,
                          boolean isWhitelisted, boolean blacklisted) {
        this.nickname = nickname;
        this.inetAddress = inetAddress;
        this.whitelisted = isWhitelisted;
        this.blacklisted = blacklisted;
    }

    public String getStringAddress() {
        return inetAddress.getHostAddress();
    }

    public String getNickname() {
        return nickname;
    }

    public InetAddress getInetAddress() {
        return inetAddress;
    }

    public boolean isWhitelisted() {
        return whitelisted;
    }

    public boolean isBlacklisted() {
        return blacklisted;
    }

}
