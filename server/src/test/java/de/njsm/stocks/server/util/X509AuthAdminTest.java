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

import de.njsm.stocks.common.api.StatusCode;
import fj.data.Validation;
import org.apache.commons.io.IOUtils;
import org.junit.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Set;

import static de.njsm.stocks.server.v2.db.DbTestCase.CIRCUIT_BREAKER_TIMEOUT;
import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.*;

public class X509AuthAdminTest {

    private static File caDirectory;

    private static int testCounter;

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
        testCounter = 0;
    }

    @Before
    public void setup() {
        uut = new X509AuthAdmin(caDirectory.getAbsolutePath(),
                "touch " + caDirectory + "/reload-nginx",
                "ca" + testCounter,
                CIRCUIT_BREAKER_TIMEOUT);
    }

    @After
    public void tearDown() {
        testCounter++;
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

        Validation<StatusCode, Principals> p = uut.getPrincipals(testCounter);

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

        uut.saveCsr(testCounter +1, content);
        testCounter++;

        String savedContent = IOUtils.toString(
                new FileInputStream(caDirectory.getAbsoluteFile() + "/intermediate/csr/user_" + testCounter + ".csr.pem"),
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

    @Test
    public void testHealthCheck() {
        StatusCode result = uut.getHealth();

        assertEquals(StatusCode.SUCCESS, result);
    }

    @Test
    public void testFailingHealth() {
        File f = new File(caDirectory.getAbsolutePath() + "/intermediate/certs/user_1.cert.pem");
        File tmp = new File(caDirectory.getAbsolutePath() + "/intermediate/certs/user.cert.pem");
        assertTrue(f.renameTo(tmp));

        StatusCode result = uut.getHealth();

        assertEquals(StatusCode.CA_UNREACHABLE, result);
        assertTrue(tmp.renameTo(f));
    }

    @Test
    public void testFetchingValidPrincipals() {

        Validation<StatusCode, Set<Principals>> output = uut.getValidPrincipals();

        assertTrue(output.isSuccess());
        assertFalse(output.success().isEmpty());
        assertTrue(output.success().contains(new Principals("Jack", "Device", 1, 1)));
    }

    @Test
    public void parsingIndexLineWorks() {
        assertEquals(new Principals("Jack", "Device", 1, 1), uut.parseIndexLine("V\t30190519221020Z\t\t1001\tunknown\t/O=stocks/OU=User/CN=Jack$1$Device$1"));
        assertEquals(new Principals("Jack", "cli-client", 1, 6), uut.parseIndexLine("V\t30190519221049Z\t\t1003\tunknown\t/O=stocks/OU=User/CN=Jack$1$cli-client$6"));
        assertEquals(new Principals("Jack", "android-client", 1, 7), uut.parseIndexLine("V\t30190519221125Z\t\t1004\tunknown\t/CN=Jack$1$android-client$7"));

        assertNull(uut.parseIndexLine("V\t30190519221008Z\t\t1000\tunknown\t/C=CH/ST=Zurich/L=Zurich/O=stocks/CN=stocks server"));
        assertNull(uut.parseIndexLine("R\t30190519221031Z\t200116221031Z\t1002\tunknown\t/O=stocks/OU=User/CN=Jon$5$Laptop$5"));
        assertNull(uut.parseIndexLine("E\t20190519221125Z\t\t1004\tunknown\t/CN=Jack$1$android-client$7"));
        assertNull(uut.parseIndexLine("V\t30190519221125Z\t1004\tunknown\t/CN=Jack$1$android-client$7"));
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
        return new Principals("Jack", "Device", 1, ++testCounter);
    }
}
