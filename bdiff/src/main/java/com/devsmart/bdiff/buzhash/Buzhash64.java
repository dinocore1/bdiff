package com.devsmart.bdiff.buzhash;

import java.util.Random;

import static com.google.common.base.Preconditions.checkArgument;

public class Buzhash64 {

    private static final int WORD_SIZE = 64;
    private static final long[] HASHMAP;

    static {
        HASHMAP = new long[256];
        Random r = new Random(1);
        for(int i=0;i<HASHMAP.length;i++){
            HASHMAP[i] = r.nextLong();
        }
    }


    private final int mWindowSize;
    private final byte[] mWindow;
    private long mHashvalue;
    private int mBufPos;
    private long mBytesAdded ;

    public Buzhash64(int windowSize) {
        checkArgument(windowSize > 0 && windowSize < WORD_SIZE);
        mWindowSize = windowSize;
        mWindow = new byte[mWindowSize];
        reset();
    }


    public long addByte(final byte b) {

        mHashvalue = Long.rotateLeft(mHashvalue, 1);

        if(++mBytesAdded > mWindowSize) {
            mHashvalue ^= Long.rotateLeft(HASHMAP[0xff & mWindow[mBufPos]], mWindowSize);
        }

        mHashvalue ^= HASHMAP[0xff & b];

        mWindow[mBufPos] = b;
        mBufPos = (mBufPos + 1) % mWindowSize;

        return mHashvalue;
    }

    public void reset() {
        mHashvalue = 0;
        mBytesAdded = 0;
        mBufPos = 0;
    }

    public long getHashCode() {
        return mHashvalue;
    }
}