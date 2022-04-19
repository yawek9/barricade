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

import xyz.yawek.barricade.user.ConnectingUser;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AccountLimitManager {

    private final Map<String, Integer> addressesMap = new ConcurrentHashMap<>();

    public void increment(ConnectingUser connectingUser) {
        String address = connectingUser.getStringAddress();
        addressesMap.put(address, addressesMap.getOrDefault(address, 0) + 1);
    }

    public void decrement(ConnectingUser connectingUser) {
        String address = connectingUser.getStringAddress();
        if (!addressesMap.containsKey(address)) return;
        if (addressesMap.get(address) == 0) {
            addressesMap.remove(address);
            return;
        }
        addressesMap.put(address, addressesMap.get(address) - 1);
    }

    public int numOfConnections(ConnectingUser connectingUser) {
        return addressesMap.getOrDefault(connectingUser.getStringAddress(), 0);
    }

}
