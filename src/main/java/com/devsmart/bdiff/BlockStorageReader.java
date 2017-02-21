package com.devsmart.bdiff;

import com.google.common.hash.HashCode;

import java.io.InputStream;

public interface BlockStorageReader {

    InputStream getBlock(HashCode id);
}
