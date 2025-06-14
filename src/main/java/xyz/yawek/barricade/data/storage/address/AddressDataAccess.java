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

package xyz.yawek.barricade.data.storage.address;

import java.util.Optional;
import java.util.Set;
import org.jetbrains.annotations.Nullable;
import xyz.yawek.barricade.user.StoredAddress;

public interface AddressDataAccess {

    Optional<StoredAddress> getAddress(String address);

    Optional<Set<String>> getNicknames(String address);

    void addNickname(String address, @Nullable String nickname,
        boolean whitelisted, boolean blacklisted);

    boolean isWhitelisted(String address);

    void setWhitelisted(String address, boolean whitelisted);

    boolean isBlacklisted(String address);

    void setBlacklisted(String address, boolean blacklisted);

}
