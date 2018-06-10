package cn.edu.ruc.iir.pixels.presto;

import com.facebook.presto.spi.*;
import com.facebook.presto.spi.connector.ConnectorTransactionHandle;

public class PixelsHandleResolver
        implements ConnectorHandleResolver {
    @Override
    public Class<? extends ConnectorTableLayoutHandle> getTableLayoutHandleClass() {
        return PixelsTableLayoutHandle.class;
    }

    @Override
    public Class<? extends ConnectorTableHandle> getTableHandleClass() {
        return PixelsTableHandle.class;
    }

    @Override
    public Class<? extends ColumnHandle> getColumnHandleClass() {
        return PixelsColumnHandle.class;
    }

    @Override
    public Class<? extends ConnectorSplit> getSplitClass() {
        return PixelsSplit.class;
    }

    @Override
    public Class<? extends ConnectorTransactionHandle> getTransactionHandleClass() {
        return PixelsTransactionHandle.class;
    }
}
