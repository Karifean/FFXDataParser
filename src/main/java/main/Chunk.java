package main;

import java.util.Arrays;

public class Chunk {
    public int[] bytes;
    public int offset;
    public int length;

    public Chunk() {
        bytes = new int[0];
    }

    public Chunk(int[] toCopy, int from, int to) {
        int boundedTo = Math.min(to, toCopy.length);
        bytes = from < toCopy.length ? Arrays.copyOfRange(toCopy, from, boundedTo) : new int[0];
        offset = from;
        length = to - from;
    }
}
