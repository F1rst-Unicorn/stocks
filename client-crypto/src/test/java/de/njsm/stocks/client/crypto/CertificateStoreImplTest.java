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

import static org.junit.jupiter.api.Assertions.assertTrue;

class CertificateStoreImplTest {

    private CertificateStoreImpl uut;

    private KeyPair keyPair;

    private PemFile certificate;

    @BeforeEach
    void setup() throws NoSuchAlgorithmException, CertificateException, IOException, OperatorCreationException {
        keyPair = generateKey();
        certificate = selfSignKey(keyPair);
        uut = new CertificateStoreImpl(new TestFileInteractor());
    }

    @AfterEach
    void tearDown() {
        new File("keystore").delete();
    }

    @Test
    void storingCertificateWorks() throws KeyStoreException {
        uut.storeCertificate(Collections.singletonList(certificate));

        assertTrue(uut.getKeystore().isCertificateEntry(certificate.name()));
    }

    @Test
    void storingKeyWorks() throws KeyStoreException {

        uut.storeKey(keyPair, Collections.singletonList(certificate));

        assertTrue(uut.getKeystore().isKeyEntry("client"));
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
}
