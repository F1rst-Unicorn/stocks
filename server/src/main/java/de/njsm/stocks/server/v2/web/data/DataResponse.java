package de.njsm.stocks.server.v2.web.data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import de.njsm.stocks.server.v2.business.StatusCode;
import fj.data.Validation;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        creatorVisibility = JsonAutoDetect.Visibility.NONE)
@XmlRootElement
public class DataResponse<T> extends Response {

    public List<T> data;

    public DataResponse(Validation<StatusCode, List<T>> option) {
        if (option.isSuccess()) {
            status = StatusCode.SUCCESS;
            data = option.success();
        } else {
            status = option.fail();
            data = null;
        }
    }
}
