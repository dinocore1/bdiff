package com.devsmart.bdiff;


import com.google.common.base.Preconditions;

public class Block {

    public final long offset;
    public final int length;

    public Block(long offset, int len) {
        Preconditions.checkArgument(offset >= 0 && len >= 0);
        this.offset = offset;
        this.length = len;
    }

    public Block(long offset, long len) {
        Preconditions.checkArgument(offset >= 0 && len >= 0);
        this.offset = offset;
        this.length = (int) len;
    }

    public long end() {
        return offset + length;
    }

    @Override
    public int hashCode() {
        int retval = (int) (offset & 0xffff);
        retval ^= (offset >> 32) & 0xffff;
        retval ^= length;
        return retval;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null || obj.getClass() != obj.getClass()) {
            return false;
        }

        Block o = (Block) obj;
        return o.offset == offset && o.length == length;
    }
}
