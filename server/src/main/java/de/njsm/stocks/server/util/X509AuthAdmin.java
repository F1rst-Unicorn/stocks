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

import de.njsm.stocks.common.util.FunctionWithExceptions;
import de.njsm.stocks.common.util.ProducerWithExceptions;
import de.njsm.stocks.common.api.*;
import de.njsm.stocks.server.v2.web.servlet.PrincipalFilter;
import fj.data.Validation;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

public class X509AuthAdmin implements AuthAdmin, HystrixWrapper<Void, Exception> {

    private static final Logger LOG = LogManager.getLogger(X509AuthAdmin.class);

    private final String csrFormatString;

    private final String certFormatString;

    private final String caRootDirectory;

    private final String reloadCommand;

    private final String resourceIdentifier;

    private final int timeout;

    public X509AuthAdmin(String caRootDirectory,
                         String reloadCommand,
                         String resourceIdentifier,
                         int timeout) {
        this.caRootDirectory = caRootDirectory;
        this.csrFormatString = caRootDirectory + "/intermediate/csr/%s.csr.pem";
        this.certFormatString = caRootDirectory + "/intermediate/certs/%s.cert.pem";
        this.reloadCommand = reloadCommand;
        this.resourceIdentifier = resourceIdentifier;
        this.timeout = timeout;
    }

    @Override
    public synchronized StatusCode saveCsr(int deviceId, String content) {
        return runCommand(dummy -> {
            FileOutputStream csrFile = new FileOutputStream(getCsrFileName(deviceId));
            IOUtils.write(content, csrFile, StandardCharsets.UTF_8);
            csrFile.close();
            return StatusCode.SUCCESS;
        });
    }

    @Override
    public synchronized void wipeDeviceCredentials(int deviceId) {
        (new File(getCsrFileName(deviceId))).delete();
        (new File(getCertificateFileName(deviceId))).delete();
    }

    public synchronized StatusCode generateCertificate(int deviceId) {

        String command = String.format("openssl ca " +
                        "-config %s/intermediate/openssl.cnf " +
                        "-extensions usr_cert " +
                        "-notext " +
                        "-batch " +
                        "-md sha256 " +
                        "-in %s " +
                        "-out %s ",
                caRootDirectory,
                getCsrFileName(deviceId),
                getCertificateFileName(deviceId));

        return runCommand(dummy -> {
            executeSystemCommand(command);
            return StatusCode.SUCCESS;
        });
    }

    @Override
    public synchronized Validation<StatusCode, String> getCertificate(int deviceId) {
        return runFunction(dummy -> {
            FileInputStream input = new FileInputStream(getCertificateFileName(deviceId));
            String result = IOUtils.toString(input, StandardCharsets.UTF_8);
            input.close();
            return Validation.success(result);
        });
    }

    /**
     * Read the CSR and extract the parts of the Subject name
     *
     * @return The parsed principals
     */
    @Override
    public synchronized Validation<StatusCode, Principals> getPrincipals(int deviceId) {
        return runFunction(dummy -> {
            PEMParser parser = new PEMParser(new FileReader(getCsrFileName(deviceId)));
            Object csrRaw = parser.readObject();
            if (csrRaw instanceof PKCS10CertificationRequest) {
                PKCS10CertificationRequest csr = (PKCS10CertificationRequest) csrRaw;
                return PrincipalFilter.parseSubjectName(csr.getSubject().toString());
            } else {
                LOG.warn("Could not parse CSR");
                return Validation.fail(StatusCode.INVALID_ARGUMENT);
            }
        });
    }

    @Override
    public synchronized StatusCode revokeCertificate(int id) {
        return runCommand(dummy -> {
            String command = String.format("openssl ca " +
                            "-config %s/intermediate/openssl.cnf " +
                            "-batch " +
                            "-revoke %s",
                    caRootDirectory,
                    getCertificateFileName(id));
            executeSystemCommand(command);
            refreshCrl();
            return StatusCode.SUCCESS;
        });
    }

    @Override
    public Validation<StatusCode, Set<Principals>> getValidPrincipals() {
        return runFunction(dummy -> {
            Set<Principals> result = new HashSet<>();

            FileInputStream input = new FileInputStream(caRootDirectory + "/intermediate/index.txt");
            String[] content = IOUtils.toString(input, StandardCharsets.UTF_8).split("\n");

            for (String line : content) {
                Principals p = parseIndexLine(line);
                if (p != null)
                    result.add(p);
            }

            return Validation.success(result);
        });
    }

    @Override
    public StatusCode getHealth() {
        // Assume that the certificate for the first device always exists
        Validation<StatusCode, String> probe = getCertificate(1);
        if (probe.isFail()) {
            return probe.fail();
        } else {
            return StatusCode.SUCCESS;
        }
    }

    @Override
    public String getResourceIdentifier() {
        return resourceIdentifier;
    }

    @Override
    public int getCircuitBreakerTimeout() {
        return timeout;
    }

    @Override
    public StatusCode getDefaultErrorCode() {
        return StatusCode.CA_UNREACHABLE;
    }

    @Override
    public <O> ProducerWithExceptions<Validation<StatusCode, O>, Exception>
    wrap(FunctionWithExceptions<Void, Validation<StatusCode, O>, Exception> client) {
        return () -> client.apply(null);
    }

    // Parsed according to https://pki-tutorial.readthedocs.io/en/latest/cadb.html
    Principals parseIndexLine(String line) {
        String[] fields = line.split("\t");
        if (fields.length != 6)
            return null;

        if (!fields[0].equals("V"))
            return null;

        if (line.matches("^(.*/)?CN=stocks server(/.*)?$"))
            return null;

        Validation<StatusCode, Principals> result = PrincipalFilter.parseSubjectName(fields[5]);
        return result.isSuccess() ? result.success() : null;
    }

    private void refreshCrl() {
        runCommand(dummy -> {
            String command = String.format("openssl ca " +
                            "-config %s/intermediate/openssl.cnf " +
                            "-gencrl " +
                            "-out %s/intermediate/crl/intermediate.crl.pem",
                    caRootDirectory,
                    caRootDirectory);

            executeSystemCommand(command);

            FileOutputStream out = new FileOutputStream(caRootDirectory + "/intermediate/crl/whole.crl.pem");
            IOUtils.copy(new FileInputStream(caRootDirectory + "/crl/ca.crl.pem"), out);
            IOUtils.copy(new FileInputStream(caRootDirectory + "/intermediate/crl/intermediate.crl.pem"), out);
            out.close();

            Runtime.getRuntime().exec(reloadCommand).waitFor();
            return null;
        });
    }

    private void executeSystemCommand(String command) throws IOException, InterruptedException {
        Process p = Runtime.getRuntime().exec(command);
        p.waitFor();
        String stdout = IOUtils.toString(p.getInputStream(), Charset.defaultCharset());
        String stderr = IOUtils.toString(p.getErrorStream(), Charset.defaultCharset());
        LOG.debug("command: {}", command);
        LOG.debug("stdout: {}", stdout);
        LOG.debug("stderr: {}", stderr);
    }

    private String getCsrFileName(int deviceId) {
        String userFileName = getFileBaseName(deviceId);
        return String.format(csrFormatString, userFileName);
    }

    private String getCertificateFileName(int deviceId) {
        String userFileName = getFileBaseName(deviceId);
        return String.format(certFormatString, userFileName);
    }

    private String getFileBaseName(int deviceId) {
        return String.format("user_%d", deviceId);
    }
}
