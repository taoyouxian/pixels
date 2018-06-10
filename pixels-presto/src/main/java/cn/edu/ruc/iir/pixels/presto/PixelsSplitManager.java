package cn.edu.ruc.iir.pixels.presto;

import cn.edu.ruc.iir.pixels.presto.client.MetadataService;
import cn.edu.ruc.iir.pixels.presto.impl.FSFactory;
import com.facebook.presto.spi.*;
import com.facebook.presto.spi.connector.ConnectorSplitManager;
import com.facebook.presto.spi.connector.ConnectorTransactionHandle;
import com.facebook.presto.spi.predicate.TupleDomain;
import io.airlift.log.Logger;
import org.apache.hadoop.fs.Path;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * @version V1.0
 * @Package: cn.edu.ruc.iir.pixels.presto
 * @ClassName: PixelsSplitManager
 * @Description:
 * @author: tao
 * @date: Create in 2018-01-20 19:16
 **/
public class PixelsSplitManager
        implements ConnectorSplitManager {
    private final Logger log = Logger.get(PixelsSplitManager.class);
    private final String connectorId;
    //    private final PixelsMetadataReader pixelsMetadataReader;
    private final FSFactory fsFactory;
    private final MetadataService metadataService;

    @Inject
    public PixelsSplitManager(PixelsConnectorId connectorId, MetadataService metadataService, FSFactory fsFactory) {
        this.connectorId = requireNonNull(connectorId, "connectorId is null").toString();
//        this.pixelsMetadataReader = requireNonNull(PixelsMetadataReader.Instance(), "pixelsMetadataReader is null");
        this.fsFactory = requireNonNull(fsFactory, "fsFactory is null");
        this.metadataService = requireNonNull(metadataService, "metadataService is null");
    }

    @Override
    public ConnectorSplitSource getSplits(ConnectorTransactionHandle handle, ConnectorSession session, ConnectorTableLayoutHandle layout, SplitSchedulingStrategy splitSchedulingStrategy) {
        PixelsTableLayoutHandle layoutHandle = (PixelsTableLayoutHandle) layout;
        PixelsTableHandle tableHandle = layoutHandle.getTable();
//        PixelsTable table = pixelsMetadataReader.getTable(connectorId, tableHandle.getSchemaName(), tableHandle.getTableName());
        // this can happen if table is removed during a query
//        checkState(table != null, "Table %s.%s no longer exists", tableHandle.getSchemaName(), tableHandle.getTableName());

        List<ConnectorSplit> splits = new ArrayList<>();

        TupleDomain<PixelsColumnHandle> constraint = layoutHandle.getConstraint()
                .transform(PixelsColumnHandle.class::cast);
        // push down
//        Map<PixelsColumnHandle, Domain> domains = constraint.getDomains().get();
//        log.info("domains size: " + domains.size());
//        log.info("domains values: " + domains.values());
//        List<PixelsColumnHandle> indexedColumns = new ArrayList<>();
        // compose partitionId by using indexed column
//        for (Map.Entry<PixelsColumnHandle, Domain> entry : domains.entrySet()) {
//            PixelsColumnHandle column = (PixelsColumnHandle) entry.getKey();
//            log.info("column: " + column.getColumnName() + " " + column.getColumnType());
//            Domain domain = entry.getValue();
//            if (domain.isSingleValue()) {
//                indexedColumns.add(column);
//                // Only one indexed column predicate can be pushed down.
//            }
//            log.info("domain: " + domain.isSingleValue());
//        }
//        log.info("indexedColumns: " + indexedColumns.toString());
        List<Layout> catalogList = metadataService.getLayoutsByTblName(tableHandle.getTableName());
        List<Path> files = new ArrayList<>();
        for (Layout l : catalogList) {
            files.addAll(fsFactory.listFiles(l.getLayInitPath()));
        }

        files.forEach(file -> splits.add(new PixelsSplit(connectorId,
                tableHandle.getSchemaName(),
                tableHandle.getTableName(),
                file.toString(), 0, -1,
                fsFactory.getBlockLocations(file, 0, Long.MAX_VALUE), constraint)));

        Collections.shuffle(splits);

//        log.info("files forEach: " + files.size());
        return new FixedSplitSource(splits);
    }
}