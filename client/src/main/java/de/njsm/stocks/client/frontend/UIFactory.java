package de.njsm.stocks.client.frontend;

import de.njsm.stocks.client.config.Configuration;

public abstract class UIFactory {

    public abstract ConfigGenerator getConfigActor();

    public abstract CertificateGenerator getCertGenerator();

    public abstract MainHandler getMainHandler(Configuration c);
}
