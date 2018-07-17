package de.njsm.stocks.server.v2.business.data;

import de.njsm.stocks.server.v2.business.StatusCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.internal.util.Producer;

public class VersionedData extends Data {

    private static final Logger LOG = LogManager.getLogger(VersionedData.class);

    public int version;

    public VersionedData(int id, int version) {
        super(id);
        this.version = version;
    }

    public VersionedData() {
        this.version = 1;
    }

    public StatusCode ifVersionEquals(int version, Producer<StatusCode> command) {
        if (this.version == version) {
            return command.call();
        } else {
            LOG.info("Tried to modify outdated food");
            LOG.debug("Requested: " + id + ", " + version);
            LOG.debug("In DTO: " + id + ", " + this.version);
            return StatusCode.INVALID_DATA_VERSION;

        }
    }
}
