package com.devsmart.bdiff;

import java.io.IOException;


public class BadBlockIOException extends IOException {

    public final SecureBlock mBlock;

    public BadBlockIOException(SecureBlock block, String msg) {
        super(msg);
        mBlock = block;
    }
}
