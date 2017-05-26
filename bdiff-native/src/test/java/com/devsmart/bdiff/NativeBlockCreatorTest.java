package com.devsmart.bdiff;


import com.devsmart.IOUtils;
import com.google.common.collect.Iterables;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import org.junit.Test;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class NativeBlockCreatorTest {

    private InputStream createInputStream() {
        return new RandomInputStream(1, 10*1024*1024);
    }

    @Test
    public void testNativeBlockCreator() throws Exception {
        final HashFunction secureHash = Hashing.md5();
        int windowSize = 50;
        int numBits = 15;

        final List<SecureBlock> expectedBlocks = new LinkedList<SecureBlock>();

        BlockCreatorInputStream trustedBlockCreator = new BlockCreatorInputStream(createInputStream(),
                secureHash, windowSize, numBits);
        trustedBlockCreator.setCallback(new BlockCreatorInputStream.Callback() {
            @Override
            public void onNewBlock(SecureBlock block) {
                expectedBlocks.add(block);
            }
        });

        IOUtils.pump(trustedBlockCreator, new NullOutputStream());


        final List<SecureBlock> blocks = new LinkedList<SecureBlock>();
        NativeBlockCreatorInputStream blockCreator = new NativeBlockCreatorInputStream(createInputStream(),
                secureHash, windowSize, numBits);
        blockCreator.setCallback(new NativeBlockCreatorInputStream.Callback() {
            @Override
            public void onNewBlock(SecureBlock block) {
                blocks.add(block);
            }
        });

        IOUtils.pump(blockCreator, new NullOutputStream());


        assertTrue(Iterables.elementsEqual(expectedBlocks, blocks));
    }
}
