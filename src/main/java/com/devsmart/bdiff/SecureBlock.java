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

    @Override
    public int hashCode() {
        int retval = super.hashCode();
        retval ^= secureHash.asInt();
        return retval;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null || obj.getClass() != getClass()) {
            return false;
        }

        SecureBlock o = (SecureBlock) obj;
        return super.equals(o) && secureHash.equals(o.secureHash);
    }
}
