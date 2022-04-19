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

package xyz.yawek.barricade.listener.handler.playerchat;

import xyz.yawek.barricade.Barricade;

import java.time.Duration;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public abstract class PlayerChatHandler {

    protected final Barricade barricade;

    private final Set<CachedMessage> cachedMessages = ConcurrentHashMap.newKeySet();

    public PlayerChatHandler(Barricade barricade) {
        this.barricade = barricade;
    }

    /**
     * Checks for message similarity in the last time and possibly kicks player.
     *
     * @param uuid player's UUID
     * @param message message sent by player
     */
    public void resolve(UUID uuid, String message) {
        if (!barricade.getConfig().antiSpamEnabled()) return;
        CachedMessage cachedMessage = new CachedMessage(uuid, message);
        if (!kickIfSuspected(cachedMessage)) {
            cachedMessages.add(cachedMessage);
            barricade.getServer().getScheduler()
                    .buildTask(barricade, () -> cachedMessages.remove(cachedMessage))
                    .delay(Duration.ofSeconds(10))
                    .schedule();
        }
    }

    /**
     * Kicks player if there is saved message that content is equal and timestamp is before threshold.
     *
     * @param cachedMessage PlayerMessage instance
     * @return true if player was kicked, false otherwise
     */
    private boolean kickIfSuspected(CachedMessage cachedMessage) {
        boolean kick = false;
        for (CachedMessage setMessage : cachedMessages) {
            if (!cachedMessage.getMessage().equals(setMessage.getMessage())
                    || cachedMessage.getTimestamp() - setMessage.getTimestamp()
                    > barricade.getConfig().chatThreshold()) {
                continue;
            }
            barricade.getServer().getPlayer(setMessage.getPlayerUuid())
                    .ifPresent(player -> player.disconnect(barricade.getConfig().spamBotSuspected()));
            kick = true;
        }
        if (kick) barricade.getServer().getPlayer(cachedMessage.getPlayerUuid())
                .ifPresent(player -> player.disconnect(barricade.getConfig().spamBotSuspected()));
        return kick;
    }

}
