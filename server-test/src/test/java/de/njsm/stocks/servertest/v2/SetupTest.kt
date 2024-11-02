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

import de.njsm.stocks.servertest.TestSuite
import io.restassured.RestAssured
import io.restassured.config.LogConfig
import io.restassured.config.SSLConfig
import io.restassured.http.ContentType
import io.restassured.response.ValidatableResponse
import org.bouncycastle.operator.OperatorCreationException
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder
import org.bouncycastle.util.io.pem.PemObject
import org.bouncycastle.util.io.pem.PemWriter
import org.hamcrest.CoreMatchers
import org.hamcrest.Matchers
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.StringWriter
import java.nio.charset.StandardCharsets
import java.security.*
import java.security.cert.Certificate
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import javax.security.auth.x500.X500Principal

@Order(100)
class SetupTest {

    private val subjectName = "Jack$1\$Device$1"

    private lateinit var clientKeys: KeyPair

    @BeforeEach
    fun setup() {
        keystore = firstKeystore
        RestAssured.config = RestAssured.config().sslConfig(
            SSLConfig.sslConfig()
                .allowAllHostnames()
                .trustStore(keystore)
        )
            .logConfig(
                LogConfig.logConfig()
                    .enableLoggingOfRequestAndResponseIfValidationFails()
            )
    }

    @AfterEach
    fun tearDown() {
        RestAssured.config = RestAssured.config().sslConfig(
            SSLConfig.sslConfig()
                .allowAllHostnames()
                .trustStore(keystore)
                .keyStore("keystore", PASSWORD)
        )
            .logConfig(
                LogConfig.logConfig()
                    .enableLoggingOfRequestAndResponseIfValidationFails()
            )
    }

    @Test
    fun setupFirstAccount() {
        clientKeys = generateKeyPair()
        val csr = getCsr(clientKeys, subjectName)

        val response: ValidatableResponse =
            RestAssured.given()
                .log().ifValidationFails()
                .formParam("device", 1)
                .formParam("token", "0000")
                .formParam("csr", csr)
                .`when`()
                .post("https://" + TestSuite.HOSTNAME + ":" + TestSuite.INIT_PORT + "/v2/auth/newuser")
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("status", CoreMatchers.equalTo(0))
                .body("data", Matchers.not(Matchers.isEmptyOrNullString()))

        val rawCert = response.extract().jsonPath().getString("data")
        storeToDisk(
            keystore, rawCert, "keystore",
            clientKeys
        )
    }

    companion object {
        private lateinit var keystore: KeyStore

        const val PASSWORD: String = "passwordfooyouneverguessme$32XD"

        fun generateKeyPair(): KeyPair {
            val gen = KeyPairGenerator.getInstance("RSA")
            gen.initialize(2048)
            return gen.generateKeyPair()
        }

        fun storeToDisk(
            keystore: KeyStore,
            rawCert: String,
            fileName: String,
            clientKeys: KeyPair
        ) {
            val clientCert = convertToCertificate(rawCert)
            val trustChain = arrayOfNulls<Certificate>(3)
            trustChain[0] = clientCert
            trustChain[1] = keystore.getCertificate("chain")
            trustChain[2] = keystore.getCertificate("ca")
            keystore.setKeyEntry(
                "client",
                clientKeys.private,
                PASSWORD.toCharArray(),
                trustChain
            )
            keystore.store(FileOutputStream(fileName), PASSWORD.toCharArray())
        }

        val firstKeystore: KeyStore
            get() {
                val keystore =
                    KeyStore.getInstance(KeyStore.getDefaultType())
                keystore.load(null)
                val ca: String = RestAssured
                    .`when`()
                    .get("http://" + TestSuite.HOSTNAME + ":" + TestSuite.CA_PORT + "/ca")
                    .then()
                    .extract()
                    .body()
                    .asString()
                val chain: String = RestAssured
                    .`when`()
                    .get("http://" + TestSuite.HOSTNAME + ":" + TestSuite.CA_PORT + "/chain")
                    .then()
                    .extract()
                    .body()
                    .asString()
                keystore.setCertificateEntry(
                    "ca",
                    convertToCertificate(ca)
                )
                keystore.setCertificateEntry(
                    "chain",
                    convertToCertificate(chain)
                )
                return keystore
            }

        @Throws(
            OperatorCreationException::class,
            IOException::class
        )
        fun getCsr(clientKeys: KeyPair, subjectName: String): String {
            val principal = X500Principal("CN=" + subjectName + ",OU=User,O=stocks")
            val signGen = JcaContentSignerBuilder("SHA256WithRSA").build(clientKeys.private)
            val builder: PKCS10CertificationRequestBuilder =
                JcaPKCS10CertificationRequestBuilder(
                    principal,
                    clientKeys.public
                )
            val csr = builder.build(signGen)
            val `object` = PemObject("CERTIFICATE REQUEST", csr.encoded)
            val writer = StringWriter()
            val pemWriter = PemWriter(writer)
            pemWriter.writeObject(`object`)
            pemWriter.close()
            return writer.toString()
        }

        private fun convertToCertificate(pemFile: String): Certificate {
            val factory = CertificateFactory.getInstance("X.509")
            return factory.generateCertificate(
                ByteArrayInputStream(
                    pemFile.toByteArray(
                        StandardCharsets.UTF_8
                    )
                )
            )
        }
    }
}
