package com.devsmart.bdiff;

import com.google.common.base.Preconditions;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.HashingInputStream;

import java.io.IOException;
import java.io.InputStream;


public class BDiffInputStream extends InputStream {

    private SecureBlock mCurrentBlock;
    private long mOffset = 0;
    private int mBlockNum = 0;
    private final SecureBlock[] mBlocks;
    private final BlockStorageReader mStorageReader;
    private final long mLength;
    private final HashFunction mSecureHash;
    private InputStream mCurrentBlockStream;

    public BDiffInputStream(SecureBlock[] blocks, BlockStorageReader storage, HashFunction secureHash) {
        Preconditions.checkArgument(Block.isContinuous(blocks));
        mBlocks = blocks;
        mStorageReader = storage;
        mLength = blocks[blocks.length-1].end();
        mSecureHash = secureHash;
    }

    @Override
    public void close() throws IOException {
        if(mCurrentBlockStream != null) {
            mCurrentBlockStream.close();
            mCurrentBlockStream = null;
        }
        super.close();
    }

    private void getNextBlock() throws IOException {
        if(mCurrentBlockStream != null) {
            mCurrentBlockStream.close();
        }

        if(mSecureHash != null) {
            HashCode currentBlockHash = ((HashingInputStream) mCurrentBlockStream).hash();
            if(!currentBlockHash.equals(mCurrentBlock.secureHash)) {
                throw new BadBlockIOException(mCurrentBlock, "checksums do not match");
            }
        }

        mCurrentBlock = mBlocks[mBlockNum++];
        mCurrentBlockStream = mStorageReader.getBlock(mCurrentBlock.secureHash);
        if(mCurrentBlockStream == null) {
            throw new BadBlockIOException(mCurrentBlock, "block is null");
        } else if(mSecureHash != null) {
            mCurrentBlockStream = new HashingInputStream(mSecureHash, mCurrentBlockStream);
        }
    }

    @Override
    public int read() throws IOException {
        if(mOffset >= mLength) {
            return -1;
        }

        if(mCurrentBlock == null || mOffset >= mCurrentBlock.end()) {
            getNextBlock();
        }

        int retval = mCurrentBlockStream.read();
        if(retval < 0) {
            throw new BadBlockIOException(mCurrentBlock, "block stream is shorter than expected");
        }

        mOffset++;
        return retval;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if(mOffset >= mLength) {
            return -1;
        }

        if(mCurrentBlock == null || mOffset >= mCurrentBlock.end()) {
            getNextBlock();
        }

        long blockOffset = mOffset - mCurrentBlock.offset;
        int blockOffsetDiff = (int) (mCurrentBlock.length - blockOffset);
        int retval = mCurrentBlockStream.read(b, off, (len < blockOffsetDiff) ? len : blockOffsetDiff);
        if(retval < 0) {
            throw new BadBlockIOException(mCurrentBlock, "block stream is shorter than expected");
        }

        mOffset += retval;
        return retval;
    }
}
