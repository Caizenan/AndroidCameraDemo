package com.example.mp4lib.mp4parser.atom;

import com.example.sml_mp4_parser.annotation.AtomFieldAnnotation;
import com.example.sml_mp4_parser.util.ParsableByteArray;

import java.io.IOException;
import java.util.List;

public class VmhdAtom extends Atom {
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
            name = "graphicsMode",
            size = 2
    )
    public int graphicsMode;
    @AtomFieldAnnotation(
            name = "opcolorRed",
            size = 2
    )
    public int opcolorRed;
    @AtomFieldAnnotation(
            name = "opcolorGreen",
            size = 2
    )
    public int opcolorGreen;
    @AtomFieldAnnotation(
            name = "opcolorBlue",
            size = 2
    )
    public int opcolorBlue;

    public VmhdAtom() {
    }

    @Override
    public String getName() {
        return "vmhd";
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
        this.graphicsMode = input.readUnsignedShort();
        this.opcolorRed = input.readUnsignedShort();
        this.opcolorGreen = input.readUnsignedShort();
        this.opcolorBlue = input.readUnsignedShort();
    }
}