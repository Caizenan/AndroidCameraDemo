package com.example.mp4lib.mp4parser.atom;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import com.example.mp4lib.mp4parser.util.ParsableByteArray;
import com.example.mp4lib.mp4parser.util.Util;

import java.io.IOException;
import java.util.List;

public class OtherAtom extends Atom {
    private String name;
    private boolean shouldParse;

    public OtherAtom(long size, int type) {
        this.setSize(size);
        this.setType(type);
        this.name = Util.fromUtf8Bytes(Util.intToByteArray(type));
        this.shouldParse = false;
    }

    public OtherAtom() {
        this.shouldParse = true;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void parse(long position, ParsableByteArray input) throws IOException {
        super.parse(position, input);
        this.setOffset(position);
        if (this.shouldParse) {
            this.setSize(input.readUnsignedInt());
            this.setType(input.readInt());
            input.setPosition(input.getPosition() - 4);
            this.name = input.readString(4);
        }

    }

    @Override
    public List<AtomField> getAtomFieldList() {
        List<AtomField> list = super.getAtomFieldList();
        list.add(new AtomField("DATA", "", 8L, this.getSize() - 8L));
        return list;
    }
}

