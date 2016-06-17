package de.njsm.stocks.server.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
@XmlRootElement
class Update extends Data {

        public String table;
        @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd hh:mm:ss")
        public Date lastUpdate;

}
