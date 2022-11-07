package com.example.mp4lib.mp4parser.input;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//


import com.example.simplemedialib.app.App;

import java.io.IOException;
import java.io.InputStream;

public class FileDataReader implements DataReader {
    private int fileSize;
    private byte[] data;
    private int position;

    public FileDataReader(String name) throws IOException {
        InputStream inputStream = App.getAppContext().getAssets().open(name);
        this.fileSize = inputStream.available();
        this.data = new byte[inputStream.available()];
        inputStream.read(this.data);
        inputStream.close();
        this.position = 0;
    }

    @Override
    public int read(byte[] target, int offset, int length) {
        if (this.position >= this.fileSize) {
            return -1;
        } else {
            if (this.position + length > this.fileSize) {
                length = this.fileSize - this.position;
            }

            System.arraycopy(this.data, this.position, target, offset, length);
            this.position += length;
            return length;
        }
    }

    public byte[] getData() {
        return this.data;
    }

    public int getFileSize() {
        return this.fileSize;
    }
}

