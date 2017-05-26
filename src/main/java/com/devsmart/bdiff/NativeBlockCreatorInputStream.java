package com.devsmart.bdiff;

import com.devsmart.bdiff.buzhash.Buzhash;
import com.devsmart.bdiff.buzhash.NativeBuzhash;
import com.google.common.base.Preconditions;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;

import java.io.IOException;
import java.io.InputStream;


public class NativeBlockCreatorInputStream extends InputStream {

    public interface Callback {
        void onNewBlock(SecureBlock block);
    }

    private final InputStream mInputStream;
    private final HashFunction mSecureHashFunction;
    private final long mMask;
    private Callback mCallback;
    private NativeBuzhash mBuzHash;
    private Hasher mSecureHash;
    private long mLast = 0;
    private long mPos = 0;

    private byte[] mByteBuf = new byte[1];


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
    public NativeBlockCreatorInputStream(InputStream in, HashFunction secureHash, int windowSize, int numBits) {
        Preconditions.checkNotNull(in);
        Preconditions.checkNotNull(secureHash);
        Preconditions.checkArgument(windowSize > 0 && numBits > 0);
        Preconditions.checkArgument(numBits < windowSize * 8);

        mInputStream = in;
        mSecureHashFunction = secureHash;
        mMask = (1 << numBits) -1;
        mBuzHash = NativeBuzhash.create(windowSize);
        mBuzHash.reset();
        mSecureHash = mSecureHashFunction.newHasher();
    }

    @Override
    public void close() throws IOException {
        mInputStream.close();
    }

    public void setCallback(Callback cb) {
        mCallback = cb;
    }

    private void newBlock(SecureBlock s) {
        if(mCallback != null) {
            mCallback.onNewBlock(s);
        }
    }

    private boolean mEndReached = false;

    @Override
    public int read() throws IOException {

        throw new UnsupportedOperationException("not implemented");

        /*
        final int data = mInputStream.read();
        if(data < 0) {
            if(mPos - mLast > 0 && !mEndReached) {
                mEndReached = true;
                SecureBlock block = new SecureBlock(mLast, mPos - mLast, mSecureHash.hash());
                newBlock(block);

            }
            return data;
        }

        mByteBuf[0] = (byte) data;
        int foundOffset = mBuzHash.addBytes(mByteBuf, 0, 1, mMask);


        final long hash = mBuzHash.addByte((byte)data);
        mSecureHash.putByte((byte)data);
        mPos++;
        if((hash & mMask) == 0) {
            SecureBlock block = new SecureBlock(mLast, mPos - mLast, mSecureHash.hash());

            newBlock(block);


            mLast = mPos;
            mBuzHash.reset();
            mSecureHash = mSecureHashFunction.newHasher();
        }

        return data;
        */
    }

    public int consumeBytes(byte[] b, int off, int len) throws IOException {
        int foundOffset = mBuzHash.addBytes(b, off, len, mMask);
        if(foundOffset < 0) {
            mSecureHash.putBytes(b, off, len);
            mPos += len;
            return len;
        } else {
            mSecureHash.putBytes(b, off, foundOffset+1);
            mPos += foundOffset+1;
            SecureBlock block = new SecureBlock(mLast, mPos - mLast, mSecureHash.hash());
            newBlock(block);

            mLast = mPos;
            mBuzHash.reset();
            mSecureHash = mSecureHashFunction.newHasher();
            return foundOffset+1;
        }
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        final int bytesRead = mInputStream.read(b, off, len);
        if(bytesRead < 0 && mPos - mLast > 0 && !mEndReached) {
            mEndReached = true;
            SecureBlock block = new SecureBlock(mLast, mPos - mLast, mSecureHash.hash());
            newBlock(block);
            return -1;
        }

        int bytesLeft = bytesRead;
        while(bytesLeft > 0) {
            len = consumeBytes(b, off, bytesLeft);
            off += len;
            bytesLeft -= len;
        }

        return bytesRead;
    }
}
