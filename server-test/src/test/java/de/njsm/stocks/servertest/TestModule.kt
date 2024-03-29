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
package de.njsm.stocks.servertest

import dagger.Module
import dagger.Provides
import de.njsm.stocks.client.business.CertificateStore
import de.njsm.stocks.client.business.Settings
import de.njsm.stocks.client.business.entities.ServerEndpoint
import de.njsm.stocks.client.runtime.FileInteractor
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Singleton

@Module
interface TestModule {
    companion object {
        @Provides
        @Singleton
        fun serverEndpoint(
            settings: Settings,
            certificateStore: CertificateStore,
        ): ServerEndpoint {
            return ServerEndpoint.create(
                settings.serverName,
                settings.serverPort,
                certificateStore.trustManager,
                certificateStore.keyManager,
            )
        }

        @Provides
        fun fileInteractor(): FileInteractor {
            return object : FileInteractor {
                override fun doesFileExist(file: File): Boolean = file.exists()

                override fun getFileInputStream(file: File): InputStream = FileInputStream(file)

                override fun getFileOutputStream(file: File): OutputStream {
                    throw UnsupportedOperationException("TODO")
                }

                override fun delete(file: File) {
                    throw UnsupportedOperationException("TODO")
                }

                override fun listCrashLogs(): Array<File> {
                    throw UnsupportedOperationException("TODO")
                }
            }
        }

        @Provides
        fun settings(): Settings {
            return object : Settings {
                override fun getServerName(): String = System.getenv().getOrDefault("DEPLOYMENT_VM", "dp-server")

                override fun getCaPort(): Int = 10910

                override fun getRegistrationPort(): Int = 10911

                override fun getServerPort(): Int = 10912

                override fun getUserId(): Int {
                    throw UnsupportedOperationException("TODO")
                }

                override fun getUserName(): String {
                    throw UnsupportedOperationException("TODO")
                }

                override fun getUserDeviceId(): Int {
                    throw UnsupportedOperationException("TODO")
                }

                override fun getUserDeviceName(): String {
                    throw UnsupportedOperationException("TODO")
                }

                override fun getFingerprint(): String {
                    throw UnsupportedOperationException("TODO")
                }

                override fun getTicket(): String {
                    throw UnsupportedOperationException("TODO")
                }
            }
        }
    }
}
