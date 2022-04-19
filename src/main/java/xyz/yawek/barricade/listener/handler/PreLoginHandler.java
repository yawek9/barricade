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

package xyz.yawek.barricade.listener.handler;

import com.velocitypowered.api.event.connection.PreLoginEvent;
import net.kyori.adventure.text.Component;
import xyz.yawek.barricade.Barricade;
import xyz.yawek.barricade.check.*;
import xyz.yawek.barricade.user.ConnectingUser;

import java.util.*;

public abstract class PreLoginHandler {

    protected final Barricade barricade;

    private final Set<AbstractCheck> checkSet = new TreeSet<>();

    public PreLoginHandler(Barricade barricade) {
        this.barricade = barricade;

        checkSet.add(new NicknameCheck(this.barricade));
        checkSet.add(new AccountLimitCheck(this.barricade));
        checkSet.add(new ASNCheck(this.barricade));
        checkSet.add(new BlacklistCheck(this.barricade));
        checkSet.add(new GeoCheck(this.barricade));
        checkSet.add(new RateLimitCheck(this.barricade));
    }

    /**
     * Updates stored user data and runs checks.
     *
     * @param connectingUser ConnectingUser instance
     * @return result of the ran checks
     */
    public PreLoginEvent.PreLoginComponentResult resolve(ConnectingUser connectingUser) {
        barricade.getConnectingUserManager().update(connectingUser);
        if (connectingUser.isWhitelisted())
            return PreLoginEvent.PreLoginComponentResult.allowed();
        for (AbstractCheck abstractCheck : checkSet) {
            Optional<Component> resultComponent = abstractCheck.check(connectingUser);
            if (resultComponent.isPresent())
                return PreLoginEvent.PreLoginComponentResult.denied(resultComponent.get());
        }
        return PreLoginEvent.PreLoginComponentResult.allowed();
    }

}
