package de.njsm.stocks.linux.client.frontend;

import de.njsm.stocks.linux.client.Configuration;

public abstract class UIFactory {

    public abstract ConfigGenerator getInteractor();

    public abstract CertificateGenerator getCertGenerator();

    public abstract MainHandler getMainHandler(Configuration c);
}
