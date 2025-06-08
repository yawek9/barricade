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

import com.zaxxer.hikari.HikariDataSource;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import xyz.yawek.barricade.Barricade;
import xyz.yawek.barricade.config.Config;
import xyz.yawek.barricade.data.storage.address.AddressDataAccess;
import xyz.yawek.barricade.data.storage.address.MySQLAddressDataAccess;
import xyz.yawek.barricade.data.storage.address.SQLiteAddressDataAccess;
import xyz.yawek.barricade.data.storage.user.MySQLUserDataAccess;
import xyz.yawek.barricade.data.storage.user.SQLiteUserDataAccess;
import xyz.yawek.barricade.data.storage.user.UserDataAccess;
import xyz.yawek.barricade.util.LogUtils;

public class DatabaseConnector {

    private final Barricade barricade;
    private final AddressDataAccess addressDataAccess;
    private final UserDataAccess userDataAccess;
    private Connection connection;
    private HikariDataSource hikari;

    public DatabaseConnector(Barricade barricade) {
        this.barricade = barricade;

        if (barricade.getConfig().useMySQL()) {
            openMySQLConnection();
            this.addressDataAccess = new MySQLAddressDataAccess(hikari);
            this.userDataAccess = new MySQLUserDataAccess(hikari);
        } else {
            openSQLiteConnection();
            this.addressDataAccess = new SQLiteAddressDataAccess(connection);
            this.userDataAccess = new SQLiteUserDataAccess(connection);
        }
    }

    private void openMySQLConnection() {
        Config config = barricade.getConfig();

        hikari = new HikariDataSource();
        hikari.setDataSourceClassName("com.mysql.cj.jdbc.MysqlDataSource");
        hikari.addDataSourceProperty("serverName", config.databaseAddress());
        hikari.addDataSourceProperty("port", config.databasePort());
        hikari.addDataSourceProperty("databaseName", config.databaseName());
        hikari.addDataSourceProperty("user", config.databaseUser());
        hikari.addDataSourceProperty("password", config.databasePassword());
        hikari.setPoolName("barricade-hikari");

        try {
            Flyway.configure(barricade.getClass().getClassLoader())
                .dataSource(hikari)
                .locations("classpath:db/migration-mysql")
                .baselineOnMigrate(true)
                .load()
                .migrate();
            LogUtils.infoDataAccess("Database connection has been initialized successfully.");
        } catch (FlywayException e) {
            LogUtils.errorDataAccess("Database connection could not be initialized.");
            e.printStackTrace();
        }
    }

    private void openSQLiteConnection() {
        File dataDir = barricade.getDataDirectory().toFile();
        if (!dataDir.exists()) dataDir.mkdirs();

        File databaseFile = new File(dataDir, "data.db");
        if (!databaseFile.exists()) {
            try {
                databaseFile.createNewFile();
            } catch (IOException e) {
                LogUtils.errorDataAccess("Database file could not be created.");
                e.printStackTrace();
            }
        }

        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + databaseFile);

            Flyway.configure(barricade.getClass().getClassLoader())
                .dataSource("jdbc:sqlite:"
                    + databaseFile.getAbsolutePath(), null, null)
                .locations("classpath:db/migration-sqlite")
                .baselineOnMigrate(true)
                .load()
                .migrate();
            LogUtils.infoDataAccess("Database connection has been initialized successfully.");
        } catch (ClassNotFoundException | SQLException | FlywayException e) {
            LogUtils.errorDataAccess("Database connection could not be initialized.");
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        if (barricade.getConfig().useMySQL()) {
            hikari.close();
            LogUtils.infoDataAccess("Database connection has been closed.");
        } else {
            try {
                connection.close();
                LogUtils.infoDataAccess("Database connection has been closed.");
            } catch (SQLException e) {
                LogUtils.errorDataAccess("An error occurred while trying " +
                        "to close database connection.");
                e.printStackTrace();
            }
        }
    }

    public AddressDataAccess getAddressDataAccess() {
        return addressDataAccess;
    }

    public UserDataAccess getUserDataAccess() {
        return userDataAccess;
    }

}
