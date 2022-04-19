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

package xyz.yawek.barricade.check;

import net.kyori.adventure.text.Component;
import xyz.yawek.barricade.Barricade;
import xyz.yawek.barricade.user.ConnectingUser;

import java.util.Optional;

public class GeoCheck extends AbstractCheck {

    public GeoCheck(Barricade barricade) {
        super(barricade, barricade.getConfig().geoipPriority());
    }

    @Override
    public Optional<Component> check(ConnectingUser connectingUser) {
        Optional<String> countryCode =
                barricade.getGeoDataProvider().getCountryCode(connectingUser.getInetAddress());
        boolean configContainsCountry = countryCode.isPresent()
                        && barricade.getConfig().geoipCountries().contains(countryCode.get());
        if (barricade.getConfig().geoipBlacklist() && configContainsCountry) {
            return Optional.of(barricade.getConfig().countryBlocked());
        } else if (!barricade.getConfig().geoipBlacklist() && !configContainsCountry) {
            return Optional.of(barricade.getConfig().countryBlocked());
        }
        return Optional.empty();
    }

}
