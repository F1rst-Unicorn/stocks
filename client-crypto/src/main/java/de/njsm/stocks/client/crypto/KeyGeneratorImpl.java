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

import de.njsm.stocks.client.business.KeyGenerator;
import de.njsm.stocks.client.business.SubsystemException;
import de.njsm.stocks.client.business.entities.KeyGenerationParameters;
import de.njsm.stocks.client.business.entities.Principals;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;

import javax.inject.Inject;
import javax.security.auth.x500.X500Principal;
import java.io.IOException;
import java.io.StringWriter;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

class KeyGeneratorImpl implements KeyGenerator {

    @Inject
    KeyGeneratorImpl() {
    }

    @Override
    public KeyPair generateKeyPair(KeyGenerationParameters parameters) throws SubsystemException {
        try {
            KeyPairGenerator gen = KeyPairGenerator.getInstance(parameters.keyAlgorithm());
            gen.initialize(parameters.keySize());
            return gen.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new CryptoException("generating key", e);
        }
    }

    @Override
    public String generateCertificateSigningRequest(KeyPair keyPair, Principals principals, KeyGenerationParameters parameters) {
        try {
            X500Principal x500Principals = new X500Principal("CN=" + principals.asCommonName());
            PKCS10CertificationRequest request =
                    new JcaPKCS10CertificationRequestBuilder(
                            x500Principals,
                            keyPair.getPublic())
                            .build(new JcaContentSignerBuilder(parameters.signingAlgorithm()).build(keyPair.getPrivate()));

            StringWriter buf = new StringWriter();
            JcaPEMWriter writer = new JcaPEMWriter(buf);
            writer.writeObject(request);
            writer.close();
            return buf.toString();
        } catch (IOException | OperatorCreationException | IllegalArgumentException e) {
            throw new CryptoException("generating csr", e);
        }
    }
}
