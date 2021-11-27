/*
 * stocks is client-server program to manage a household's food stock
 * Copyright (C) 2019  The stocks developers
 *
 * This file is part of the stocks program suite.
 *
 * stocks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * stocks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package de.njsm.stocks.clientold.init;

import de.njsm.stocks.clientold.config.Configuration;
import de.njsm.stocks.clientold.config.PropertiesFileHandler;
import de.njsm.stocks.clientold.exceptions.CryptoException;
import de.njsm.stocks.clientold.exceptions.InitialisationException;
import de.njsm.stocks.clientold.frontend.CertificateGenerator;
import de.njsm.stocks.clientold.frontend.ConfigGenerator;
import de.njsm.stocks.clientold.init.upgrade.UpgradeManager;
import de.njsm.stocks.clientold.network.TcpHost;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

public class InitManager {

    private static final Logger LOG = LogManager.getLogger(InitManager.class);

    private final Configuration newConfiguration;

    private final ConfigGenerator configGenerator;

    private final CertificateGenerator certificateGenerator;

    private final TicketHandler ticketHandler;

    private final UpgradeManager upgradeManager;

    private TcpHost caHost;

    private TcpHost ticketHost;

    public InitManager(ConfigGenerator configGenerator,
                       CertificateGenerator certificateGenerator,
                       TicketHandler ticketHandler,
                       PropertiesFileHandler fileHandler,
                       UpgradeManager upgradeManager) {
        this.configGenerator = configGenerator;
        this.certificateGenerator = certificateGenerator;
        this.ticketHandler = ticketHandler;
        this.upgradeManager = upgradeManager;
        newConfiguration = new Configuration(fileHandler);
    }

    public void initialise() throws InitialisationException {
        if (isFirstStartup()) {
            LOG.info("Starting initialisation process");
            runFirstInitialisation();
        } else if (upgradeManager.needsUpgrade()) {
            LOG.info("Upgrading to new version");
            upgradeManager.upgrade();
        } else {
            LOG.info("Client is already initialised");
        }
    }

    private boolean isFirstStartup() {
        return ! new File(Configuration.CONFIG_PATH).exists();
    }

    private void runFirstInitialisation() throws InitialisationException {
        try {
            ticketHandler.startBackgroundWork();
            initialiseConfigFile();
            setupDatabase();
            getServerProperties(configGenerator);
            createHosts();
            initCertificates(certificateGenerator);
            newConfiguration.saveConfig();
        } catch (IOException e) {
            LOG.error("Error during initialisation", e);
            LOG.error("Reverting keystore file");
            destroyKeystore();
            throw new InitialisationException("Initialisation failed");
        }
    }

    private void setupDatabase() throws IOException {
        LOG.info("Copying " + Configuration.SYSTEM_DB_PATH + " to " + Configuration.DB_PATH);
        BufferedInputStream inFile = new BufferedInputStream(new FileInputStream(Configuration.SYSTEM_DB_PATH));
        BufferedOutputStream outFile = new BufferedOutputStream(new FileOutputStream(Configuration.DB_PATH));
        IOUtils.copyLarge(inFile, outFile);
        inFile.close();
        outFile.close();
    }

    private void getServerProperties(ConfigGenerator source) {
        String serverName = source.getServerName();
        int[] ports = source.getPorts();

        newConfiguration.setServerName(serverName);
        newConfiguration.setCaPort(ports[0]);
        newConfiguration.setTicketPort(ports[1]);
        newConfiguration.setServerPort(ports[2]);

        LOG.info("Server hostname is " + serverName);
        LOG.info("CA port is " + ports[0]);
        LOG.info("Ticket port is " + ports[1]);
        LOG.info("Server port is " + ports[2]);
    }

    private void createHosts() {
        caHost = new TcpHost(newConfiguration.getServerName(),
                newConfiguration.getCaPort());
        ticketHost = new TcpHost(newConfiguration.getServerName(),
                newConfiguration.getTicketPort());
    }

    private void initCertificates(CertificateGenerator source) throws InitialisationException {
        try {

            String username = source.getUsername();
            String deviceName = source.getDeviceName();
            int uid = source.getUserId();
            int did = source.getDeviceId();
            String fingerprint = source.getCaFingerprint();
            String ticket = source.getTicket();

            ticketHandler.generateKey();
            ticketHandler.verifyServerCa(caHost, fingerprint);
            ticketHandler.generateCsr(username, deviceName, uid, did);
            ticketHandler.handleTicket(ticketHost, ticket, did);

            newConfiguration.setUsername(username);
            newConfiguration.setDeviceName(deviceName);
            newConfiguration.setUserId(uid);
            newConfiguration.setDeviceId(did);
            newConfiguration.setFingerprint(fingerprint);

        } catch (CryptoException e) {
            LOG.error("Error during key creation", e);
            destroyKeystore();
            throw new InitialisationException("Certificate generation failed");
        }
    }

    private void initialiseConfigFile() throws IOException {
        File configFile = new File(Configuration.CONFIG_PATH);
        boolean newDirectoryWasCreated = configFile.getParentFile().mkdirs();
        if (newDirectoryWasCreated) {
            LOG.info("New path for stocks config created");
        }
    }

    private void destroyKeystore() {
        File keystore = new File(Configuration.KEYSTORE_PATH);
        keystore.delete();
    }

}
