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

package de.njsm.stocks.servertest;

import dagger.Module;
import dagger.Provides;
import de.njsm.stocks.client.business.CertificateStore;
import de.njsm.stocks.client.business.Settings;
import de.njsm.stocks.client.business.entities.ServerEndpoint;
import de.njsm.stocks.client.runtime.FileInteractor;

import javax.inject.Singleton;
import java.io.*;

@Module
public interface TestModule {

    @Provides
    @Singleton
    static ServerEndpoint serverEndpoint(Settings settings, CertificateStore certificateStore) {
        return ServerEndpoint.create(settings.getServerName(), settings.getServerPort(), certificateStore.getTrustManager(), certificateStore.getKeyManager());
    }

    @Provides
    static FileInteractor fileInteractor() {
        return new FileInteractor() {
            @Override
            public boolean doesFileExist(File file) {
                return file.exists();
            }

            @Override
            public OutputStream getFileOutputStream(File file) {
                throw new UnsupportedOperationException("TODO");
            }

            @Override
            public InputStream getFileInputStream(File file) throws FileNotFoundException {
                return new FileInputStream(file);
            }

            @Override
            public void delete(File file) {
                throw new UnsupportedOperationException("TODO");
            }

            @Override
            public File[] listCrashLogs() {
                throw new UnsupportedOperationException("TODO");
            }
        };
    }

    @Provides
    static Settings settings() {
        return new Settings() {
            @Override
            public String getServerName() {
                return System.getenv().getOrDefault("DEPLOYMENT_VM", "dp-server");
            }

            @Override
            public int getCaPort() {
                return 10910;
            }

            @Override
            public int getRegistrationPort() {
                return 10911;
            }

            @Override
            public int getServerPort() {
                return 10912;
            }

            @Override
            public int getUserId() {
                throw new UnsupportedOperationException("TODO");
            }

            @Override
            public String getUserName() {
                throw new UnsupportedOperationException("TODO");
            }

            @Override
            public int getUserDeviceId() {
                throw new UnsupportedOperationException("TODO");
            }

            @Override
            public String getUserDeviceName() {
                throw new UnsupportedOperationException("TODO");
            }

            @Override
            public String getFingerprint() {
                throw new UnsupportedOperationException("TODO");
            }

            @Override
            public String getTicket() {
                throw new UnsupportedOperationException("TODO");
            }
        };
    }

}
