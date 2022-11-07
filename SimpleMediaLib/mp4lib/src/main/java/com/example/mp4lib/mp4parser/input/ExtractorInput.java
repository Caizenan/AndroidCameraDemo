package com.example.mp4lib.mp4parser.input;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import java.io.IOException;

public interface ExtractorInput extends DataReader {
    int read(byte[] var1, int var2, int var3) throws IOException;

    boolean readFully(byte[] var1, int var2, int var3, boolean var4) throws IOException;

    void readFully(byte[] var1, int var2, int var3) throws IOException;

    int skip(int var1) throws IOException;

    boolean skipFully(int var1, boolean var2) throws IOException;

    void skipFully(int var1) throws IOException;

    int peek(byte[] var1, int var2, int var3) throws IOException;

    boolean peekFully(byte[] var1, int var2, int var3, boolean var4) throws IOException;

    void peekFully(byte[] var1, int var2, int var3) throws IOException;

    boolean advancePeekPosition(int var1, boolean var2) throws IOException;

    void advancePeekPosition(int var1) throws IOException;

    void resetPeekPosition();

    long getPeekPosition();

    long getPosition();

    long getLength();

    <E extends Throwable> void setRetryPosition(long var1, E var3) throws E;
}
