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

package xyz.yawek.barricade.data.geo;

import com.maxmind.db.CHMCache;
import com.maxmind.db.Reader;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.utils.IOUtils;
import xyz.yawek.barricade.Barricade;
import xyz.yawek.barricade.util.LogUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Optional;
import java.util.zip.GZIPInputStream;

public class GeoDataProvider {

    private final Barricade barricade;
    private DatabaseReader countryReader;
    private DatabaseReader asnReader;

    public GeoDataProvider(Barricade barricade) {
        this.barricade = barricade;

        loadDatabases();
    }

    public void loadDatabases() {
        try {
            String licenseKey = barricade.getConfig().geoipLicense();
            if (licenseKey.equalsIgnoreCase("license_key")) {
                logWarnNoSetup();
                return;
            }
            File dataFolder = barricade.getDataDirectory().toFile();
            File countryFile = new File(dataFolder, "GeoLite2-Country.mmdb");
            File countryArchiveFile = new File(dataFolder, "GeoLite2-Country.tar.gz");
            File asnFile = new File(dataFolder, "GeoLite2-ASN.mmdb");
            File asnArchiveFile = new File(dataFolder, "GeoLite2-ASN.tar.gz");

            LogUtils.info("Downloading GeoIP databases...");
            if (countryReader != null) countryReader.close();
            downloadDBFile(countryFile, countryArchiveFile,
                    "https://download.maxmind.com/app/geoip_download" +
                            "?edition_id=GeoLite2-Country&license_key=" +
                            licenseKey +
                            "&suffix=tar.gz");
            if (asnReader != null) asnReader.close();
            downloadDBFile(asnFile, asnArchiveFile,
                    "https://download.maxmind.com/app/geoip_download" +
                            "?edition_id=GeoLite2-ASN&license_key=" +
                            licenseKey +
                            "&suffix=tar.gz");
            countryReader = loadReader("GeoLite2-Country.mmdb");
            asnReader = loadReader("GeoLite2-ASN.mmdb");
        } catch (IOException e) {
            LogUtils.error("GeoIP files could not be loaded.");
            e.printStackTrace();
        }
    }

    public Optional<String> getCountryCode(InetAddress inetAddress) {
        if (countryReader == null) {
            logWarnNoSetup();
            return Optional.empty();
        }
        try {
            return Optional.of(countryReader.country(inetAddress).getCountry().getIsoCode());
        } catch (IOException | GeoIp2Exception ignored) {}
        return Optional.empty();
    }

    public Optional<String> getAsnCode(InetAddress inetAddress) {
        if (countryReader == null) {
            logWarnNoSetup();
            return Optional.empty();
        }
        try {
            return Optional.of("AS" + asnReader.asn(inetAddress).getAutonomousSystemNumber());
        } catch (IOException | GeoIp2Exception ignored) {}
        return Optional.empty();
    }

    private void downloadDBFile(File targetFile, File archive, String urlString) {
        try {
            ReadableByteChannel readableByteChannel = Channels.newChannel(new URL(urlString).openStream());
            FileOutputStream fileOutputStream = new FileOutputStream(archive);
            fileOutputStream.getChannel()
                    .transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
            fileOutputStream.close();

            TarArchiveInputStream tarInput =
                    new TarArchiveInputStream(new GZIPInputStream(new FileInputStream(archive)));
            TarArchiveEntry entry = tarInput.getNextTarEntry();
            while (entry != null) {
                String[] entriesNames = entry.getName().split("/");
                if (entriesNames[entriesNames.length - 1].equals(targetFile.getName())) {
                    FileOutputStream outputStream = new FileOutputStream(targetFile);
                    IOUtils.copy(tarInput, outputStream);
                    outputStream.close();
                }
                entry = tarInput.getNextTarEntry();
            }
            tarInput.close();
            LogUtils.info("GeoIP {} database has been downloaded successfully!", targetFile.getName());
        } catch (IOException e) {
            LogUtils.error("GeoIP {} file could not be downloaded.", targetFile.getName());
            e.printStackTrace();
        } finally {
            archive.delete();
        }
    }

    private DatabaseReader loadReader(String fileName) throws IOException {
        return new DatabaseReader.Builder(
                new File(barricade.getDataDirectory().toFile(), fileName))
                .withCache(new CHMCache())
                .fileMode(Reader.FileMode.MEMORY)
                .build();
    }

    private void logWarnNoSetup() {
        LogUtils.warn("You have not set the GeoIP license key. " +
            "Setting valid license key is necessary for plugin to work.");
        LogUtils.warn("Please set a valid license key and restart the server.");
    }

}
