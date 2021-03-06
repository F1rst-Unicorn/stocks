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

package de.njsm.stocks.client.init;

import de.njsm.stocks.client.exceptions.CryptoException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Callable;

public class AsyncKeyGenerator implements Callable<KeyPair> {

    private static final Logger LOG = LogManager.getLogger(AsyncKeyGenerator.class);

    private String keyAlgorithmName;
    private int keySize;

    public AsyncKeyGenerator(String keyAlgorithmName, int keySize) {
        this.keyAlgorithmName = keyAlgorithmName;
        this.keySize = keySize;
    }

    @Override
    public KeyPair call() throws Exception {
        try {
            LOG.info("Start keypair generation");
            KeyPairGenerator gen = KeyPairGenerator.getInstance(keyAlgorithmName);
            gen.initialize(keySize);
            LOG.info("Initialised");
            return gen.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            LOG.error(e);
            throw new CryptoException("Crypto algorithm not available");
        } finally {
            LOG.info("Keypair generation finished");
        }
    }
}
