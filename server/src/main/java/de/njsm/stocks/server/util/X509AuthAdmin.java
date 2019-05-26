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
import de.njsm.stocks.server.v2.business.StatusCode;
import de.njsm.stocks.server.v2.web.servlet.PrincipalFilter;
import fj.data.Validation;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;

public class X509AuthAdmin implements AuthAdmin, HystrixWrapper<Void, Exception> {

    private static final Logger LOG = LogManager.getLogger(X509AuthAdmin.class);

    private String csrFormatString;

    private String certFormatString;

    private String caRootDirectory;

    private String reloadCommand;

    private String resourceIdentifier;

    public X509AuthAdmin(String caRootDirectory,
                         String reloadCommand,
                         String resourceIdentifier) {
        this.caRootDirectory = caRootDirectory;
        this.csrFormatString = caRootDirectory + "/intermediate/csr/%s.csr.pem";
        this.certFormatString = caRootDirectory + "/intermediate/certs/%s.cert.pem";
        this.reloadCommand = reloadCommand;
        this.resourceIdentifier = resourceIdentifier;
    }

    @Override
    public synchronized StatusCode saveCsr(int deviceId, String content) {
        return runCommand(dummy -> {
            FileOutputStream csrFile = new FileOutputStream(getCsrFileName(deviceId));
            IOUtils.write(content, csrFile);
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
            Process p = Runtime.getRuntime().exec(command);
            p.waitFor();
            return StatusCode.SUCCESS;
        });
    }

    @Override
    public synchronized Validation<StatusCode, String> getCertificate(int deviceId) {
        return runFunction(dummy -> {
            FileInputStream input = new FileInputStream(getCertificateFileName(deviceId));
            String result = IOUtils.toString(input);
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
            Runtime.getRuntime().exec(command).waitFor();
            refreshCrl();
            return StatusCode.SUCCESS;
        });
    }

    @Override
    public String getResourceIdentifier() {
        return resourceIdentifier;
    }

    @Override
    public StatusCode getDefaultErrorCode() {
        return StatusCode.CA_UNREACHABLE;
    }

    @Override
    public <O> ProducerWithExceptions<Validation<StatusCode, O>, Exception>
    wrap(FunctionWithExceptions<Void, Validation<StatusCode, O>, Exception> client) {
        return () -> {
            return client.apply(null);
        };
    }

    private void refreshCrl() {
        runCommand(dummy -> {
            String crlCommand = String.format("openssl ca " +
                            "-config %s/intermediate/openssl.cnf " +
                            "-gencrl " +
                            "-out %s/intermediate/crl/intermediate.crl.pem",
                    caRootDirectory,
                    caRootDirectory);

            Runtime.getRuntime().exec(crlCommand).waitFor();

            FileOutputStream out = new FileOutputStream(caRootDirectory + "/intermediate/crl/whole.crl.pem");
            IOUtils.copy(new FileInputStream(caRootDirectory + "/crl/ca.crl.pem"), out);
            IOUtils.copy(new FileInputStream(caRootDirectory + "/intermediate/crl/intermediate.crl.pem"), out);
            out.close();

            Runtime.getRuntime().exec(reloadCommand).waitFor();
            return null;
        });
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
