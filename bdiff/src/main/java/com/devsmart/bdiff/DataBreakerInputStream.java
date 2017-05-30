package com.devsmart.bdiff;

import com.devsmart.bdiff.buzhash.Buzhash64;
import com.google.common.base.Preconditions;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class DataBreakerInputStream extends InputStream {

    public interface Callback {
        void onNewBlock(SecureBlock block, InputStream in);
    }

    private final InputStream mInputStream;
    private final HashFunction mSecureHashFunction;
    private final long mMask;
    private Callback mCallback;
    private Buzhash64 mBuzHash;
    private Hasher mSecureHash;
    private long mLast = 0;
    private long mPos = 0;
    private ByteArrayOutputStream mBlockBuffer;


    /**
     * This class splits the data read from the underlying inputstream into 1 or more dynamically size blocks. A secure
     * hash values is computed for the data within each block. The Block's size is determined using a rolling hash function.
     * When the lower {code}numBits{/code} bits of the rolling hash are all '1's a block boundary is found. Therefor, the probability
     * of finding a block boundary is 2^-{code}numBits{/code} (assuming the data in the stream is pseudorandom).
     * @param in
     * @param secureHash
     * @param windowSize the rolling hash byte window size. A good value is 50.
     * @param numBits the number of bits in the rolling hash that must match to define a delineation. A good value is 12.
     */
    public DataBreakerInputStream(InputStream in, HashFunction secureHash, int windowSize, int numBits) {
        Preconditions.checkNotNull(in);
        Preconditions.checkNotNull(secureHash);
        Preconditions.checkArgument(windowSize > 0 && numBits > 0);
        Preconditions.checkArgument(numBits < windowSize * 8);

        mInputStream = in;
        mSecureHashFunction = secureHash;
        mMask = (1 << numBits) -1;
        mBuzHash = new Buzhash64(windowSize);
        mBuzHash.reset();
        mSecureHash = mSecureHashFunction.newHasher();
        mBlockBuffer = new ByteArrayOutputStream(1 << numBits);
    }

    @Override
    public void close() throws IOException {
        mInputStream.close();
    }

    public void setCallback(Callback cb) {
        mCallback = cb;
    }

    private void newBlock(SecureBlock s, byte[] data) {
        if(mCallback != null) {
            mCallback.onNewBlock(s, new ByteArrayInputStream(data));
        }
    }

    private boolean mEndReached = false;

    @Override
    public int read() throws IOException {
        final int data = mInputStream.read();
        if(data < 0) {
            if(mPos - mLast > 0 && !mEndReached) {
                mEndReached = true;
                SecureBlock block = new SecureBlock(mLast, mPos - mLast, mSecureHash.hash());

                mBlockBuffer.flush();
                newBlock(block, mBlockBuffer.toByteArray());
                mBlockBuffer.reset();
            }
            return data;
        }
        final long hash = mBuzHash.addByte((byte)data);
        mSecureHash.putByte((byte)data);
        mBlockBuffer.write(data);
        mPos++;
        if((hash & mMask) == 0) {
            SecureBlock block = new SecureBlock(mLast, mPos - mLast, mSecureHash.hash());
            mBlockBuffer.flush();
            newBlock(block, mBlockBuffer.toByteArray());
            mBlockBuffer.reset();

            mLast = mPos;
            mBuzHash.reset();
            mSecureHash = mSecureHashFunction.newHasher();
        }

        return data;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int bytesRead = mInputStream.read(b, off, len);
        if(bytesRead > 0) {
            for(int i=0;i<bytesRead;i++) {
                final byte data = b[off + i];
                final long hash = mBuzHash.addByte(data);
                mSecureHash.putByte(data);
                mBlockBuffer.write(data);
                mPos++;
                if((hash & mMask) == 0) {
                    SecureBlock block = new SecureBlock(mLast, mPos - mLast, mSecureHash.hash());
                    mBlockBuffer.flush();
                    newBlock(block, mBlockBuffer.toByteArray());
                    mBlockBuffer.reset();

                    mLast = mPos;
                    mBuzHash.reset();
                    mSecureHash = mSecureHashFunction.newHasher();
                }
            }
        } else {
            if(mPos - mLast > 0 && !mEndReached) {
                mEndReached = true;
                SecureBlock block = new SecureBlock(mLast, mPos - mLast, mSecureHash.hash());

                mBlockBuffer.flush();
                newBlock(block, mBlockBuffer.toByteArray());
                mBlockBuffer.reset();
            }
        }
        return bytesRead;
    }

}