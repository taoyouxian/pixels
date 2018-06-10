package cn.edu.ruc.iir.pixels.presto;

import com.facebook.presto.spi.connector.Connector;
import com.facebook.presto.spi.connector.ConnectorMetadata;
import com.facebook.presto.spi.connector.ConnectorSplitManager;
import com.facebook.presto.spi.connector.ConnectorTransactionHandle;
import com.facebook.presto.spi.transaction.IsolationLevel;
import io.airlift.bootstrap.LifeCycleManager;
import io.airlift.log.Logger;

import javax.inject.Inject;

import static cn.edu.ruc.iir.pixels.presto.PixelsTransactionHandle.INSTANCE;
import static java.util.Objects.requireNonNull;

public class PixelsConnector
        implements Connector {
    private static final Logger log = Logger.get(PixelsConnector.class);

    private final LifeCycleManager lifeCycleManager;
    private final PixelsMetadata metadata;
    private final PixelsSplitManager splitManager;
    private final PixelsPageSourceProvider pageSourceProvider;

    @Inject
    public PixelsConnector(
            LifeCycleManager lifeCycleManager,
            PixelsMetadata metadata,
            PixelsSplitManager splitManager,
            PixelsPageSourceProvider pageSourceProvider) {
        this.lifeCycleManager = requireNonNull(lifeCycleManager, "lifeCycleManager is null");
        this.metadata = requireNonNull(metadata, "metadata is null");
        this.splitManager = requireNonNull(splitManager, "splitManager is null");
        this.pageSourceProvider = requireNonNull(pageSourceProvider, "recordSetProvider is null");
    }

    @Override
    public ConnectorTransactionHandle beginTransaction(IsolationLevel isolationLevel, boolean readOnly) {
        return INSTANCE;
    }

    @Override
    public ConnectorMetadata getMetadata(ConnectorTransactionHandle transactionHandle) {
        return metadata;
    }

    @Override
    public ConnectorSplitManager getSplitManager() {
        return splitManager;
    }

    @Override
    public PixelsPageSourceProvider getPageSourceProvider() {
        return pageSourceProvider;
    }

    @Override
    public final void shutdown() {
        try {
            lifeCycleManager.stop();
        } catch (Exception e) {
            log.error(e, "Error shutting down connector");
        }
    }
}
