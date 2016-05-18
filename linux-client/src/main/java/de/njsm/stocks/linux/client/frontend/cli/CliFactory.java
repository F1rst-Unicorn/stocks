package de.njsm.stocks.linux.client.frontend.cli;

import de.njsm.stocks.linux.client.frontend.CertificateGenerator;
import de.njsm.stocks.linux.client.frontend.MainHandler;
import de.njsm.stocks.linux.client.frontend.UIFactory;
import de.njsm.stocks.linux.client.frontend.ConfigGenerator;

public class CliFactory extends UIFactory {

    @Override
    public ConfigGenerator getInteractor() {
        return new CliConfigGenerator();
    }

    @Override
    public CertificateGenerator getCertGenerator() {
        return new CliCertificateGenerator();
    }

    @Override
    public MainHandler getMainHandler() {
        return new CliMainHandler();
    }
}
