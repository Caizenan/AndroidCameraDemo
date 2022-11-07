package com.example.mp4lib.mp4parser.atom;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import com.example.sml_mp4_parser.annotation.AtomFieldAnnotation;
import com.example.sml_mp4_parser.util.ParsableByteArray;

import java.io.IOException;
import java.util.List;

public class Stz2Atom extends Atom implements SampleSizeAtom {
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
            name = "reserved",
            size = 3
    )
    public int reserved;
    @AtomFieldAnnotation(
            name = "fieldSize",
            size = 1
    )
    public int fieldSize;
    @AtomFieldAnnotation(
            name = "sampleCount",
            size = 4
    )
    public int sampleCount;
    public Stz2Atom.SampleSizeItem2[] sampleSizeItems;

    public Stz2Atom() {
    }

    @Override
    public int getSampleCount() {
        return this.getSampleCount();
    }

    @Override
    public int getFixedSampleSize() {
        return -1;
    }

    @Override
    public StszAtom.SampleSizeItem[] getSampleSizeItems() {
        return this.sampleSizeItems;
    }

    @Override
    public String getName() {
        return "stsz";
    }

    @Override
    public List<AtomField> getAtomFieldList() {
        List<AtomField> list = super.getAtomFieldList();
        list.addAll(parseAtomFieldFromAnnotations(this, new long[0]));
        long offset = ((AtomField)list.get(list.size() - 1)).getEndPosition();
        if (this.sampleCount > 0) {
            list.add(new AtomField("Sample Size Table", "", offset, 0L));

            for(int i = 0; i < this.sampleCount; ++i) {
                this.sampleSizeItems[i].addAtomField(list, i, offset, this.fieldSize);
                offset += (long)this.fieldSize;
            }
        }

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
        this.reserved = input.readInt24();
        this.fieldSize = input.readUnsignedByte();
        this.sampleCount = input.readInt();
        this.sampleSizeItems = new Stz2Atom.SampleSizeItem2[this.sampleCount];

        for(int i = 0; i < this.sampleCount; ++i) {
            this.sampleSizeItems[i] = new Stz2Atom.SampleSizeItem2();
            this.sampleSizeItems[i].parse(input, this.fieldSize, i);
        }

    }

    public static class SampleSizeItem2 extends StszAtom.SampleSizeItem {
        public SampleSizeItem2() {
        }

        void addAtomField(List<AtomField> list, int index, long offset, int fieldSize) {
            list.add(new AtomField("" + index, "[size = " + this.entrySize + "]", offset, (long)fieldSize));
        }

        void parse(ParsableByteArray input, int fieldSize, int index) {
            if (fieldSize == 4) {
                if (index % 2 == 0) {
                    this.entrySize = input.readUnsignedByte() >> 4;
                    input.setPosition(input.getPosition() - 1);
                } else {
                    this.entrySize = input.readUnsignedByte() & 15;
                }
            } else if (fieldSize == 8) {
                this.entrySize = input.readUnsignedByte();
            } else if (fieldSize == 16) {
                this.entrySize = input.readUnsignedShort();
            }

        }
    }
}

