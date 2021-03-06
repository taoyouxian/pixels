package cn.edu.ruc.iir.pixels.presto;

import cn.edu.ruc.iir.pixels.presto.client.MetadataService;
import cn.edu.ruc.iir.pixels.presto.impl.FSFactory;
import cn.edu.ruc.iir.pixels.presto.impl.PixelsPrestoConfig;
import cn.edu.ruc.iir.pixels.presto.impl.PixelsMetadataReader;
import com.facebook.presto.spi.type.Type;
import com.facebook.presto.spi.type.TypeManager;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.FromStringDeserializer;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Scopes;

import javax.inject.Inject;

import static com.facebook.presto.spi.type.TypeSignature.parseTypeSignature;
import static com.google.common.base.Preconditions.checkArgument;
import static io.airlift.configuration.ConfigBinder.configBinder;
import static io.airlift.json.JsonBinder.jsonBinder;
import static io.airlift.json.JsonCodec.listJsonCodec;
import static io.airlift.json.JsonCodecBinder.jsonCodecBinder;
import static java.util.Objects.requireNonNull;

public class PixelsModule
        implements Module {
    private final String connectorId;
    private final TypeManager typeManager;

    public PixelsModule(String connectorId, TypeManager typeManager) {
        this.connectorId = requireNonNull(connectorId, "connector id is null");
        this.typeManager = requireNonNull(typeManager, "typeManager is null");
    }

    @Override
    public void configure(Binder binder) {
        binder.bind(TypeManager.class).toInstance(typeManager);

        binder.bind(PixelsConnector.class).in(Scopes.SINGLETON);
        binder.bind(PixelsConnectorId.class).toInstance(new PixelsConnectorId(connectorId));
        binder.bind(PixelsMetadata.class).in(Scopes.SINGLETON);
        binder.bind(FSFactory.class).in(Scopes.SINGLETON);
        binder.bind(PixelsMetadataReader.class).in(Scopes.SINGLETON);
        binder.bind(MetadataService.class).in(Scopes.SINGLETON);
//        binder.bind(ConnectorPageSourceProvider.class).to(PixelsPageSourceProvider.class).in(Scopes.SINGLETON);
//        binder.bind(ConnectorPageSource.class).to(PixelsPageSource.class).in(Scopes.SINGLETON);
        binder.bind(PixelsSplitManager.class).in(Scopes.SINGLETON);
//        binder.bind(PixelsHandleResolver.class).in(Scopes.SINGLETON);
        binder.bind(PixelsPageSourceProvider.class).in(Scopes.SINGLETON);
//        binder.bind(PixelsPageSource.class).in(Scopes.SINGLETON);
        configBinder(binder).bindConfig(PixelsPrestoConfig.class);

        jsonBinder(binder).addDeserializerBinding(Type.class).to(TypeDeserializer.class);
        jsonCodecBinder(binder).bindMapJsonCodec(String.class, listJsonCodec(PixelsTable.class));
    }

    public static final class TypeDeserializer
            extends FromStringDeserializer<Type> {
        private static final long serialVersionUID = -8192232141190978355L;
        private final TypeManager typeManager;

        @Inject
        public TypeDeserializer(TypeManager typeManager) {
            super(Type.class);
            this.typeManager = requireNonNull(typeManager, "typeManager is null");
        }

        @Override
        protected Type _deserialize(String value, DeserializationContext context) {
            Type type = typeManager.getType(parseTypeSignature(value));
            checkArgument(type != null, "Unknown type %s", value);
            return type;
        }
    }
}
