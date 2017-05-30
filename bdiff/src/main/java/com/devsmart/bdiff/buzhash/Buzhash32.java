package com.devsmart.bdiff.buzhash;

import java.util.Random;

import static com.google.common.base.Preconditions.checkArgument;

public class Buzhash32 {

    private static final int WORD_SIZE = 32;
    private static final int[] HASHMAP;

    static {
        HASHMAP = new int[256];
        Random r = new Random(1);
        for(int i=0;i<HASHMAP.length;i++){
            HASHMAP[i] = (int) r.nextLong();
        }
    }


    private final int mWindowSize;
    private final byte[] mWindow;
    private int mHashvalue;
    private int mBufPos;
    private long mBytesAdded;

    public Buzhash32(int windowSize) {
        checkArgument(windowSize > 0 && windowSize < WORD_SIZE);
        mWindowSize = windowSize;
        mWindow = new byte[mWindowSize];
        reset();
    }


    public int addByte(final byte b) {

        mHashvalue = Integer.rotateLeft(mHashvalue, 1);

        if(++mBytesAdded > mWindowSize) {
            mHashvalue ^= Integer.rotateLeft(HASHMAP[0xff & mWindow[mBufPos]], mWindowSize);
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
