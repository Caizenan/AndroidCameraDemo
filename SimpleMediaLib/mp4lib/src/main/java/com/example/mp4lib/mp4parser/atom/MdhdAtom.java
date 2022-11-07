package com.example.mp4lib.mp4parser.atom;


import com.example.mp4lib.mp4parser.annotation.AtomFieldAnnotation;
import com.example.mp4lib.mp4parser.util.ParsableByteArray;

import java.io.IOException;
import java.util.List;

public class MdhdAtom extends Atom{
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
            name = "language",
            size = 2
    )
    public int language;
    @AtomFieldAnnotation(
            name = "quality",
            size = 2
    )
    public int quality;

    public MdhdAtom() {
    }

    @Override
    public String getName() {
        return "mdhd";
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
        super.parse(position, input);
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

        this.language = input.readUnsignedShort();
        this.quality = input.readUnsignedShort();
    }
}
