package com.example.mp4lib.mp4parser.atom;

import com.example.sml_mp4_parser.annotation.AtomFieldAnnotation;
import com.example.sml_mp4_parser.util.ParsableByteArray;

import java.io.IOException;
import java.util.List;

public class SmhdAtom extends Atom{
    @AtomFieldAnnotation(
            name = "version",
            offset = 8,
            size = 1
    )
    public int version;
    @AtomFieldAnnotation(
            name = "flag",
            size = 3
    )
    public int flag;
    @AtomFieldAnnotation(
            name = "balance",
            size = 2
    )
    public int balance;
    @AtomFieldAnnotation(
            name = "reserved",
            size = 2
    )
    public int reserved;

    public SmhdAtom() {
    }

    @Override
    public String getName() {
        return "smhd";
    }

    @Override
    public List<AtomField> getAtomFieldList() {
        List<AtomField> list = super.getAtomFieldList();
        list.addAll(parseAtomFieldFromAnnotations(this, new long[0]));
        return list;
    }

    @Override
    public void parse(long position, ParsableByteArray input) throws IOException {
        super.parse(position, input);
        this.setOffset(position);
        this.setSize(input.readUnsignedInt());
        this.setType(input.readInt());
        this.version = input.readUnsignedByte();
        this.flag = input.readInt24();
        this.balance = input.readUnsignedShort();
        this.reserved = input.readUnsignedShort();
    }
}
