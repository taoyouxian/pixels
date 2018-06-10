package cn.edu.ruc.iir.pixels.presto;

import com.facebook.presto.spi.ColumnHandle;
import com.facebook.presto.spi.ConnectorTableLayoutHandle;
import com.facebook.presto.spi.predicate.TupleDomain;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class PixelsTableLayoutHandle
        implements ConnectorTableLayoutHandle {
    private final PixelsTableHandle table;

    private TupleDomain<ColumnHandle> constraint;

    @JsonCreator
    public PixelsTableLayoutHandle(@JsonProperty("table") PixelsTableHandle table,
                                   @JsonProperty("constraint") TupleDomain<ColumnHandle> constraint)

    {
        this.table = requireNonNull(table, "table is null");
        this.constraint = requireNonNull(constraint, "constraint is null");
    }

    @JsonProperty
    public TupleDomain<ColumnHandle> getConstraint() {
        return constraint;
    }

    public void setConstraint(TupleDomain<ColumnHandle> constraint)
    {
        this.constraint = constraint;
    }

    @JsonProperty
    public PixelsTableHandle getTable() {
        return table;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PixelsTableLayoutHandle that = (PixelsTableLayoutHandle) o;
        return Objects.equals(table, that.table) && Objects.equals(constraint, that.constraint);
    }

    @Override
    public int hashCode() {
        return Objects.hash(table, constraint);
    }

    @Override
    public String toString() {
        return "PixelsTableLayoutHandle{" +
                "table=" + table +
                ", constraint=" + constraint +
                '}';
    }
}
