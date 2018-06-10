package cn.edu.ruc.iir.pixels.presto;

import com.facebook.presto.spi.ConnectorTableHandle;
import com.facebook.presto.spi.SchemaTableName;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Joiner;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * @version V1.0
 * @Package: cn.edu.ruc.iir.pixels.presto
 * @ClassName: PixelsTableHandle
 * @Description:
 * @author: tao
 * @date: Create in 2018-01-19 15:21
 **/
public final class PixelsTableHandle
        implements ConnectorTableHandle {
    private final String connectorId;
    private final String schemaName;
    private final String tableName;
    private final String path;

    @JsonCreator
    public PixelsTableHandle(
            @JsonProperty("connectorId") String connectorId,
            @JsonProperty("schemaName") String schemaName,
            @JsonProperty("tableName") String tableName,
            @JsonProperty("path") String path) {
        this.connectorId = requireNonNull(connectorId, "connectorId is null");
        this.schemaName = requireNonNull(schemaName, "schemaName is null");
        this.tableName = requireNonNull(tableName, "tableName is null");
        this.path = requireNonNull(path, "path is null");
    }

    @JsonProperty
    public String getConnectorId() {
        return connectorId;
    }

    @JsonProperty
    public String getSchemaName() {
        return schemaName;
    }

    @JsonProperty
    public String getTableName() {
        return tableName;
    }

    @JsonProperty
    public String getPath() {
        return path;
    }

    public SchemaTableName toSchemaTableName() {
        return new SchemaTableName(schemaName, tableName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(connectorId, schemaName, tableName, path);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }

        PixelsTableHandle other = (PixelsTableHandle) obj;
        return Objects.equals(this.connectorId, other.connectorId) &&
                Objects.equals(this.schemaName, other.schemaName) &&
                Objects.equals(this.tableName, other.tableName) &&
                Objects.equals(this.path, other.path);
    }

    @Override
    public String toString() {
        return Joiner.on(":").join(connectorId, schemaName, tableName, path);
    }
}
