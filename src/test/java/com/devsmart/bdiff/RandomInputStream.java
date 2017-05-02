package com.devsmart.bdiff;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;


public class RandomInputStream extends InputStream {

    private final Random mRandom;
    private final long mLength;
    private long mOffset = 0;

    public RandomInputStream(int seed, long length) {
        mRandom = new Random(seed);
        mLength = length;
    }

    @Override
    public int read() throws IOException {
        if (mOffset++ < mLength) {
            int retval = Math.abs((0xff & mRandom.nextInt()));
            return retval;
        } else {
            return -1;
        }
    }
}
