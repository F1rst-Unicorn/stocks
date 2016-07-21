package de.njsm.stocks.client.frontend.cli;

import de.njsm.stocks.client.frontend.CertificateGenerator;
import de.njsm.stocks.client.frontend.MainHandler;
import de.njsm.stocks.client.frontend.UIFactory;
import de.njsm.stocks.client.Configuration;
import de.njsm.stocks.client.frontend.ConfigGenerator;

public class CliFactory extends UIFactory {

    @Override
    public ConfigGenerator getConfigActor() {
        return new CliConfigGenerator();
    }

    @Override
    public CertificateGenerator getCertGenerator() {
        return new CliCertificateGenerator();
    }

    @Override
    public MainHandler getMainHandler(Configuration c) {
        return new CliMainHandler(c);
    }
}
