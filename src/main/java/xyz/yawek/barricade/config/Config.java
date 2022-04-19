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

package xyz.yawek.barricade.config;

import net.kyori.adventure.text.Component;

import java.util.*;

public class Config {

    private final ConfigProvider configProvider;
    private final ConfigUtils configUtils;

    public Config(ConfigProvider configProvider) {
        this.configProvider = configProvider;

        configUtils = new ConfigUtils(configProvider);
    }

    public boolean useMySQL() {
        return configProvider.getString("data.database") != null
                && configProvider.getString("data.database").equalsIgnoreCase("mysql");
    }

    public String databaseAddress() {
        return configProvider.getString("data.mysql.address");
    }

    public String databasePort() {
        return configProvider.getString("data.mysql.port");
    }

    public String databaseName() {
        return configProvider.getString("data.mysql.database-name");
    }

    public String databaseUser() {
        return configProvider.getString("data.mysql.user");
    }

    public String databasePassword() {
        return configProvider.getString("data.mysql.password");
    }

    public String geoipLicense() {
        return configProvider.getString("data.geoip-license-key");
    }

    public int nicknameBlockPriority() {
        return configProvider.getInt("nickname-block.priority");
    }

    public List<String> restrictedNicknames() {
        List<String> blockedNicknames =
                configProvider.getStringList("nickname-block.block-containing");
        return blockedNicknames != null
                ? blockedNicknames : Collections.emptyList();
    }

    public int accountLimitPriority() {
        return configProvider.getInt("account-limit.priority");
    }

    public int perIpLimit() {
        return configProvider.getInt("account-limit.per-ip-limit");
    }

    public int asnBlockPriority() {
        return configProvider.getInt("asn-block.priority");
    }

    public List<String> asnBlockList() {
        List<String> asnList = configProvider.getStringList("asn-block.list");
        return asnList != null ? asnList : Collections.emptyList();
    }

    public int blacklistPriority() {
        return configProvider.getInt("blacklist.priority");
    }

    public int geoipPriority() {
        return configProvider.getInt("geoip.priority");
    }

    public boolean geoipBlacklist() {
        return configProvider.getBoolean("geoip.blacklist-mode");
    }

    public List<String> geoipCountries() {
        List<String> countries = configProvider.getStringList("geoip.countries");
        return countries != null ? countries : Collections.emptyList();
    }

    public int rateLimitPriority() {
        return configProvider.getInt("rate-limit.priority");
    }

    public int connectionsPerSecond() {
        return configProvider.getInt("rate-limit.connections-per-second");
    }

    public boolean antiSpamEnabled() {
        return configProvider.getBoolean("anti-spam.enabled");
    }

    public int chatThreshold() {
        return configProvider.getInt("anti-spam.chat-threshold");
    }

    public Set<String> serverQueues() {
        Map<String, ?> serversMap = configProvider.getMap("server-queue.servers");
        return serversMap != null ? serversMap.keySet() : Collections.emptySet();
    }

    public Optional<Integer> serverQueuePeriod(String serverName) {
        return serverQueues().contains(serverName) ?
                Optional.of(configProvider.getInt("server-queue.servers."
                        + serverName + ".period")) : Optional.empty();
    }

    public Component ipLimit() {
        return configUtils.noPrefixMessage("messages.kick.ip-limit");
    }

    public Component restrictedNickname() {
        return configUtils.noPrefixMessage("messages.kick.restricted-nickname");
    }

    public Component asnBlocked() {
        return configUtils.noPrefixMessage("messages.kick.asn-blocked");
    }

    public Component rateLimit() {
        return configUtils.noPrefixMessage("messages.kick.rate-limit");
    }

    public Component blacklisted() {
        return configUtils.noPrefixMessage("messages.kick.blacklisted");
    }

    public Component countryBlocked() {
        return configUtils.noPrefixMessage("messages.kick.country-blocked");
    }

    public Component spamBotSuspected() {
        return configUtils.noPrefixMessage("messages.kick.spam-bot-suspect");
    }

    public Component noPermission() {
        return configUtils.prefixedMessage("messages.chat.no-permission");
    }

    public Component usage() {
        return configUtils.listPrefixedMessage("messages.chat.main-usage");
    }

    public Component whitelistUsage() {
        return configUtils.listPrefixedMessage("messages.chat.whitelist-usage");
    }

    public Component blacklistUsage() {
        return configUtils.listPrefixedMessage("messages.chat.blacklist-usage");
    }

    public Component infoUsage() {
        return configUtils.listPrefixedMessage("messages.chat.info-usage");
    }

    public Component addressAlreadyWhitelisted(String address) {
        return configUtils.prefixedMessage("messages.chat.address-already-whitelisted", address);
    }

    public Component playerAlreadyWhitelisted(String nickname) {
        return configUtils.prefixedMessage("messages.chat.player-already-whitelisted", nickname);
    }

    public Component addressWhitelisted(String address) {
        return configUtils.prefixedMessage("messages.chat.address-whitelisted", address);
    }

    public Component playerWhitelisted(String nickname) {
        return configUtils.prefixedMessage("messages.chat.player-whitelisted", nickname);
    }

    public Component addressNotWhitelisted(String address) {
        return configUtils.prefixedMessage("messages.chat.address-not-whitelisted", address);
    }

    public Component playerNotWhitelisted(String nickname) {
        return configUtils.prefixedMessage("messages.chat.player-not-whitelisted", nickname);
    }

    public Component addressWhitelistRemoved(String address) {
        return configUtils.prefixedMessage("messages.chat.address-whitelist-removed", address);
    }

    public Component playerWhitelistRemoved(String nickname) {
        return configUtils.prefixedMessage("messages.chat.player-whitelist-removed", nickname);
    }

    public Component addressAlreadyBlacklisted(String address) {
        return configUtils.prefixedMessage("messages.chat.address-already-blacklisted", address);
    }

    public Component playerAlreadyBlacklisted(String nickname) {
        return configUtils.prefixedMessage("messages.chat.player-already-blacklisted", nickname);
    }

    public Component addressBlacklisted(String address) {
        return configUtils.prefixedMessage("messages.chat.address-blacklisted", address);
    }

    public Component playerBlacklisted(String nickname) {
        return configUtils.prefixedMessage("messages.chat.player-blacklisted", nickname);
    }

    public Component addressNotBlacklisted(String address) {
        return configUtils.prefixedMessage("messages.chat.address-not-blacklisted", address);
    }

    public Component playerNotBlacklisted(String nickname) {
        return configUtils.prefixedMessage("messages.chat.player-not-blacklisted", nickname);
    }

    public Component addressBlacklistRemoved(String address) {
        return configUtils.prefixedMessage("messages.chat.address-blacklist-removed", address);
    }

    public Component playerBlacklistRemoved(String nickname) {
        return configUtils.prefixedMessage("messages.chat.player-blacklist-removed", nickname);
    }

    public Component wrongAddressPlayer(String text) {
        return configUtils.prefixedMessage("messages.chat.wrong-address-player", text);
    }

    public Component queuePosition(int position, String serverName) {
        return configUtils.prefixedMessage(
                "messages.chat.queue-position", String.valueOf(position), serverName);
    }

    public Component queueMoved(String serverName) {
        return configUtils.prefixedMessage("messages.chat.queue-moved", serverName);
    }

    public Component queuedAlready(String serverName) {
        return configUtils.prefixedMessage("messages.chat.already-in-queue", serverName);
    }

    public Component addressInfo(String address, boolean whitelisted,
                                 boolean blacklisted, Set<String> nicknames) {
        String yesString = configProvider.getString("messages.chat.yes-in-message");
        String noString = configProvider.getString("messages.chat.no-in-message");
        String whitelistedString = whitelisted ? yesString : noString;
        String blacklistedString = blacklisted ? yesString : noString;
        String nicknamesString = String.join(", ", nicknames);
        return configUtils.listPrefixedMessage("messages.chat.address-info",
                address, whitelistedString, blacklistedString, nicknamesString);
    }

    public Component playerInfo(String nickname, boolean whitelisted,
                                 boolean blacklisted, Set<String> addresses) {
        String yesString = configProvider.getString("messages.chat.yes-in-message");
        String noString = configProvider.getString("messages.chat.no-in-message");
        String whitelistedString = whitelisted ? yesString : noString;
        String blacklistedString = blacklisted ? yesString : noString;
        String addressesString = String.join(", ", addresses);
        return configUtils.listPrefixedMessage("messages.chat.player-info",
                nickname, whitelistedString, blacklistedString, addressesString);
    }

    public Component configReloaded() {
        return configUtils.prefixedMessage("messages.chat.config-reloaded");
    }

}
