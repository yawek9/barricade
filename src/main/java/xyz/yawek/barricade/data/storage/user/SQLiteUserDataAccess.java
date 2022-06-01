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

package xyz.yawek.barricade.data.storage.user;

import xyz.yawek.barricade.data.storage.SQLiteDataAccess;
import xyz.yawek.barricade.util.LogUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class SQLiteUserDataAccess extends SQLiteDataAccess implements UserDataAccess {

    public SQLiteUserDataAccess(Connection connection) {
        super(connection);
    }

    @Override
    public Optional<Set<String>> getAddresses(String nickname) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("""
                          SELECT addresses FROM users WHERE nickname = ?
                        """)) {
            preparedStatement.setString(1, nickname);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(Arrays.stream(resultSet.getString(1).split(","))
                        .collect(Collectors.toSet()));
            }
        } catch (SQLException e) {
            LogUtils.errorDataAccess("Unable to get address " +
                    "for nickname '{}'.", nickname);
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public void addAddress(String nickname, String address) {
        Set<String> addresses = new HashSet<>();
        Optional<Set<String>> addressesOptional = getAddresses(nickname);
        addressesOptional.ifPresent(addresses::addAll);
        addresses.add(address);

        try (PreparedStatement preparedStatement = connection.prepareStatement("""
                          INSERT INTO users (nickname, addresses) VALUES (?, ?)
                          ON CONFLICT(nickname) DO UPDATE SET
                          nickname = ?,
                          addresses = ?
                        """)) {
            preparedStatement.setString(1, nickname);
            preparedStatement.setString(2, String.join(",", addresses));
            preparedStatement.setString(3, nickname);
            preparedStatement.setString(4, String.join(",", addresses));
            preparedStatement.execute();
        } catch (SQLException e) {
            LogUtils.errorDataAccess("Unable to add address '{}' " +
                    "for nickname '{}'.", address, nickname);
            e.printStackTrace();
        }
    }

    @Override
    public boolean isWhitelisted(String nickname) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("""
                          SELECT whitelisted FROM users where nickname = ?
                        """)) {
            preparedStatement.setString(1, nickname);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getBoolean(1);
            }
        } catch (SQLException e) {
            LogUtils.errorDataAccess("Unable to check " +
                    "if nickname '{}' is whitelisted.", nickname);
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void setWhitelisted(String nickname, boolean whitelisted) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("""
                          UPDATE users SET whitelisted = ? WHERE nickname = ?
                        """)) {
            preparedStatement.setBoolean(1, whitelisted);
            preparedStatement.setString(2, nickname);
            preparedStatement.execute();
        } catch (SQLException e) {
            LogUtils.errorDataAccess("Unable to set whitelisted '{}' " +
                    "for nickname '{}'.", String.valueOf(whitelisted), nickname);
            e.printStackTrace();
        }
    }

    @Override
    public boolean isBlacklisted(String nickname) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("""
                          SELECT blacklisted FROM users where nickname = ?
                        """)) {
            preparedStatement.setString(1, nickname);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getBoolean(1);
            }
        } catch (SQLException e) {
            LogUtils.errorDataAccess("Unable to check " +
                    "if nickname '{}' is blacklisted.", nickname);
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void setBlacklisted(String nickname, boolean blacklisted) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("""
                          UPDATE users SET blacklisted = ? WHERE nickname = ?
                        """)) {
            preparedStatement.setBoolean(1, blacklisted);
            preparedStatement.setString(2, nickname);
            preparedStatement.execute();
        } catch (SQLException e) {
            LogUtils.errorDataAccess("Unable to set blacklisted '{}' " +
                    "for nickname '{}'.", String.valueOf(blacklisted), nickname);
            e.printStackTrace();
        }
    }

}
