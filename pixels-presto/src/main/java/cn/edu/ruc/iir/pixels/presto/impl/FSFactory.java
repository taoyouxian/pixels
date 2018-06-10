package cn.edu.ruc.iir.pixels.presto.impl;

import com.facebook.presto.spi.HostAddress;
import com.facebook.presto.spi.PrestoException;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import io.airlift.log.Logger;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.client.HdfsDataInputStream;
import org.apache.hadoop.hdfs.protocol.LocatedBlock;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static cn.edu.ruc.iir.pixels.presto.PixelsErrorCode.*;

public final class FSFactory
{
    private FileSystem fileSystem;
    private final Logger logger = Logger.get(FSFactory.class.getName());

    @Inject
    public FSFactory(PixelsPrestoConfig config)
    {
        Configuration hdfsConfig = new Configuration(false);
        ConfigFactory configFactory = config.getFactory();
        File hdfsConfigDir = new File(configFactory.getProperty("hdfs.config.dir"));
        hdfsConfig.set("fs.hdfs.impl", DistributedFileSystem.class.getName());
        hdfsConfig.set("fs.file.impl", LocalFileSystem.class.getName());
        try
        {
            if (hdfsConfigDir.exists() && hdfsConfigDir.isDirectory())
            {
                File[] hdfsConfigFiles = hdfsConfigDir.listFiles((file, s) -> s.endsWith("core-site.xml") || s.endsWith("hdfs-site.xml"));
                if (hdfsConfigFiles != null && hdfsConfigFiles.length == 2)
                {
                    hdfsConfig.addResource(hdfsConfigFiles[0].toURI().toURL());
                    hdfsConfig.addResource(hdfsConfigFiles[1].toURI().toURL());
                }
            } else
            {
                logger.error("can not read hdfs configuration file in pixels connector. hdfs.config.dir=" + hdfsConfigDir);
            }
            fileSystem = FileSystem.get(hdfsConfig);
        } catch (IOException e)
        {
            logger.error(e);
            throw new PrestoException(PIXELS_CONFIG_ERROR, e);
        }
    }

    public Optional<FileSystem> getFileSystem()
    {
        return Optional.of(this.fileSystem);
    }

    public List<Path> listFiles(Path dirPath)
    {
        List<Path> files = new ArrayList<>();
        FileStatus[] fileStatuses = null;
        try
        {
            fileStatuses = this.fileSystem.listStatus(dirPath);
            if (fileStatuses != null)
            {
                for (FileStatus f : fileStatuses)
                {
                    if (f.isFile())
                    {
                        files.add(f.getPath());
                    }
                }
            }
        } catch (IOException e)
        {
            logger.error(e);
            throw new PrestoException(PIXELS_HDFS_FILE_ERROR, e);
        }

        return files;
    }

    public List<Path> listFiles(String dirPath)
    {
        return listFiles(new Path(dirPath));
    }

    // assume that a file contains only a block
    public List<HostAddress> getBlockLocations(Path file, long start, long len)
    {
        Set<HostAddress> addresses = new HashSet<>();
        BlockLocation[] locations = new BlockLocation[0];
        try
        {
            locations = this.fileSystem.getFileBlockLocations(file, start, len);
        } catch (IOException e)
        {
            logger.error(e);
            throw new PrestoException(PIXELS_HDFS_BLOCK_ERROR, e);
        }
        for (BlockLocation location : locations)
        {
            try
            {
                addresses.addAll(toHostAddress(location.getHosts()));
            } catch (IOException e)
            {
                logger.error(e);
                throw new PrestoException(PIXELS_HDFS_BLOCK_ERROR, e);
            }
        }
        return new ArrayList<>(addresses);
    }

    private List<HostAddress> toHostAddress(String[] hosts)
    {
        ImmutableList.Builder<HostAddress> builder = ImmutableList.builder();
        for (String host : hosts)
        {
            builder.add(HostAddress.fromString(host));
        }
        return builder.build();
    }

    public List<LocatedBlock> listLocatedBlocks(Path path)
    {
        FSDataInputStream in = null;
        try
        {
            in = this.fileSystem.open(path);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        HdfsDataInputStream hdis = (HdfsDataInputStream) in;
        List<LocatedBlock> allBlocks = null;
        try
        {
            allBlocks = hdis.getAllBlocks();
        } catch (IOException e)
        {
            e.printStackTrace();
            throw new PrestoException(PIXELS_HDFS_BLOCK_ERROR, e);
        }
        return allBlocks;
    }

    public List<LocatedBlock> listLocatedBlocks(String path)
    {
        return listLocatedBlocks(new Path(path));
    }

}
