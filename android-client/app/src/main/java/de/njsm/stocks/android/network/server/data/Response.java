package de.njsm.stocks.android.network.server.data;


import androidx.annotation.NonNull;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import de.njsm.stocks.android.network.server.StatusCode;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Response {

    public StatusCode status;

    public Response(StatusCode status) {
        this.status = status;
    }

    public Response() {
    }

    @NonNull
    @Override
    public String toString() {
        return "Response{" + "status=" + status +'}';
    }
}
