package com.devsmart.bdiff;

import org.junit.Test;

import static org.junit.Assert.*;

public class BlockTest {

    @Test
    public void testSimpleIsContinuous() {
        Block[] blocks = new Block[] {
                new Block(0, 10),
        };

        assertTrue(Block.isContinuous(blocks));
    }

    @Test
    public void testIsContinuous() {
        Block[] blocks = new Block[] {
                new Block(0, 10),
                new Block(10, 10),
                new Block(20, 10)
        };

        assertTrue(Block.isContinuous(blocks));
    }

    @Test
    public void testIsNotContinuous() {
        Block[] blocks = new Block[] {
                new Block(0, 10),
                new Block(20, 10),
                new Block(30, 10)
        };

        assertFalse(Block.isContinuous(blocks));
    }

    @Test
    public void testIsNotContinuousOverlap() {
        Block[] blocks = new Block[] {
                new Block(0, 10),
                new Block(9, 15),
                new Block(20, 10)
        };

        assertFalse(Block.isContinuous(blocks));
    }


}
