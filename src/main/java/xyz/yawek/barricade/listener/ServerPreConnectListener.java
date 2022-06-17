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

package xyz.yawek.barricade.listener;

import com.velocitypowered.api.event.EventTask;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import xyz.yawek.barricade.Barricade;
import xyz.yawek.barricade.listener.handler.serverpreconnect.ServerPreConnectHandler;

public class ServerPreConnectListener extends ServerPreConnectHandler {

    public ServerPreConnectListener(Barricade barricade) {
        super(barricade);
    }

    @SuppressWarnings("unused")
    @Subscribe(order = PostOrder.LAST)
    public EventTask onServerPreConnected(ServerPreConnectEvent e) {
        return EventTask.async(() -> {
            RegisteredServer server = e.getOriginalServer();
            e.setResult(resolve(server.getServerInfo().getName(),
                    e.getPlayer().getUniqueId())
                    ? ServerPreConnectEvent.ServerResult.allowed(server)
                    : ServerPreConnectEvent.ServerResult.denied());
        });
    }

}
