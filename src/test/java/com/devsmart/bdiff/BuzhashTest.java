package com.devsmart.bdiff;


import com.google.common.base.Charsets;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class BuzhashTest {

    @Test
    public void testBuzHash() {
        /**
         * Buzhash is a rolling hash function, that means that the current hash sum is the sum of the last n consumed bytes.
         * When hashing both messages with a buzhasher with n=16, both messages will have the same hash sum, since the last
         * 16 characters (nstrate buzhash.) are equal.
         */

        final int windowSize = 16;

        final byte[] message1 = "This is a stupid example text to demonstrate buzhash.".getBytes(Charsets.UTF_8);
        final byte[] message2 = "Another text to demonstrate buzhash.".getBytes(Charsets.UTF_8);

        Buzhash hash1 = new Buzhash(windowSize);
        for(int i=0;i<message1.length;i++){
            hash1.addByte(message1[i]);
        }


        Buzhash hash2 = new Buzhash(windowSize);
        for(int i=0;i<message2.length;i++){
            hash2.addByte(message2[i]);
        }

        assertEquals(hash1.getHashCode(), hash2.getHashCode());

    }
}