package de.njsm.stocks.android.network.server.data;


import androidx.annotation.NonNull;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import de.njsm.stocks.android.network.server.StatusCode;

import java.util.Arrays;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ListResponse<T> extends DataResponse<T[]> {

    public ListResponse(StatusCode status, T[] data) {
        super(status, data);
    }

    public ListResponse() {
        super();
    }

    @NonNull
    @Override
    public String toString() {
        return "ListResponse{" +
                "data=" + Arrays.toString(data) +
                ", status=" + status +
                '}';
    }
}
