package com.devsmart.bdiff;

import com.google.common.hash.HashCode;

import java.io.IOException;
import java.io.InputStream;

public interface BlockStorageWriter {

    /**
     * put a block's input stream by its secure hashcode id
     * @param id
     * @param in
     */
    void putBlock(HashCode id, InputStream in) throws IOException;
}
