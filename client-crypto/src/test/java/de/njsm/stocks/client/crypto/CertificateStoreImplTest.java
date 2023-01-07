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

package de.njsm.stocks.client.crypto;

import de.njsm.stocks.client.business.TestFileInteractor;
import de.njsm.stocks.client.business.entities.PemFile;
import org.apache.logging.log4j.core.util.StringBuilderWriter;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.jupiter.api.Assertions.*;

class CertificateStoreImplTest {

    private CertificateStoreImpl uut;

    private KeyPair keyPair;

    private PemFile certificate;

    private File keystore;

    @BeforeEach
    void setup() throws NoSuchAlgorithmException, CertificateException, IOException, OperatorCreationException {
        keyPair = generateKey();
        certificate = selfSignKey(keyPair);
        uut = new CertificateStoreImpl(new TestFileInteractor(new File(".")));
        keystore = new File("keystore");
    }

    @AfterEach
    void tearDown() {
        keystore.delete();
    }

    @Test
    void storingCertificateWorks() throws KeyStoreException {
        uut.storeCertificates(Collections.singletonList(certificate));

        assertTrue(uut.getKeystore().isCertificateEntry(certificate.name()));
    }

    @Test
    void clearingStoreWorks() {
        uut.storeCertificates(Collections.singletonList(certificate));

        uut.clear();

        assertFalse(keystore.delete());
    }

    @Test
    void clearingIsIdempotent() {
        uut.storeCertificates(Collections.singletonList(certificate));

        uut.clear();
        uut.clear();
    }

    @Test
    void storingKeyWorks() throws KeyStoreException {

        uut.storeKey(keyPair, Collections.singletonList(certificate));

        assertTrue(uut.getKeystore().isKeyEntry("client"));
    }

    @Test
    void fingerprintIsComputedCorrectly() {
        uut.storeCertificates(Collections.singletonList(PemFile.create("ca", TEST_CERTIFICATE)));

        String actual = uut.getCaCertificateFingerprint();

        assertEquals("B7:F7:94:CD:46:8E:4A:E6:50:74:C3:70:5D:4D:98:55:4A:11:88:46:3B:63:63:B7:F7:8A:D3:13:12:8B:F9:10", actual);
    }

    @Test
    void gettingKeyManagerWorks() {
        KeyManagerFactory keyManager = uut.getKeyManager();

        assertThat(keyManager.getKeyManagers().length, greaterThan(0));
    }


    @Test
    void gettingTrustManagerWorks() {
        TrustManagerFactory trustManager = uut.getTrustManager();

        assertThat(trustManager.getTrustManagers().length, greaterThan(0));
    }

    private KeyPair generateKey() throws NoSuchAlgorithmException {
        KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
        gen.initialize(512);
        return gen.generateKeyPair();
    }

    private PemFile selfSignKey(KeyPair keyPair) throws OperatorCreationException, CertificateException, IOException {
        final Instant now = Instant.now();
        final Date notBefore = Date.from(now);
        final Date notAfter = Date.from(now.plus(Duration.ofDays(2)));
        final ContentSigner contentSigner = new JcaContentSignerBuilder("SHA256WithRSA").build(keyPair.getPrivate());
        final X500Name x500Name = new X500Name("CN=client");
        final X509v3CertificateBuilder certificateBuilder =
                new JcaX509v3CertificateBuilder(x500Name,
                        BigInteger.valueOf(now.toEpochMilli()),
                        notBefore,
                        notAfter,
                        x500Name,
                        keyPair.getPublic())
                        .addExtension(Extension.basicConstraints, true, new BasicConstraints(true));

        Certificate certificate = new JcaX509CertificateConverter()
                .setProvider(new BouncyCastleProvider()).getCertificate(certificateBuilder.build(contentSigner));

        StringBuilder buf = new StringBuilder();
        JcaPEMWriter writer = new JcaPEMWriter(new StringBuilderWriter(buf));
        writer.writeObject(certificate);
        writer.flush();
        writer.close();
        return PemFile.create("client", buf.toString());
    }

    private static final String TEST_CERTIFICATE = "-----BEGIN CERTIFICATE-----\n" +
            "MIIFkDCCA3igAwIBAgIJAIMQuUirOmYyMA0GCSqGSIb3DQEBCwUAMFQxCzAJBgNV\n" +
            "BAYTAkNIMQ8wDQYDVQQIDAZadXJpY2gxDzANBgNVBAcMBlp1cmljaDEPMA0GA1UE\n" +
            "CgwGc3RvY2tzMRIwEAYDVQQDDAlzdG9ja3MgQ0EwIBcNMTYwNjE5MTAwMDQ1WhgP\n" +
            "MzAxNTEwMjExMDAwNDVaMFQxCzAJBgNVBAYTAkNIMQ8wDQYDVQQIDAZadXJpY2gx\n" +
            "DzANBgNVBAcMBlp1cmljaDEPMA0GA1UECgwGc3RvY2tzMRIwEAYDVQQDDAlzdG9j\n" +
            "a3MgQ0EwggIiMA0GCSqGSIb3DQEBAQUAA4ICDwAwggIKAoICAQDaK8yFYWewRtSe\n" +
            "CpCIFZGI+Z9pc6gn8RoAbkBFtC3vtdN8CJkq/rfzi25wtv0u6BWLTPRmndfvqS0+\n" +
            "iXqx9E4js5lq3U5GdfRdaipYVJB6JbZcdX8bARs2Rx/jhu/v0hhi3LkYgUHt3Mkq\n" +
            "OhtCht3SxPNVl0OlARUg3j1uqA9towQi8DVPdgeixfpicJmti4CxKWc6WxuIsQDL\n" +
            "G0L5+D02BMVn85Vqe6ta2LVGzhu9uSNKHI3A984IuoQeKswuLR6HDWfjv7J7UFy8\n" +
            "GRMGdt7UIa1HdacOtEfWBFD1l3k+SL9ySG86ZfLGpx8JKCrUGbLJJ4Eq3EAcBIQC\n" +
            "TsJe629jexe1nK/4+Z1rBA3XIFpViOqF9C61oEjX3XElEC2XyV8RnjoTVrR27mQD\n" +
            "yYeDl2tO2ba33KIRjDOTtpATUhlT6e3+uMBq2Z+G5YYFYfHD1QS4MfrOv44fjXnX\n" +
            "IMVMwp5bFuzJ1Crdg7udsUneJpVCvLzBZcs92s2r6Z0+kK1/W8cNEaovgJOnqzUE\n" +
            "Nb+mEYKn/83bB0L37iVFkN2KAkxg3C+9X38rL+mwg+49/AwLR5G2k23uPmSnJwWX\n" +
            "gK8D6ZRLiFg+/KFjIpSgg9B9xhSAPkjc1oana46K/cumRb9QnO97IwPqflYZ5atz\n" +
            "i8yIUIcfLoeQZHYY0o1Q4ScOIy9gXQIDAQABo2MwYTAdBgNVHQ4EFgQUtDI9zlCO\n" +
            "07lHacbSkfyOYm7YdbowHwYDVR0jBBgwFoAUtDI9zlCO07lHacbSkfyOYm7Ydbow\n" +
            "DwYDVR0TAQH/BAUwAwEB/zAOBgNVHQ8BAf8EBAMCAYYwDQYJKoZIhvcNAQELBQAD\n" +
            "ggIBAEf4to0abfASb6a7DCXkGR4CacTb7v053A/QFDOxX0+6DIWlMzWm5g2jWoDq\n" +
            "ABk2FsazmGtQvtWUx7a++W/iqLlwc5TQLAAXm/AIcbYUvTepASxreBqfF4nJrVhc\n" +
            "kpVxfdcmbqDk5YLE0vyqjsxX0qQAGiNAQ0+QskauAf0oki+05PEzebY5NVkC4SLs\n" +
            "Wnb4yBY6ktRF3Gt7qbwsHL6pq58h42IeEMqPZ8OXtqLR84NHWAyw2Qwd4O9AQvZI\n" +
            "LdtMyVFDcqmfzi/BPHg7OQdqYsWJNw60RFMFDMMOQ0NOIJPQ4CONDoieeVpAgDwd\n" +
            "GkEmdqFF4Z3NZUZJmEB8LbNRfzpSsrrRV8pnBuF3ARA0HfRn+E+EJjDJo5Hg69HV\n" +
            "wvcikW/O4J/On6ZzH0kYXfRiOh9zlmTY5USDWmZKG5inNZQ65jsTVePUDIEGR8pf\n" +
            "gwik2BjRLt4T4uvQ/wAnD+Tn0SJAYxz3DqzWuKpK7sUY12X49xs9kW087IoRepk3\n" +
            "cdliDU9OLNbP9kiBFM1ZW6srawFZfCpnAEvqJTH5Geolu/kWxvu1+Q5yBYfqqVFR\n" +
            "KyKDHGUNpy44Yefo3fy/BUR9jOsEVZxFH8c6ZZQ9NOxq3Pg8585g63Arjb4kPbP+\n" +
            "hvqmNABmarg7dNiV+8C47ifvnS5uzMrM8cFfBNioKb3xbvaH\n" +
            "-----END CERTIFICATE-----\n";
}
