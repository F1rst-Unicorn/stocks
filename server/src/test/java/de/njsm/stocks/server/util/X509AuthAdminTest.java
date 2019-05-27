/* stocks is client-server program to manage a household's food stock
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
 */

package de.njsm.stocks.server.util;

import de.njsm.stocks.server.v2.business.StatusCode;
import fj.data.Validation;
import org.apache.commons.io.IOUtils;
import org.junit.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;

import static de.njsm.stocks.server.v2.db.DbTestCase.CIRCUIT_BREAKER_TIMEOUT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class X509AuthAdminTest {

    private static File caDirectory;

    private static int deviceCounter;

    private X509AuthAdmin uut;

    @BeforeClass
    public static void setupCa() throws Exception {
        caDirectory = new File("src/test/resources/tmp");
        caDirectory.mkdirs();
        Process p = Runtime.getRuntime().exec("../deploy-server/config/setup-ca " +
                caDirectory.getAbsolutePath() + " " +
                caDirectory.getAbsolutePath() +
                "/../../../../../deploy-server/config");
        p.waitFor();
        deviceCounter = 0;
    }

    @Before
    public void setup() {
        uut = new X509AuthAdmin(caDirectory.getAbsolutePath(),
                "touch " + caDirectory + "/reload-nginx",
                "ca",
                CIRCUIT_BREAKER_TIMEOUT);
    }

    @After
    public void tearDown() {
        deviceCounter++;
    }

    @AfterClass
    public static void removeCa() throws Exception {
        Runtime.getRuntime().exec("rm -rf " + caDirectory.getAbsolutePath()).waitFor();
    }

    @Test
    public void defaultErrorIsCorrect() {
        assertEquals(StatusCode.CA_UNREACHABLE, uut.getDefaultErrorCode());
    }

    @Test
    public void testParseCsr() throws Exception {
        Principals input = getFreshPrincipals();
        generateCsr(input);

        Validation<StatusCode, Principals> p = uut.getPrincipals(deviceCounter);

        Assert.assertTrue(p.isSuccess());
        assertEquals(input, p.success());
    }

    @Test
    public void testParsingInvalidCsr() throws Exception {
        File invalidCsr = new File(caDirectory.getPath() + "/intermediate/csr/user_-1.csr.pem");
        IOUtils.write("invalid csr", new FileOutputStream(invalidCsr), StandardCharsets.UTF_8);

        Validation<StatusCode, Principals> result = uut.getPrincipals(-1);
        Assert.assertFalse(result.isSuccess());
    }

    @Test
    public void testSavingCsr() throws Exception {
        Principals input = getFreshPrincipals();
        String content = generateCsr(input);

        uut.saveCsr(deviceCounter+1, content);
        deviceCounter++;

        String savedContent = IOUtils.toString(
                new FileInputStream(caDirectory.getAbsoluteFile() + "/intermediate/csr/user_" + deviceCounter + ".csr.pem"),
                StandardCharsets.UTF_8);
        assertEquals(content, savedContent);
    }

    @Test
    public void testCertificateGeneration() throws Exception {
        Principals input = getFreshPrincipals();
        String content = generateCsr(input);
        uut.saveCsr(input.getDid(), content);

        uut.generateCertificate(input.getDid());

        assertTrue(new File(caDirectory.getPath() + "/intermediate/certs/user_" + input.getDid() + ".cert.pem").exists());
    }

    @Test
    public void testGettingCertificate() throws Exception {
        Principals input = getFreshPrincipals();
        String content = generateCsr(input);
        uut.saveCsr(input.getDid(), content);
        uut.generateCertificate(input.getDid());

        Validation<StatusCode, String> certificate = uut.getCertificate(input.getDid());

        Assert.assertTrue(certificate.isSuccess());
        assertFalse(certificate.success().isEmpty());
    }

    @Test
    public void testWiping() throws Exception {
        Principals input = getFreshPrincipals();
        String content = generateCsr(input);
        uut.saveCsr(input.getDid(), content);
        uut.generateCertificate(input.getDid());

        uut.wipeDeviceCredentials(input.getDid());

        assertFalse(new File(caDirectory.getPath() + "/intermediate/csr/user_" + input.getDid() + ".csr.pem").exists());
        assertFalse(new File(caDirectory.getPath() + "/intermediate/cert/user_" + input.getDid() + ".cert.pem").exists());
    }

    @Test
    public void testCrlGenerationAndReloading() throws Exception {
        Principals input = getFreshPrincipals();
        String content = generateCsr(input);
        uut.saveCsr(input.getDid(), content);
        uut.generateCertificate(input.getDid());

        uut.revokeCertificate(input.getDid());

        assertTrue(new File(caDirectory.getPath() + "/reload-nginx").exists());
        assertTrue(new File(caDirectory.getPath() + "/intermediate/crl/intermediate.crl.pem").exists());
    }

    private String generateCsr(Principals p) throws Exception {
        Process pr;
        pr = Runtime.getRuntime().exec(String.format("openssl genrsa -out %s/%s/user_%d.key.pem 1024",
                caDirectory.getAbsoluteFile(),
                "intermediate/private/",
                p.getDid()));
        pr.waitFor();

        String command = String.format("openssl req -config %s/openssl.cnf " +
                        "-new -sha256 -key %s/private/user_%d.key.pem -out %s/csr/user_%d.csr.pem " +
                        "-subj /CN=%s -batch",
                caDirectory.getPath() + "/intermediate",
                caDirectory.getPath() + "/intermediate",
                p.getDid(),
                caDirectory.getPath() + "/intermediate",
                p.getDid(),
                p.toString());
        pr = Runtime.getRuntime().exec(command);
        pr.waitFor();
        return IOUtils.toString(new FileInputStream(
                caDirectory.getAbsoluteFile() + "/intermediate/csr/user_" + p.getDid() + ".csr.pem"),
                StandardCharsets.UTF_8);
    }

    private Principals getFreshPrincipals() {
        return new Principals("Jack", "Device", 1, ++deviceCounter);
    }
}