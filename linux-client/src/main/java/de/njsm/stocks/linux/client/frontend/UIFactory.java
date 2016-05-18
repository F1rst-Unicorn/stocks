package de.njsm.stocks.linux.client.frontend;

public abstract class UIFactory {

    public abstract ConfigGenerator getInteractor();

    public abstract CertificateGenerator getCertGenerator();

    public abstract MainHandler getMainHandler();
}
