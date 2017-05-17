package com.devsmart.bdiff;

import com.devsmart.IOUtils;
import com.google.common.base.Preconditions;
import com.google.common.hash.HashCode;
import com.google.common.io.BaseEncoding;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class FlatFileBlockStorage implements BlockStorageReader, BlockStorageWriter {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlatFileBlockStorage.class);

    private final File mRootDir;
    private final String mCompressionType;

    public FlatFileBlockStorage(File rootDir, String compressionType) {
        Preconditions.checkArgument(rootDir != null && rootDir.isDirectory());
        mRootDir = rootDir;
        mCompressionType = compressionType;
    }

    public FlatFileBlockStorage(File rootDir) {
        this(rootDir, null);
    }

    private File toFile(HashCode id) {
        final String filename = BaseEncoding.base16().encode(id.asBytes());
        File f = new File(mRootDir, filename.substring(0, 2));
        f = new File(f, filename.substring(2, 4));
        f = new File(f, filename);
        return f;
    }

    @Override
    public void putBlock(HashCode id, InputStream in) throws IOException {
        final File f = toFile(id);
        final File dir = f.getParentFile();
        if(!dir.exists() && !dir.mkdirs()) {
            final String message = "could not create dir: " + f.getParentFile();
            LOGGER.error(message);
            throw new IOException(message);
        }

        OutputStream out = new FileOutputStream(f);
        if(mCompressionType != null) {
            try {
                out = new CompressorStreamFactory().createCompressorOutputStream(mCompressionType, out);
            } catch (CompressorException e) {
                throw new IOException(e);
            }
        }

        IOUtils.pump(in, out);
    }

    public OutputStream putBlock(HashCode id) throws IOException {
        final File f = toFile(id);
        final File dir = f.getParentFile();
        if(!dir.exists() && !dir.mkdirs()) {
            final String message = "could not create dir: " + f.getParentFile();
            LOGGER.error(message);
            throw new IOException(message);
        }

        OutputStream out = new FileOutputStream(f);
        if(mCompressionType != null) {
            try {
                out = new CompressorStreamFactory().createCompressorOutputStream(mCompressionType, out);
            } catch (CompressorException e) {
                throw new IOException(e);
            }
        }

        return out;
    }

    @Override
    public InputStream getBlock(HashCode id) throws IOException {
        final File f = toFile(id);
        if(!f.exists()) {
            return null;
        }
        InputStream in = new FileInputStream(f);
        if(mCompressionType != null) {
            try {
                in = new CompressorStreamFactory().createCompressorInputStream(mCompressionType, in);
            } catch (CompressorException e) {
                throw new IOException(e);
            }
        }
        return in;
    }

    public void delete(HashCode id) {
        final File f = toFile(id);
        if(f.exists()) {
            if(!f.delete()) {
                LOGGER.error("could not delete file: " + f);
            }
        }
    }

    public void wipe() {
        for(File f : mRootDir.listFiles()) {
            IOUtils.deleteTree(f);
        }
    }
}
