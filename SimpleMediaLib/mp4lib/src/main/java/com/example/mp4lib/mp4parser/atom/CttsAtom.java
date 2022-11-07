package com.example.mp4lib.mp4parser.atom;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import com.example.mp4lib.mp4parser.annotation.AtomFieldAnnotation;
import com.example.mp4lib.mp4parser.util.ParsableByteArray;

import java.io.IOException;
import java.util.List;

public class CttsAtom extends Atom {
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
    public CttsAtom.CompositionOffsetItem[] timeToSampleItems;

    public CttsAtom() {
    }

    @Override
    public String getName() {
        return "ctts";
    }

    @Override
    public List<AtomField> getAtomFieldList() {
        List<AtomField> list = super.getAtomFieldList();
        list.addAll(parseAtomFieldFromAnnotations(this, new long[0]));
        long offset = ((AtomField)list.get(list.size() - 1)).getEndPosition();
        list.add(new AtomField("Ctts Table , size ", this.entryCount, offset, 4L));
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
        this.timeToSampleItems = new CttsAtom.CompositionOffsetItem[this.entryCount];

        for(int i = 0; i < this.entryCount; ++i) {
            this.timeToSampleItems[i] = new CttsAtom.CompositionOffsetItem();
            this.timeToSampleItems[i].parse(input);
        }

    }

    public CttsAtom.CompositionOffsetItem[] getTimeToSampleItems() {
        return this.timeToSampleItems;
    }

    public static class CompositionOffsetItem {
        public int sampleCount;
        public int sampleOffset;

        public CompositionOffsetItem() {
        }

        void addAtomField(List<AtomField> list, int index, long offset) {
            list.add(new AtomField("" + index, "[sampleCount=" + this.sampleCount + " , sampleDelta = " + this.sampleOffset + "]", offset, 8L));
        }

        void parse(ParsableByteArray input) {
            this.sampleCount = input.readInt();
            this.sampleOffset = input.readInt();
        }
    }
}

