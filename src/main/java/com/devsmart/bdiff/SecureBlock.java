package com.devsmart.bdiff;


import com.google.common.hash.HashCode;

public class SecureBlock extends Block {

    public final HashCode secureHash;

    public SecureBlock(long offset, int len, HashCode secureHash) {
        super(offset, len);
        this.secureHash = secureHash;
    }

    public SecureBlock(long offset, long len, HashCode secureHash) {
        super(offset, len);
        this.secureHash = secureHash;
    }
}
