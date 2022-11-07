package com.example.mp4lib.mp4parser.input;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import com.example.mp4lib.mp4parser.util.Util;

import java.io.EOFException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.Arrays;

public final class DefaultExtractorInput implements ExtractorInput {
    private static final int PEEK_MIN_FREE_SPACE_AFTER_RESIZE = 65536;
    private static final int PEEK_MAX_FREE_SPACE = 524288;
    private static final int SCRATCH_SPACE_SIZE = 4096;
    private final byte[] scratchSpace;
    private final DataReader dataReader;
    private final long streamLength;
    private long position;
    private byte[] peekBuffer;
    private int peekBufferPosition;
    private int peekBufferLength;

    public DefaultExtractorInput(DataReader dataReader, long position, long length) {
        this.dataReader = dataReader;
        this.position = position;
        this.streamLength = length;
        this.peekBuffer = new byte[65536];
        this.scratchSpace = new byte[4096];
    }

    @Override
    public int read(byte[] target, int offset, int length) throws IOException {
        int bytesRead = this.readFromPeekBuffer(target, offset, length);
        if (bytesRead == 0) {
            bytesRead = this.readFromUpstream(target, offset, length, 0, true);
        }

        this.commitBytesRead(bytesRead);
        return bytesRead;
    }

    @Override
    public boolean readFully(byte[] target, int offset, int length, boolean allowEndOfInput) throws IOException {
        int bytesRead;
        for(bytesRead = this.readFromPeekBuffer(target, offset, length); bytesRead < length && bytesRead != -1; bytesRead = this.readFromUpstream(target, offset, length, bytesRead, allowEndOfInput)) {
        }

        this.commitBytesRead(bytesRead);
        return bytesRead != -1;
    }

    @Override
    public void readFully(byte[] target, int offset, int length) throws IOException {
        this.readFully(target, offset, length, false);
    }

    @Override
    public int skip(int length) throws IOException {
        int bytesSkipped = this.skipFromPeekBuffer(length);
        if (bytesSkipped == 0) {
            bytesSkipped = this.readFromUpstream(this.scratchSpace, 0, Math.min(length, this.scratchSpace.length), 0, true);
        }

        this.commitBytesRead(bytesSkipped);
        return bytesSkipped;
    }

    @Override
    public boolean skipFully(int length, boolean allowEndOfInput) throws IOException {
        int bytesSkipped;
        int minLength;
        for(bytesSkipped = this.skipFromPeekBuffer(length); bytesSkipped < length && bytesSkipped != -1; bytesSkipped = this.readFromUpstream(this.scratchSpace, -bytesSkipped, minLength, bytesSkipped, allowEndOfInput)) {
            minLength = Math.min(length, bytesSkipped + this.scratchSpace.length);
        }

        this.commitBytesRead(bytesSkipped);
        return bytesSkipped != -1;
    }

    @Override
    public void skipFully(int length) throws IOException {
        this.skipFully(length, false);
    }

    @Override
    public int peek(byte[] target, int offset, int length) throws IOException {
        this.ensureSpaceForPeek(length);
        int peekBufferRemainingBytes = this.peekBufferLength - this.peekBufferPosition;
        int bytesPeeked;
        if (peekBufferRemainingBytes == 0) {
            bytesPeeked = this.readFromUpstream(this.peekBuffer, this.peekBufferPosition, length, 0, true);
            if (bytesPeeked == -1) {
                return -1;
            }

            this.peekBufferLength += bytesPeeked;
        } else {
            bytesPeeked = Math.min(length, peekBufferRemainingBytes);
        }

        System.arraycopy(this.peekBuffer, this.peekBufferPosition, target, offset, bytesPeeked);
        this.peekBufferPosition += bytesPeeked;
        return bytesPeeked;
    }

    @Override
    public boolean peekFully(byte[] target, int offset, int length, boolean allowEndOfInput) throws IOException {
        if (!this.advancePeekPosition(length, allowEndOfInput)) {
            return false;
        } else {
            System.arraycopy(this.peekBuffer, this.peekBufferPosition - length, target, offset, length);
            return true;
        }
    }

    @Override
    public void peekFully(byte[] target, int offset, int length) throws IOException {
        this.peekFully(target, offset, length, false);
    }

    @Override
    public boolean advancePeekPosition(int length, boolean allowEndOfInput) throws IOException {
        this.ensureSpaceForPeek(length);

        for(int bytesPeeked = this.peekBufferLength - this.peekBufferPosition; bytesPeeked < length; this.peekBufferLength = this.peekBufferPosition + bytesPeeked) {
            bytesPeeked = this.readFromUpstream(this.peekBuffer, this.peekBufferPosition, length, bytesPeeked, allowEndOfInput);
            if (bytesPeeked == -1) {
                return false;
            }
        }

        this.peekBufferPosition += length;
        return true;
    }

    @Override
    public void advancePeekPosition(int length) throws IOException {
        this.advancePeekPosition(length, false);
    }

    @Override
    public void resetPeekPosition() {
        this.peekBufferPosition = 0;
    }

    @Override
    public long getPeekPosition() {
        return this.position + (long)this.peekBufferPosition;
    }

    @Override
    public long getPosition() {
        return this.position;
    }

    @Override
    public long getLength() {
        return this.streamLength;
    }

    @Override
    public <E extends Throwable> void setRetryPosition(long position, E e) throws E {
        this.position = position;
        throw e;
    }

    private void ensureSpaceForPeek(int length) {
        int requiredLength = this.peekBufferPosition + length;
        if (requiredLength > this.peekBuffer.length) {
            int newPeekCapacity = Util.constrainValue(this.peekBuffer.length * 2, requiredLength + 65536, requiredLength + 524288);
            this.peekBuffer = Arrays.copyOf(this.peekBuffer, newPeekCapacity);
        }

    }

    private int skipFromPeekBuffer(int length) {
        int bytesSkipped = Math.min(this.peekBufferLength, length);
        this.updatePeekBuffer(bytesSkipped);
        return bytesSkipped;
    }

    private int readFromPeekBuffer(byte[] target, int offset, int length) {
        if (this.peekBufferLength == 0) {
            return 0;
        } else {
            int peekBytes = Math.min(this.peekBufferLength, length);
            System.arraycopy(this.peekBuffer, 0, target, offset, peekBytes);
            this.updatePeekBuffer(peekBytes);
            return peekBytes;
        }
    }

    private void updatePeekBuffer(int bytesConsumed) {
        this.peekBufferLength -= bytesConsumed;
        this.peekBufferPosition = 0;
        byte[] newPeekBuffer = this.peekBuffer;
        if (this.peekBufferLength < this.peekBuffer.length - 524288) {
            newPeekBuffer = new byte[this.peekBufferLength + 65536];
        }

        System.arraycopy(this.peekBuffer, bytesConsumed, newPeekBuffer, 0, this.peekBufferLength);
        this.peekBuffer = newPeekBuffer;
    }

    private int readFromUpstream(byte[] target, int offset, int length, int bytesAlreadyRead, boolean allowEndOfInput) throws IOException {
        if (Thread.interrupted()) {
            throw new InterruptedIOException();
        } else {
            int bytesRead = this.dataReader.read(target, offset + bytesAlreadyRead, length - bytesAlreadyRead);
            if (bytesRead == -1) {
                if (bytesAlreadyRead == 0 && allowEndOfInput) {
                    return -1;
                } else {
                    throw new EOFException();
                }
            } else {
                return bytesAlreadyRead + bytesRead;
            }
        }
    }

    private void commitBytesRead(int bytesRead) {
        if (bytesRead != -1) {
            this.position += (long)bytesRead;
        }

    }
}

