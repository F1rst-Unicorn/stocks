package de.njsm.stocks.server.v2.web.data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import de.njsm.stocks.server.v2.business.StatusCode;
import de.njsm.stocks.server.v2.business.json.StatusCodeDeserialiser;
import de.njsm.stocks.server.v2.business.json.StatusCodeSerialiser;

import javax.xml.bind.annotation.XmlRootElement;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        creatorVisibility = JsonAutoDetect.Visibility.NONE)
@XmlRootElement
public class Response {

    @JsonSerialize(using = StatusCodeSerialiser.class)
    @JsonDeserialize(using = StatusCodeDeserialiser.class)
    public StatusCode status;

    public Response(StatusCode status) {
        this.status = status;
    }

    public Response() {
    }
}
