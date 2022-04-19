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

package xyz.yawek.barricade.manager.queue;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import xyz.yawek.barricade.Barricade;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public class ServerQueueManager {

    private final Barricade barricade;

    private final ConcurrentLinkedQueue<QueuedPlayer> queuedPlayers = new ConcurrentLinkedQueue<>();

    public ServerQueueManager(Barricade barricade) {
        this.barricade = barricade;
    }

    /**
     * Adds the player to the queue, removing if they are in another queue.
     *
     * @param serverName name of the server
     * @param playerUUID player's UUID
     * @return player's position in the queue
     */
    public int addToQueue(String serverName, UUID playerUUID) {
        removeFromQueues(playerUUID);
        queuedPlayers.add(new QueuedPlayer(playerUUID, serverName));
        return queuedPlayers.size();
    }

    public Optional<QueuedPlayer> getIfQueued(String serverName, UUID playerUuid) {
        return queuedPlayers.stream()
                .filter(queuedPlayer -> queuedPlayer.getUuid().equals(playerUuid)
                        && queuedPlayer.getServerName().equals(serverName))
                .findAny();
    }

    public void removeFromQueues(UUID playerUUID) {
        queuedPlayers.removeIf(queuedPlayer ->
                queuedPlayer.getUuid().equals(playerUUID));
    }

    public Set<QueuedPlayer> getAllQueued(String serverName) {
        return queuedPlayers.stream()
                .filter(queuedPlayer -> queuedPlayer.getServerName().equals(serverName))
                .collect(Collectors.toSet());
    }

    /**
     * Retrieves first player in the queue for specified server and moves them.
     */
    public void processQueues(String serverName) {
        queuedPlayers.stream()
                .filter(queuedPlayer -> queuedPlayer.getServerName().equals(serverName))
                .findFirst()
                .ifPresent(queuedPlayer -> {
                    queuedPlayer.setAllowedToJoin(true);
                    Optional<Player> playerOptional =
                            barricade.getServer().getPlayer(queuedPlayer.getUuid());
                    if (playerOptional.isEmpty()) {
                        removeFromQueues(queuedPlayer.getUuid());
                        return;
                    }
                    Optional<RegisteredServer> serverOptional =
                            barricade.getServer().getServer(serverName);
                    if (serverOptional.isEmpty()) return;
                    playerOptional.get().createConnectionRequest(serverOptional.get()).connect()
                            .thenRun(() -> removeFromQueues(queuedPlayer.getUuid()));
                });
    }

}
