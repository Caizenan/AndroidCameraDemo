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

public class TkhdAtom extends Atom {
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
            name = "trackId",
            size = 4
    )
    public int trackId;
    @AtomFieldAnnotation(
            name = "reserved",
            size = 4
    )
    public int reserved;
    @AtomFieldAnnotation(
            name = "duration",
            size = 4
    )
    public long duration;
    @AtomFieldAnnotation(
            name = "reserved2",
            size = 8
    )
    public List<Integer> reserved2;
    @AtomFieldAnnotation(
            name = "layer",
            size = 2
    )
    public int layer;
    @AtomFieldAnnotation(
            name = "alternateGroup",
            size = 2
    )
    public int alternateGroup;
    @AtomFieldAnnotation(
            name = "volume",
            size = 2
    )
    public float volume;
    @AtomFieldAnnotation(
            name = "reserved3",
            size = 2
    )
    public int reserved3;
    @AtomFieldAnnotation(
            name = "matrix",
            size = 36
    )
    public List<String> matrix;
    @AtomFieldAnnotation(
            name = "rotate",
            size = 0
    )
    public int rotationDegree;
    @AtomFieldAnnotation(
            name = "width",
            size = 4
    )
    public float width;
    @AtomFieldAnnotation(
            name = "height",
            size = 4
    )
    public float height;

    public TkhdAtom() {
    }

    @Override
    public String getName() {
        return "tkhd";
    }

    @Override
    public List<AtomField> getAtomFieldList() {
        List<AtomField> list = super.getAtomFieldList();
        int timeByteLength = this.version == 0 ? 4 : 8;
        list.addAll(parseAtomFieldFromAnnotations(this, new long[]{2L, -1L, (long)timeByteLength, 3L, -1L, (long)timeByteLength, 6L, -1L, (long)timeByteLength}));
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
        if (this.version == 0) {
            this.creationTime = input.readUnsignedInt();
            this.modificationTime = input.readUnsignedInt();
            this.trackId = input.readInt();
            this.reserved = input.readInt();
            this.duration = input.readUnsignedInt();
        } else {
            this.creationTime = input.readUnsignedLongToLong();
            this.modificationTime = input.readUnsignedLongToLong();
            this.trackId = input.readInt();
            this.reserved = input.readInt();
            this.duration = input.readUnsignedLongToLong();
        }

        this.reserved2 = new ArrayList();
        this.reserved2.add(input.readInt());
        this.reserved2.add(input.readInt());
        this.layer = input.readShort();
        this.alternateGroup = input.readShort();
        this.volume = (float)input.readUnsignedByte() + Util.intToDecimalPart(input.readUnsignedByte());
        this.reserved3 = input.readUnsignedShort();
        this.matrix = new ArrayList(9);

        for(int i = 0; i < 9; ++i) {
            this.matrix.add(String.format("0x%x", input.readInt()));
        }

        input.setPosition(input.getPosition() - 36);
        this.rotationDegree = this.parseRotateDegree(input);
        this.width = (float)input.readUnsignedShort() + Util.intToDecimalPart(input.readUnsignedShort());
        this.height = (float)input.readUnsignedShort() + Util.intToDecimalPart(input.readUnsignedShort());
    }

    private int parseRotateDegree(ParsableByteArray tkhd) {
        int a00 = tkhd.readInt();
        int a01 = tkhd.readInt();
        tkhd.skipBytes(4);
        int a10 = tkhd.readInt();
        int a11 = tkhd.readInt();
        int fixedOne = 65536;
        short rotationDegrees;
        if (a00 == 0 && a01 == fixedOne && a10 == -fixedOne && a11 == 0) {
            rotationDegrees = 90;
        } else if (a00 == 0 && a01 == -fixedOne && a10 == fixedOne && a11 == 0) {
            rotationDegrees = 270;
        } else if (a00 == -fixedOne && a01 == 0 && a10 == 0 && a11 == -fixedOne) {
            rotationDegrees = 180;
        } else {
            rotationDegrees = 0;
        }

        tkhd.skipBytes(16);
        return rotationDegrees;
    }
}

