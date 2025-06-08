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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.jetbrains.annotations.Nullable;
import xyz.yawek.barricade.data.storage.SQLiteDataAccess;
import xyz.yawek.barricade.user.StoredAddress;
import xyz.yawek.barricade.util.LogUtils;

public class SQLiteAddressDataAccess extends SQLiteDataAccess implements AddressDataAccess {

    public SQLiteAddressDataAccess(Connection connection) {
        super(connection);
    }

    @Override
    public Optional<StoredAddress> getAddress(String address) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("""
                          SELECT address, nicknames, whitelisted, blacklisted FROM addresses
                          WHERE address = ?
                        """)) {
            preparedStatement.setString(1, address);
            ResultSet resultSet = preparedStatement.executeQuery();
            String nicknamesString = resultSet.getString(2);
            if (resultSet.next()) {
                return Optional.of(
                    new StoredAddress(
                        resultSet.getString(1),
                        nicknamesString != null ? Arrays.stream(nicknamesString.split(","))
                            .collect(Collectors.toSet()) : Collections.emptySet(),
                        resultSet.getBoolean(3),
                        resultSet.getBoolean(4)
                    )
                );
            }
        } catch (SQLException e) {
            LogUtils.errorDataAccess("Unable to get address '{}'.", address);
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<Set<String>> getNicknames(String address) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("""
                          SELECT nicknames FROM addresses WHERE address = ?
                        """)) {
            preparedStatement.setString(1, address);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String nicknamesString = resultSet.getString(1);
                if (nicknamesString == null) {
                    return Optional.empty();
                }
                return Optional.of(Arrays.stream(nicknamesString.split(","))
                        .collect(Collectors.toSet()));
            }
        } catch (SQLException e) {
            LogUtils.errorDataAccess("Unable to get nicknames " +
                    "for address '{}'.", address);
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public void addNickname(String address, @Nullable String nickname, boolean whitelisted, boolean blacklisted) {
        Set<String> nicknames = new HashSet<>();
        Optional<Set<String>> nicknamesOptional = getNicknames(address);
        nicknamesOptional.ifPresent(nicknames::addAll);
        if (nickname != null) {
            nicknames.add(nickname);
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement("""
                          INSERT INTO addresses (address, nicknames, whitelisted, blacklisted)
                          VALUES (?, ?, ?, ?)
                          ON CONFLICT(address) DO UPDATE SET
                          address = ?,
                          nicknames = ?,
                          whitelisted = ?,
                          blacklisted = ?
                        """)) {
            preparedStatement.setString(1, address);
            preparedStatement.setString(2, !nicknames.isEmpty()
                ? String.join(",", nicknames) : null);
            preparedStatement.setBoolean(3, whitelisted);
            preparedStatement.setBoolean(4, blacklisted);
            preparedStatement.setString(5, address);
            preparedStatement.setString(6, !nicknames.isEmpty()
                ? String.join(",", nicknames) : null);
            preparedStatement.setBoolean(7, whitelisted);
            preparedStatement.setBoolean(8, blacklisted);
            preparedStatement.execute();
        } catch (SQLException e) {
            LogUtils.errorDataAccess("Unable to add nickname '{}' " +
                    "for address '{}'.", nickname, address);
            e.printStackTrace();
        }
    }

    @Override
    public boolean isWhitelisted(String address) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("""
                          SELECT whitelisted FROM addresses where address = ?
                        """)) {
            preparedStatement.setString(1, address);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getBoolean(1);
            }
        } catch (SQLException e) {
            LogUtils.errorDataAccess("Unable to check " +
                    "if address '{}' is whitelisted.", address);
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void setWhitelisted(String address, boolean whitelisted) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("""
                          UPDATE addresses SET whitelisted = ? WHERE address = ?
                        """)) {
            preparedStatement.setBoolean(1, whitelisted);
            preparedStatement.setString(2, address);
            preparedStatement.execute();
        } catch (SQLException e) {
            LogUtils.errorDataAccess("Unable to set whitelisted '{}' " +
                    "for address '{}'.", String.valueOf(whitelisted), address);
            e.printStackTrace();
        }
    }

    @Override
    public boolean isBlacklisted(String address) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("""
                          SELECT blacklisted FROM addresses where address = ?
                        """)) {
            preparedStatement.setString(1, address);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getBoolean(1);
            }
        } catch (SQLException e) {
            LogUtils.errorDataAccess("Unable to check " +
                    "if address '{}' is blacklisted.", address);
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void setBlacklisted(String address, boolean blacklisted) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("""
                          UPDATE addresses SET blacklisted = ? WHERE address = ?
                        """)) {
            preparedStatement.setBoolean(1, blacklisted);
            preparedStatement.setString(2, address);
            preparedStatement.execute();
        } catch (SQLException e) {
            LogUtils.errorDataAccess("Unable to set blacklisted '{}' " +
                    "for address '{}'.", String.valueOf(blacklisted), address);
            e.printStackTrace();
        }
    }

}
