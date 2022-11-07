package com.example.mp4lib.mp4parser.atom;


import com.example.mp4lib.mp4parser.util.ParsableByteArray;

import java.io.IOException;

public abstract class ContainerAtom extends Atom{
    public ContainerAtom() {
    }

    @Override
    public void parse(long position, ParsableByteArray input) throws IOException {
        super.parse(position, input);
        this.setOffset(position);
        this.setSize((long)input.readInt());
        this.setType(input.readInt());
    }
}
