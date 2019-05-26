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

package de.njsm.stocks.client.frontend.cli;

import de.njsm.stocks.client.frontend.CertificateGenerator;
import de.njsm.stocks.client.frontend.cli.service.InputReader;
import de.njsm.stocks.client.service.TimeProviderImpl;

public class CliCertificateGenerator implements CertificateGenerator {

    private final InputReader reader;

    CliCertificateGenerator(InputReader reader) {
        this.reader = reader;
    }

    @Override
    public String getTicket() {
        return reader.next("Please give the ticket you got from your friend: ");
    }

    @Override
    public String getCaFingerprint() {
        return reader.next("Please give the fingerprint you got from your friend: ");
    }

    @Override
    public String getUsername() {
        String name = reader.next("Please enter your name: ");

        while (!InputReader.isNameValid(name)) {
            name = reader.next("Invalid name, try again: ");
        }
        return name;
    }

    @Override
    public String getDeviceName() {
        String name = reader.next("Please enter your device's name: ");

        while (!InputReader.isNameValid(name)) {
            name = reader.next("Invalid name, try again: ");
        }
        return name;
    }

    @Override
    public int getUserId() {
        return getId("User");
    }

    @Override
    public int getDeviceId() {
        return getId("device");
    }

    private int getId(String key) {
        String prompt = String.format("Please give the Id for the %s: ", key);
        return reader.nextInt(prompt);
    }
}
