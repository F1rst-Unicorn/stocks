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
package de.njsm.stocks.servertest.v2

import de.njsm.stocks.client.business.Registrator
import de.njsm.stocks.client.business.StatusCodeException
import de.njsm.stocks.client.business.UserDeviceAddService
import de.njsm.stocks.client.business.entities.IdImpl
import de.njsm.stocks.client.business.entities.RegistrationCsr
import de.njsm.stocks.client.business.entities.RegistrationEndpoint
import de.njsm.stocks.client.business.entities.ServerEndpoint
import de.njsm.stocks.client.business.entities.StatusCode
import de.njsm.stocks.client.business.entities.User
import de.njsm.stocks.client.business.entities.UserDeviceAddForm
import de.njsm.stocks.servertest.TestSuite
import de.njsm.stocks.servertest.v2.repo.UserRepository
import io.restassured.RestAssured
import io.restassured.config.RestAssuredConfig
import io.restassured.config.SSLConfig
import io.restassured.http.ContentType
import io.restassured.response.ValidatableResponse
import org.hamcrest.Matchers
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.MethodOrderer.MethodName
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.junit.jupiter.api.fail
import java.io.File
import java.security.KeyPair
import java.security.KeyStore
import javax.inject.Inject

@TestMethodOrder(MethodName::class)
@Order(1500)
class RegistrationTest : Base() {
    private lateinit var ticket: String

    private var deviceId: Int = 0

    private lateinit var keypair: KeyPair

    private lateinit var commonName: String

    private lateinit var keystore: KeyStore

    internal lateinit var userRepository: UserRepository
        @Inject set

    internal lateinit var userDeviceAddService: UserDeviceAddService
        @Inject set

    internal lateinit var registrator: Registrator
        @Inject set

    internal lateinit var serverEndpoint: ServerEndpoint
        @Inject set

    private lateinit var userId: IdImpl<User>

    private lateinit var userName: String

    @BeforeEach
    fun setupCredentials() {
        dagger.inject(this)
        userName = uniqueName
        userId = userRepository.createNewUser(userName)
        val ticket =
            userDeviceAddService.add(
                UserDeviceAddForm.create(
                    "Laptop",
                    userId,
                ),
            )
        this.ticket = ticket.ticket()
        deviceId = ticket.id().id()

        keypair = SetupTest.generateKeyPair()
        commonName = userName + "$" + userId.id() + "\$Laptop$" + deviceId
    }

    @AfterEach
    fun removeKeystore() {
        File("keystore_2").deleteOnExit()
    }

    @Test
    fun test1cannotRegisterWithWrongTicket() {
        tryFailingRegistration(deviceId, "0000", commonName)
        tryFailingRegistration(deviceId, "fdsa", commonName)
    }

    @Test
    fun test1cannotRegisterWithWrongDeviceId() {
        tryFailingRegistration(1, ticket, commonName)
    }

    @Test
    fun test1cannotRegisterWithWrongCommonName() {
        tryFailingRegistration(
            deviceId,
            ticket,
            userName + "$" + userId.id() + "\$Laptop$0",
        )
        tryFailingRegistration(
            deviceId,
            ticket,
            userName + "$" + userId.id() + "\$Lapto$" + deviceId,
        )
        tryFailingRegistration(
            deviceId,
            ticket,
            "Jack$" + userId.id() + "\$Laptop$" + deviceId,
        )
        tryFailingRegistration(
            deviceId,
            ticket,
            "$userName$0\$Laptop$$deviceId",
        )
        tryFailingRegistration(deviceId, ticket, "")
    }

    @Test
    fun test2haveCorrectRegistration() {
        val cert = registerSuccessfully(deviceId, ticket, commonName)
        keystore = SetupTest.firstKeystore
        SetupTest.storeToDisk(keystore, cert, "keystore_2", keypair)

        accessServerWithSecondAccount(keystore)
            .statusCode(200)
            .contentType(ContentType.JSON)

        revokedUserCannotAccessAnyMore()
    }

    private fun revokedUserCannotAccessAnyMore() {
        RestAssured.given()
            .log().ifValidationFails()
            .contentType(ContentType.JSON)
            .queryParam("id", deviceId)
            .queryParam("version", 0)
            .`when`()
            .delete(TestSuite.domain + "/v2/device")
            .then()
            .log().ifValidationFails()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("status", Matchers.equalTo(0))
        Thread.sleep(3000)

        accessServerWithSecondAccount(keystore)
            .statusCode(400)
    }

    private fun tryFailingRegistration(
        deviceId: Int,
        ticket: String,
        commonName: String,
    ) {
        val csr = SetupTest.getCsr(keypair, commonName)
        try {
            registrator.getOwnCertificate(
                RegistrationEndpoint.create(
                    TestSuite.hostname,
                    TestSuite.INIT_PORT.toInt(),
                    serverEndpoint.trustManagerFactory(),
                    serverEndpoint.keyManagerFactory(),
                ),
                RegistrationCsr.create(
                    deviceId,
                    ticket,
                    csr,
                ),
            )
            fail("registration was expected to fail, but didn't")
        } catch (e: StatusCodeException) {
            assertEquals(StatusCode.ACCESS_DENIED, e.statusCode)
        }
    }

    private fun registerSuccessfully(
        deviceId: Int,
        ticket: String,
        commonName: String,
    ): String {
        val csr = SetupTest.getCsr(keypair, commonName)
        return registrator.getOwnCertificate(
            RegistrationEndpoint.create(
                TestSuite.hostname,
                TestSuite.INIT_PORT.toInt(),
                serverEndpoint.trustManagerFactory(),
                serverEndpoint.keyManagerFactory(),
            ),
            RegistrationCsr.create(
                deviceId,
                ticket,
                csr,
            ),
        )
    }

    fun accessServerWithSecondAccount(keystore: KeyStore): ValidatableResponse {
        return RestAssured.given()
            .log().ifValidationFails()
            .config(
                RestAssuredConfig.config().sslConfig(
                    SSLConfig.sslConfig()
                        .allowAllHostnames()
                        .trustStore(keystore)
                        .keyStore("keystore_2", SetupTest.PASSWORD),
                ),
            )
            .queryParam("startingFrom")
            .queryParam("upUntil")
            .`when`()
            .get(TestSuite.domain + "/v2/location")
            .then()
            .log().ifValidationFails()
    }
}
