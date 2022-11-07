package com.example.mp4lib.mp4parser.atom;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//


import com.example.sml_mp4_parser.annotation.AtomFieldAnnotation;
import com.example.sml_mp4_parser.util.ParsableByteArray;
import com.example.sml_mp4_parser.util.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MvhdAtom extends Atom {
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
            name = "creationTime",
            size = 4
    )
    public long creationTime;
    @AtomFieldAnnotation(
            name = "modificationTime",
            size = 4
    )
    public long modificationTime;
    @AtomFieldAnnotation(
            name = "timescale",
            size = 4
    )
    public int timescale;
    @AtomFieldAnnotation(
            name = "duration",
            size = 4
    )
    public long duration;
    @AtomFieldAnnotation(
            name = "rate",
            size = 4
    )
    public float rate;
    @AtomFieldAnnotation(
            name = "volume",
            size = 2
    )
    public float volume;
    @AtomFieldAnnotation(
            name = "reserved",
            size = 10
    )
    public List<Integer> reserved;
    @AtomFieldAnnotation(
            name = "matrix",
            size = 36
    )
    public List<String> matrix;
    @AtomFieldAnnotation(
            name = "preDefined",
            size = 24
    )
    public List<Integer> preDefined;
    @AtomFieldAnnotation(
            name = "nextTrackId",
            size = 4
    )
    public int nextTrackId;

    public MvhdAtom() {
    }

    @Override
    public String getName() {
        return "mvhd";
    }

    @Override
    public List<AtomField> getAtomFieldList() {
        List<AtomField> list = super.getAtomFieldList();
        int timeByteLength = this.version == 0 ? 4 : 8;
        list.addAll(parseAtomFieldFromAnnotations(this, new long[]{2L, -1L, (long)timeByteLength, 3L, -1L, (long)timeByteLength, 5L, -1L, (long)timeByteLength}));
        return list;
    }

    @Override
    public void parse(long position, ParsableByteArray input) throws IOException {
        this.setOffset(position);
        this.setSize(input.readUnsignedInt());
        this.setType(input.readInt());
        this.version = input.readUnsignedByte();
        this.flag = input.readInt24();
        if (this.version == 0) {
            this.creationTime = input.readUnsignedInt();
            this.modificationTime = input.readUnsignedInt();
            this.timescale = input.readInt();
            this.duration = input.readUnsignedInt();
        } else {
            this.creationTime = input.readUnsignedLongToLong();
            this.modificationTime = input.readUnsignedLongToLong();
            this.timescale = input.readInt();
            this.duration = input.readUnsignedLongToLong();
        }

        this.rate = (float)input.readUnsignedShort() + Util.intToDecimalPart(input.readUnsignedShort());
        this.volume = (float)input.readUnsignedByte() + Util.intToDecimalPart(input.readUnsignedByte());
        this.reserved = new ArrayList();
        this.reserved.add(input.readUnsignedShort());
        this.reserved.add(input.readInt());
        this.reserved.add(input.readInt());
        this.matrix = new ArrayList(9);

        int i;
        for(i = 0; i < 9; ++i) {
            this.matrix.add(String.format("0x%x", input.readInt()));
        }

        this.preDefined = new ArrayList(6);

        for(i = 0; i < 6; ++i) {
            this.preDefined.add(input.readInt());
        }

        this.nextTrackId = input.readInt();
    }
}
