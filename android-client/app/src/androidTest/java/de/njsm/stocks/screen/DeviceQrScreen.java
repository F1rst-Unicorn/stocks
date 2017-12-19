package de.njsm.stocks.screen;

public class DeviceQrScreen extends AbstractScreen {

    @Override
    public DeviceScreen pressBack() {
        super.pressBack();
        return new DeviceScreen();
    }
}
