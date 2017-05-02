package com.devsmart.bdiff;

import com.devsmart.IOUtils;
import com.google.common.base.Throwables;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.common.hash.HashingInputStream;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;

import static org.junit.Assert.assertEquals;

public class FlatFileBlockStorageTest {


    private File mRootDir;

    @Before
    public void initStorageRoot() {
        mRootDir = new File("blockstorageTest");
        mRootDir.mkdirs();
    }

    @After
    public void destroyStorageRoot() {
        IOUtils.deleteTree(mRootDir);
    }

    private static InputStream createDatasource() {
        return new RandomInputStream(4, 11 * 1024 * 1024);
    }

    private static HashCode computeHash(InputStream in) throws IOException {
        HashingInputStream hin = new HashingInputStream(Hashing.md5(), createDatasource());
        IOUtils.pump(hin, new NullOutputStream());
        return hin.hash();
    }

    @Test
    public void testFlatBlock() throws Exception {
        final FlatFileBlockStorage storage = new FlatFileBlockStorage(mRootDir);

        final HashCode originalHash = computeHash(createDatasource());

        final LinkedList<SecureBlock> blockList = new LinkedList<SecureBlock>();

        DataBreakerInputStream in = new DataBreakerInputStream(createDatasource(), Hashing.sha1(), 50, 15);
        in.setCallback(new DataBreakerInputStream.Callback() {
            @Override
            public void onNewBlock(SecureBlock block, InputStream in) {
                blockList.add(block);
                try {
                    storage.putBlock(block.secureHash, in);
                } catch (IOException e) {
                    Throwables.propagate(e);
                }
            }
        });
        IOUtils.pump(in, new NullOutputStream());

        SecureBlock[] blocks = blockList.toArray(new SecureBlock[blockList.size()]);
        BDiffInputStream bin = new BDiffInputStream(blocks, storage);

        final HashCode finalHash = computeHash(bin);

        assertEquals(originalHash, finalHash);


    }
}
