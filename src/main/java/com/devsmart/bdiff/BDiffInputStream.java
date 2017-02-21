package com.devsmart.bdiff;

import com.google.common.base.Preconditions;

import java.io.IOException;
import java.io.InputStream;


public class BDiffInputStream extends InputStream {

    private SecureBlock mCurrentBlock;
    private long mOffset = 0;
    private int mBlockNum = 0;
    private final SecureBlock[] mBlocks;
    private final BlockStorageReader mStorageReader;
    private final long mLength;
    private InputStream mCurrentBlockStream;

    public BDiffInputStream(SecureBlock[] blocks, BlockStorageReader storage) {
        Preconditions.checkArgument(Block.isContinuous(blocks));

        mBlocks = blocks;
        mStorageReader = storage;

        mLength = blocks[blocks.length-1].end();
    }


    @Override
    public int read() throws IOException {
        if(mOffset >= mLength) {
            return -1;
        }

        if(mCurrentBlock == null || mOffset >= mCurrentBlock.end()) {
            if(mCurrentBlockStream != null){
                mCurrentBlockStream.close();
            }

            mCurrentBlock = mBlocks[mBlockNum++];
            mCurrentBlockStream = mStorageReader.getBlock(mCurrentBlock.secureHash);
            if(mCurrentBlockStream == null) {
                throw new IOException("block is null");
            }
        }

        int retval = mCurrentBlockStream.read();
        if(retval < 0) {
            throw new IOException("block stream is shorter than expected");
        }

        mOffset++;
        return retval;
    }
}
