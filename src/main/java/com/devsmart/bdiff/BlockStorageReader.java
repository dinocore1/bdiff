package com.devsmart.bdiff;

import com.google.common.hash.HashCode;

import java.io.InputStream;

public interface BlockStorageReader {

    /**
     * get a block's input stream by its secure hashcode id
     * @param id
     * @return
     */
    InputStream getBlock(HashCode id);
}
