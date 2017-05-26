package com.devsmart.bdiff;

import com.google.common.hash.HashCode;

import java.io.IOException;
import java.io.InputStream;

public interface BlockStorageReader {

    /**
     * get a block's input stream by its secure hashcode id.
     * If <code>id</code> cannot be found, this function should return null.
     * @param id
     * @return
     */
    InputStream getBlock(HashCode id) throws IOException;

    long getBlockLen(HashCode id);
}
