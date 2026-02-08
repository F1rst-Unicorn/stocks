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

package de.njsm.stocks.server.v2.web;

import de.njsm.stocks.common.api.DataResponse;
import de.njsm.stocks.common.api.StatusCode;
import de.njsm.stocks.server.v2.business.TicketAuthoriser;
import de.njsm.stocks.server.v2.business.data.ClientTicket;
import fj.data.Validation;
import jakarta.ws.rs.core.MediaType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.annotation.RequestScope;


@RequestMapping(path = "v2/auth")
@RestController
@RequestScope
public class RegistrationEndpoint extends Endpoint {

    private static final Logger LOG = LogManager.getLogger(RegistrationEndpoint.class);

    private final TicketAuthoriser authoriser;

    public RegistrationEndpoint(TicketAuthoriser authoriser) {
        this.authoriser = authoriser;
    }

    /**
     * Get a new user certificate
     * @return A response containing the new user certificate
     */
    @PostMapping(
            path = "newuser",
            consumes = MediaType.APPLICATION_FORM_URLENCODED,
            produces = MediaType.APPLICATION_JSON
    )
    public DataResponse<String> getNewCertificate(@RequestParam("device") int device,
                                                  @RequestParam("token") String token,
                                                  @RequestParam("csr") String csr){

        LOG.info("Got new certificate request for device id " + device);

        if (isValid(device, "device") &&
                isValid(token, "token") &&
                isValid(csr, "csr")) {

            Validation<StatusCode, String> response = authoriser.handleTicket(ClientTicket.builder()
                    .deviceId(device)
                    .ticket(token)
                    .pemFile(csr)
                    .build());

            return new DataResponse<>(response);
        } else {
            return new DataResponse<>(Validation.fail(StatusCode.INVALID_ARGUMENT));
        }
    }
}
