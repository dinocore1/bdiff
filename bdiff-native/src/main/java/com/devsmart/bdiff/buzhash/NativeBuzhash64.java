package com.devsmart.bdiff.buzhash;


public class NativeBuzhash64 {

    static {
        System.loadLibrary("buzhash");
    }

    private long mPtr;

    private NativeBuzhash64() {
    }

    @Override
    protected void finalize() throws Throwable {
        native_finalize();
        super.finalize();
    }

    native public static NativeBuzhash64 create(int mWindowSize);
    private native void native_finalize();


    /**
     * add a block of data to be hashed. If a bitfield match is found, this function will
     * return the number of bytes after offset the match is found.
     * @param buf
     * @param offset
     * @param len
     * @param matchBits
     * @return byte offset of where the match was found, less than 0 otherwise
     */
    native public int addBytes(byte[] buf, int offset, int len, long matchBits);

    native public void reset();

    native public long hash();
}
