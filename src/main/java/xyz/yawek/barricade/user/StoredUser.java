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

import java.util.Set;

public class StoredUser {

    private final String nickname;
    private final Set<String> addresses;
    private boolean whitelisted = false;
    private boolean blacklisted = false;

    public StoredUser(String nickname, Set<String> addresses,
                      boolean whitelisted, boolean blacklisted) {
        this.nickname = nickname;
        this.addresses = addresses;
        this.whitelisted = whitelisted;
        this.blacklisted = blacklisted;
    }

    public String getNickname() {
        return nickname;
    }

    public Set<String> getAddresses() {
        return addresses;
    }

    public boolean isWhitelisted() {
        return whitelisted;
    }

    public void setBlacklisted(boolean blacklisted) {
        this.blacklisted = blacklisted;
    }

    public boolean isBlacklisted() {
        return blacklisted;
    }

    public void setWhitelisted(boolean whitelisted) {
        this.whitelisted = whitelisted;
    }

}
