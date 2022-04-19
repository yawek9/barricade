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

package xyz.yawek.barricade.task;

import com.velocitypowered.api.scheduler.ScheduledTask;
import com.velocitypowered.api.scheduler.Scheduler;
import xyz.yawek.barricade.Barricade;
import xyz.yawek.barricade.config.Config;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

public class TaskLoader {

    private final Barricade barricade;
    private final Set<ScheduledTask> tasks = new HashSet<>();

    public TaskLoader(Barricade barricade) {
        this.barricade = barricade;
    }

    public void loadTasks() {
        Config config = barricade.getConfig();
        Scheduler scheduler = barricade.getServer().getScheduler();
        tasks.forEach(ScheduledTask::cancel);
        tasks.add(scheduler.buildTask(barricade, new RateLimitResetTask(barricade))
                .repeat(Duration.ofSeconds(1))
                .schedule());
        config.serverQueues().forEach(serverName -> tasks.add(scheduler.buildTask(
                barricade, new ServerQueueTask(barricade, serverName))
                .repeat(Duration.ofMillis(config.serverQueuePeriod(serverName).orElse(1000)))
                .schedule()));
    }

}
