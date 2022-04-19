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
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import xyz.yawek.barricade.Barricade;
import xyz.yawek.barricade.listener.handler.PreLoginHandler;
import xyz.yawek.barricade.user.ConnectingUser;

public class PreLoginListener extends PreLoginHandler {

    public PreLoginListener(Barricade barricade) {
        super(barricade);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public EventTask onPlayerPreLogin(PreLoginEvent e) {
        return EventTask.async(() -> {
            ConnectingUser connectingUser = barricade.getConnectingUserManager().create(
                    e.getUsername(), e.getConnection().getRemoteAddress().getAddress());
            e.setResult(resolve(connectingUser));
        });
    }

}
