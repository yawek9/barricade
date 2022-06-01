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

package xyz.yawek.barricade;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;
import xyz.yawek.barricade.command.CommandHandler;
import xyz.yawek.barricade.config.Config;
import xyz.yawek.barricade.config.ConfigProvider;
import xyz.yawek.barricade.data.storage.DataProvider;
import xyz.yawek.barricade.data.geo.GeoDataProvider;
import xyz.yawek.barricade.listener.*;
import xyz.yawek.barricade.manager.*;
import xyz.yawek.barricade.manager.queue.ServerQueueManager;
import xyz.yawek.barricade.task.TaskLoader;
import xyz.yawek.barricade.util.LogUtils;

import java.nio.file.Path;

@Plugin(id = "barricade",
        name = "Barricade",
        version = "1.0.1",
        url = "https://github.com/yawek9/barricade",
        description = "Plugin that helps protecting the proxy against bots and unwanted players.",
        authors = {"yawek9"})
public class Barricade {

    private static Barricade barricade;

    private final ProxyServer server;
    private final Logger logger;
    private final Path dataDirectory;
    private TaskLoader taskLoader;
    private ConfigProvider configProvider;
    private Config config;
    private DataProvider dataProvider;
    private GeoDataProvider geoDataProvider;
    private ConnectingUserManager connectingUserManager;
    private AddressManager addressManager;
    private StoredUserManager storedUserManager;
    private OnlineUserManager onlineUserManager;
    private AccountLimitManager accountLimitManager;
    private RateLimitManager rateLimitManager;
    private ServerQueueManager serverQueueManager;

    @Inject
    public Barricade(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onInitialize(ProxyInitializeEvent e) {
        barricade = this;

        this.configProvider = new ConfigProvider(this);
        this.config = new Config(this.configProvider);
        this.dataProvider = new DataProvider(this);
        this.geoDataProvider = new GeoDataProvider(this);
        this.addressManager = new AddressManager(this);
        this.connectingUserManager = new ConnectingUserManager(this);
        this.storedUserManager = new StoredUserManager(this);
        this.onlineUserManager = new OnlineUserManager(barricade);
        this.accountLimitManager = new AccountLimitManager();
        this.rateLimitManager = new RateLimitManager();
        this.serverQueueManager = new ServerQueueManager(barricade);

        EventManager eventManager = server.getEventManager();
        eventManager.register(this, new PreLoginListener(this));
        eventManager.register(this, new PostLoginListener(this));
        eventManager.register(this, new ServerPreConnectListener(this));
        eventManager.register(this, new DisconnectListener(this));
        eventManager.register(this, new PlayerChatListener(this));

        CommandManager commandManager = server.getCommandManager();
        commandManager.register(commandManager.metaBuilder("barricade").build(),
                new CommandHandler(this));

        this.taskLoader = new TaskLoader(this);
        taskLoader.loadTasks();

        LogUtils.info("Plugin has been enabled successfully!");
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onShutdown(ProxyShutdownEvent e) {
        dataProvider.shutdown();
    }

    public void reload() {
        configProvider.loadConfig();
        dataProvider.shutdown();
        dataProvider = new DataProvider(barricade);
        taskLoader.loadTasks();
    }

    public ProxyServer getServer() {
        return server;
    }

    public Logger getLogger() {
        return logger;
    }

    public Path getDataDirectory() {
        return dataDirectory;
    }

    public static Barricade getBarricade() {
        return barricade;
    }

    public Config getConfig() {
        return config;
    }

    public DataProvider getDataProvider() {
        return dataProvider;
    }

    public GeoDataProvider getGeoDataProvider() {
        return geoDataProvider;
    }

    public ConnectingUserManager getConnectingUserManager() {
        return connectingUserManager;
    }

    public AddressManager getAddressManager() {
        return addressManager;
    }

    public StoredUserManager getStoredUserManager() {
        return storedUserManager;
    }

    public OnlineUserManager getOnlineUserManager() {
        return onlineUserManager;
    }

    public AccountLimitManager getAccountLimitManager() {
        return accountLimitManager;
    }

    public RateLimitManager getRateLimitManager() {
        return rateLimitManager;
    }

    public ServerQueueManager getServerQueueManager() {
        return serverQueueManager;
    }

}
