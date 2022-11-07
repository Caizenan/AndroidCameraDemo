package com.example.mp4lib.mp4parser.atom;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//


import com.example.mp4lib.mp4parser.annotation.AtomFieldAnnotation;
import com.example.mp4lib.mp4parser.util.ParsableByteArray;

import java.io.IOException;
import java.util.List;

public class SttsAtom extends Atom {
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
    public int entryCount;
    public SttsAtom.TimeToSampleItem[] timeToSampleItems;

    public SttsAtom() {
    }

    @Override
    public String getName() {
        return "stts";
    }

    @Override
    public List<AtomField> getAtomFieldList() {
        List<AtomField> list = super.getAtomFieldList();
        list.addAll(parseAtomFieldFromAnnotations(this, new long[0]));
        long offset = ((AtomField)list.get(list.size() - 1)).getEndPosition();
        list.add(new AtomField("Time-to-Sample Table , size ", this.entryCount, offset, 4L));
        offset += 4L;

        for(int i = 0; i < this.entryCount; ++i) {
            this.timeToSampleItems[i].addAtomField(list, i, offset);
            offset += 8L;
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
        this.entryCount = input.readInt();
        this.timeToSampleItems = new SttsAtom.TimeToSampleItem[this.entryCount];

        for(int i = 0; i < this.entryCount; ++i) {
            this.timeToSampleItems[i] = new SttsAtom.TimeToSampleItem();
            this.timeToSampleItems[i].parse(input);
        }

    }

    public SttsAtom.TimeToSampleItem[] getTimeToSampleItems() {
        return this.timeToSampleItems;
    }

    public static class TimeToSampleItem {
        public int sampleCount;
        public int sampleDelta;

        public TimeToSampleItem() {
        }

        void addAtomField(List<AtomField> list, int index, long offset) {
            list.add(new AtomField("" + index, "[sampleCount=" + this.sampleCount + " , sampleDelta = " + this.sampleDelta + "]", offset, 8L));
        }

        void parse(ParsableByteArray input) {
            this.sampleCount = input.readInt();
            this.sampleDelta = input.readInt();
        }
    }
}

