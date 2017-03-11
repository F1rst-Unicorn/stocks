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
