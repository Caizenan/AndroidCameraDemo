package com.example.mp4lib.mp4parser.atom;


import com.example.mp4lib.mp4parser.util.ParsableByteArray;

import java.io.IOException;

public class MdatAtom extends Atom{
    public MdatAtom() {
    }

    @Override
    public String getName() {
        return "mdat";
    }

    @Override
    public void parse(long position, ParsableByteArray input) throws IOException {
        this.setOffset(position);
        this.setSize((long)input.readInt());
        this.setType(input.readInt());
    }
}
