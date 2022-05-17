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

package xyz.yawek.barricade.listener.handler.serverpreconnect;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import xyz.yawek.barricade.Barricade;
import xyz.yawek.barricade.config.Config;
import xyz.yawek.barricade.manager.queue.QueuedPlayer;
import xyz.yawek.barricade.manager.queue.ServerQueueManager;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public abstract class ServerPreConnectHandler {

    protected final Barricade barricade;

    protected ServerPreConnectHandler(Barricade barricade) {
        this.barricade = barricade;
    }

    /**
     * Checks if player can connect to the server based on queue settings ({@link #joinAttempt(String, UUID)}).
     * Sends messages to other players in the queue if their position has changed.
     *
     * @param serverName name of the server the player is trying to connect to
     * @param playerUuid player's uuid
     * @return true if the player is allowed to connect, false if not
     */
    public boolean resolve(String serverName, UUID playerUuid) {
        ProxyServer server = barricade.getServer();
        Config config = barricade.getConfig();
        ServerQueueManager queueManager = barricade.getServerQueueManager();
        Player player = server.getPlayer(playerUuid).orElse(null);
        if (player == null) return false;
        if (player.hasPermission("barricade.exemptqueue")) return true;
        switch (joinAttempt(serverName, playerUuid)) {
            case QUEUE_DISABLED -> { return true; }
            case NOT_QUEUED -> {
                player.sendMessage(config.queuePosition(
                        queueManager.addToQueue(serverName, playerUuid), serverName));
                return false;
            }
            case ALREADY_QUEUED -> {
                player.sendMessage(config.queuedAlready(serverName));
                return false;
            }
            case FIRST_IN_QUEUE -> {
                queueManager.removeFromQueues(playerUuid);
                player.sendMessage(config.queueMoved(serverName));
                Set<QueuedPlayer> queuedPlayers = queueManager.getAllQueued(serverName);
                int queuePosition = queuedPlayers.size();
                for (QueuedPlayer queuedPlayer : queuedPlayers) {
                    Optional<Player> playerOptional = server.getPlayer(queuedPlayer.getUuid());
                    if (playerOptional.isEmpty()) continue;
                    playerOptional.get().sendMessage(config.queuePosition(queuePosition, serverName));
                    queuePosition--;
                }
                return true;
            }
        }
        return false;
    }

    private JoinAttemptResult joinAttempt(String serverName, UUID playerUuid) {
        Config config = barricade.getConfig();
        if (!config.serverQueues().contains(serverName))
            return JoinAttemptResult.QUEUE_DISABLED;
        Optional<QueuedPlayer> playerOptional = barricade.getServerQueueManager()
                .getIfQueued(serverName, playerUuid);
        if (playerOptional.isPresent()) {
            if (playerOptional.get().isAllowedToJoin()) {
                return JoinAttemptResult.FIRST_IN_QUEUE;
            } else {
                return JoinAttemptResult.ALREADY_QUEUED;
            }
        } else {
            return JoinAttemptResult.NOT_QUEUED;
        }
    }

}
