package com.example.mp4lib.mp4parser.atom;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//


import com.example.mp4lib.mp4parser.annotation.AtomFieldAnnotation;
import com.example.mp4lib.mp4parser.util.ParsableByteArray;

import java.io.IOException;
import java.util.List;

public class StszAtom extends Atom implements SampleSizeAtom {
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
            name = "sampleSize",
            size = 4
    )
    public int sampleSize;
    @AtomFieldAnnotation(
            name = "sampleCount",
            size = 4
    )
    public int sampleCount;
    public StszAtom.SampleSizeItem[] sampleSizeItems;

    public StszAtom() {
    }

    @Override
    public int getSampleCount() {
        return this.sampleCount;
    }

    @Override
    public int getFixedSampleSize() {
        return this.sampleSize;
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
        if (this.sampleSize == 0) {
            long offset = ((AtomField)list.get(list.size() - 1)).getEndPosition();
            list.add(new AtomField("Sample Size Table", ":", offset, 0L));

            for(int i = 0; i < this.sampleCount; ++i) {
                this.sampleSizeItems[i].addAtomField(list, i, offset);
                offset += 4L;
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
        this.sampleSize = input.readInt();
        this.sampleCount = input.readInt();
        if (this.sampleSize == 0) {
            this.sampleSizeItems = new StszAtom.SampleSizeItem[this.sampleCount];

            for(int i = 0; i < this.sampleCount; ++i) {
                this.sampleSizeItems[i] = new StszAtom.SampleSizeItem();
                this.sampleSizeItems[i].parse(input);
            }
        }

    }

    public static class SampleSizeItem {
        public int entrySize;

        public SampleSizeItem() {
        }

        void addAtomField(List<AtomField> list, int index, long offset) {
            list.add(new AtomField("" + index, "[size = " + this.entrySize + "]", offset, 4L));
        }

        void parse(ParsableByteArray input) {
            this.entrySize = input.readInt();
        }
    }
}
