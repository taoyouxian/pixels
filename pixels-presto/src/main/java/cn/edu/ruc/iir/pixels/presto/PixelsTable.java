package cn.edu.ruc.iir.pixels.presto;

import com.facebook.presto.spi.ColumnMetadata;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * @version V1.0
 * @Package: cn.edu.ruc.iir.pixels.presto
 * @ClassName: PixelsTable
 * @Description:
 * @author: tao
 * @date: Create in 2018-01-19 15:16
 **/
public class PixelsTable {
    private final PixelsTableHandle table;
    private final PixelsTableLayoutHandle tableLayout;
    private final List<PixelsColumnHandle> columns;
    private final List<ColumnMetadata> columnsMetadata;

    @JsonCreator
    public PixelsTable(
            @JsonProperty("table") PixelsTableHandle table,
            @JsonProperty("tableLayout") PixelsTableLayoutHandle tableLayout,
            @JsonProperty("columns") List<PixelsColumnHandle> columns,
            @JsonProperty("columnsMetadata") List<ColumnMetadata> columnsMetadata) {
        this.table = requireNonNull(table, "table is null");
        this.tableLayout = requireNonNull(tableLayout, "name is null");
        this.columns = requireNonNull(columns, "columns is null");
        this.columnsMetadata = requireNonNull(columnsMetadata, "columnsMetadata is null");
    }

    @JsonProperty
    public PixelsTableHandle getTableHandle() {
        return table;
    }

    @JsonProperty
    public PixelsTableLayoutHandle getTableLayout() {
        return tableLayout;
    }

    @JsonProperty
    public List<PixelsColumnHandle> getColumns() {
        return columns;
    }

    @JsonProperty
    public List<ColumnMetadata> getColumnsMetadata() {
        return columnsMetadata;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PixelsTable that = (PixelsTable) o;

        return Objects.equals(table, that.table) && Objects.equals(tableLayout, that.tableLayout)
                && Objects.equals(columns, that.columns) && Objects.equals(columnsMetadata, that.columnsMetadata);
    }

    @Override
    public int hashCode() {
        return Objects.hash(table, tableLayout, columns, columnsMetadata);
    }

    @Override
    public String toString() {
        return "PixelsTable{" +
                "table=" + table +
                ", tableLayout=" + tableLayout +
                ", columns=" + columns +
                ", columnsMetadata=" + columnsMetadata +
                '}';
    }
}
