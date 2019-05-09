package de.njsm.stocks.android.network.server.data;


import androidx.annotation.NonNull;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import de.njsm.stocks.android.network.server.StatusCode;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DataResponse<T> extends Response {

    public T data;

    public DataResponse(StatusCode status, T data) {
        super(status);
        this.data = data;
    }

    public DataResponse() {
        super();
    }

    @Override
    @NonNull
    public String toString() {
        return "DataResponse{" +
                "data=" + data +
                ", status=" + status +
                '}';
    }
}
