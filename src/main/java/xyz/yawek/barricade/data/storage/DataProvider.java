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

package xyz.yawek.barricade.data.storage;

import xyz.yawek.barricade.Barricade;
import xyz.yawek.barricade.data.storage.address.AddressDataAccess;
import xyz.yawek.barricade.data.storage.user.UserDataAccess;
import xyz.yawek.barricade.user.ConnectingUser;
import xyz.yawek.barricade.user.StoredAddress;
import xyz.yawek.barricade.user.StoredUser;

import java.util.Optional;
import java.util.Set;

public class DataProvider {

    private final DatabaseConnector databaseConnector;
    private final AddressDataAccess addressDataAccess;
    private final UserDataAccess userDataAccess;

    public DataProvider(Barricade barricade) {
        this.databaseConnector = new DatabaseConnector(barricade);
        this.addressDataAccess = databaseConnector.getAddressDataAccess();
        this.userDataAccess = databaseConnector.getUserDataAccess();
    }

    public void shutdown() {
        databaseConnector.closeConnection();
    }

    public void updateConnectingUser(ConnectingUser connectingUser) {
        String address = connectingUser.getStringAddress();
        String nickname = connectingUser.getNickname();
        addressDataAccess.addNickname(address, nickname);
        userDataAccess.addAddress(nickname, address);
    }

    public boolean isWhitelisted(ConnectingUser connectingUser) {
        return addressDataAccess.isWhitelisted(connectingUser.getInetAddress().toString())
                || userDataAccess.isWhitelisted(connectingUser.getNickname());
    }

    public boolean isWhitelisted(StoredUser storedUser) {
        return userDataAccess.isWhitelisted(storedUser.getNickname());
    }

    public Optional<StoredAddress> getStoredAddress(String address) {
        Optional<Set<String>> nicknamesOptional = addressDataAccess.getNicknames(address);
        if (nicknamesOptional.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(new StoredAddress(address, nicknamesOptional.get(),
                addressDataAccess.isWhitelisted(address), addressDataAccess.isBlacklisted(address)));
    }

    public Optional<StoredUser> getStoredUser(String nickname) {
        Optional<Set<String>> addressesOptional = userDataAccess.getAddresses(nickname);
        if (addressesOptional.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(new StoredUser(nickname, addressesOptional.get(),
                userDataAccess.isWhitelisted(nickname), userDataAccess.isBlacklisted(nickname)));
    }

    public void updateStoredAddress(StoredAddress storedAddress) {
        String address = storedAddress.getAddress();
        storedAddress.getNicknames().forEach(s -> addressDataAccess.addNickname(address, s));
        addressDataAccess.setWhitelisted(address, storedAddress.isWhitelisted());
        addressDataAccess.setBlacklisted(address, storedAddress.isBlacklisted());
    }

    public void updateStoredUser(StoredUser storedUser) {
        String nickname = storedUser.getNickname();
        storedUser.getAddresses().forEach(s -> userDataAccess.addAddress(nickname, s));
        userDataAccess.setWhitelisted(nickname, storedUser.isWhitelisted());
        userDataAccess.setBlacklisted(nickname, storedUser.isBlacklisted());
    }

}
