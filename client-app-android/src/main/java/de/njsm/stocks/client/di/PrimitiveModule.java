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

package de.njsm.stocks.client.di;

import android.content.Context;
import dagger.Module;
import dagger.Provides;
import de.njsm.stocks.client.Application;
import de.njsm.stocks.client.business.CertificateStore;
import de.njsm.stocks.client.business.Clock;
import de.njsm.stocks.client.business.Settings;
import de.njsm.stocks.client.business.entities.ServerEndpoint;
import de.njsm.stocks.client.runtime.FileInteractor;

import javax.inject.Singleton;
import java.io.*;
import java.time.Instant;

@Module
public interface PrimitiveModule {

    @Provides
    @Singleton
    static FileInteractor fileInteractor(Application a) {
        return new FileInteractor() {
            @Override
            public boolean doesFileExist(File file) {
                try (InputStream stream = getFileInputStream(file)) {
                    return true;
                } catch (IOException e) {
                    return false;
                }
            }

            @Override
            public OutputStream getFileOutputStream(File file) throws FileNotFoundException {
                return a.openFileOutput(file.getName(), Context.MODE_PRIVATE);
            }

            @Override
            public InputStream getFileInputStream(File file) throws FileNotFoundException {
                return a.openFileInput(file.getName());
            }

            @Override
            public void delete(File file) {
                a.deleteFile(file.getName());
            }

            @Override
            public File[] listCrashLogs() {
                return a.getFilesDir().listFiles((__, name) ->
                        name.startsWith("crashlog_") && name.endsWith(".txt"));
            }
        };
    }

    @Provides
    @Singleton
    static ServerEndpoint serverEndpoint(Settings settings, CertificateStore certificateStore) {
        return ServerEndpoint.create(settings.getServerName(), settings.getServerPort(), certificateStore.getTrustManager(), certificateStore.getKeyManager());
    }

    @Provides
    @Singleton
    static Clock clock() {
        return Instant::now;
    }
}
