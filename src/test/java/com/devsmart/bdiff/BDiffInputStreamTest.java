package com.devsmart.bdiff;


import com.devsmart.IOUtils;
import com.google.common.hash.HashCode;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class BDiffInputStreamTest {

    private static class SimpleBlockStorageReader implements BlockStorageReader {

        HashMap<HashCode, byte[]> mBlockTable = new HashMap<HashCode, byte[]>();

        @Override
        public InputStream getBlock(HashCode id) {
            byte[] block = mBlockTable.get(id);
            if(block == null) {
                return null;
            }

            return new ByteArrayInputStream(block);
        }
    }

    private static void putBlock(SimpleBlockStorageReader storage, int i) {
        HashCode id1 = HashCode.fromInt(i);
        byte[] block1 = new byte[10];
        Arrays.fill(block1, (byte) i);
        storage.mBlockTable.put(id1, block1);
    }

    @Test
    public void testInputStream() throws Exception {

        SimpleBlockStorageReader storage = new SimpleBlockStorageReader();

        putBlock(storage, 0);
        putBlock(storage, 1);
        putBlock(storage, 2);

        SecureBlock[] blocks = new SecureBlock[] {
                new SecureBlock(0, 10, HashCode.fromInt(0)),
                new SecureBlock(10, 10, HashCode.fromInt(1)),
                new SecureBlock(20, 10, HashCode.fromInt(2))
        };

        BDiffInputStream in = new BDiffInputStream(blocks, storage);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        IOUtils.pump(in, out);

        byte[] total = out.toByteArray();
        assertNotNull(total);
        assertEquals(30, total.length);

        for(int i=0;i<30;i++){
            assertEquals(i / 10, total[i]);
        }

    }

    @Test
    public void testInputStream2() throws Exception {
        SimpleBlockStorageReader storage = new SimpleBlockStorageReader();

        putBlock(storage, 0);
        putBlock(storage, 1);
        putBlock(storage, 2);

        SecureBlock[] blocks = new SecureBlock[] {
                new SecureBlock(0, 10, HashCode.fromInt(0)),
                new SecureBlock(10, 10, HashCode.fromInt(1)),
                new SecureBlock(20, 10, HashCode.fromInt(2))
        };

        BDiffInputStream in = new BDiffInputStream(blocks, storage);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        IOUtils.pump(in, out, 7, null, true, true);

        byte[] total = out.toByteArray();
        assertNotNull(total);
        assertEquals(30, total.length);

        for(int i=0;i<30;i++){
            assertEquals(i / 10, total[i]);
        }
    }
}
