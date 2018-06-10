package cn.edu.ruc.iir.pixels.presto;

import com.facebook.presto.spi.ConnectorHandleResolver;
import com.facebook.presto.spi.connector.Connector;
import com.facebook.presto.spi.connector.ConnectorContext;
import com.facebook.presto.spi.connector.ConnectorFactory;
import com.google.common.base.Throwables;
import com.google.inject.Injector;
import io.airlift.bootstrap.Bootstrap;
import io.airlift.json.JsonModule;
import io.airlift.log.Logger;

import java.util.Map;

import static java.util.Objects.requireNonNull;

public class PixelsConnectorFactory
        implements ConnectorFactory {

    private Logger logger = Logger.get(PixelsConnectorFactory.class);

    private final String name = "pixels-presto";

    public PixelsConnectorFactory()  {
        logger.debug("Connector " + name + " initialized.");
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ConnectorHandleResolver getHandleResolver() {
        return new PixelsHandleResolver();
    }

    @Override
    public Connector create(final String connectorId, Map<String, String> requiredConfig, ConnectorContext context) {
        requireNonNull(requiredConfig, "requiredConfig is null");
        try {
            // A plugin is not required to use Guice; it is just very convenient
            Bootstrap app = new Bootstrap(
                    new JsonModule(),
                    new PixelsModule(connectorId, context.getTypeManager()));

            Injector injector = app
                    .strictConfig()
                    .doNotInitializeLogging()
                    .setRequiredConfigurationProperties(requiredConfig)
                    .initialize();

            return injector.getInstance(PixelsConnector.class);
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }
}
