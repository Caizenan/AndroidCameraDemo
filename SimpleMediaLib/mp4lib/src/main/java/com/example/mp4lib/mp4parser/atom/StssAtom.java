package com.example.mp4lib.mp4parser.atom;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import com.example.mp4lib.mp4parser.annotation.AtomFieldAnnotation;
import com.example.mp4lib.mp4parser.util.ParsableByteArray;

import java.io.IOException;
import java.util.List;

public class StssAtom extends Atom {
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
    public StssAtom.SyncSampleBox[] syncSampleBoxes;

    public StssAtom() {
    }

    @Override
    public String getName() {
        return "stss";
    }

    @Override
    public List<AtomField> getAtomFieldList() {
        List<AtomField> list = super.getAtomFieldList();
        list.addAll(parseAtomFieldFromAnnotations(this, new long[0]));
        long offset = ((AtomField)list.get(list.size() - 1)).getEndPosition();
        list.add(new AtomField("Sync Sample Table , size ", this.entryCount, offset, 4L));
        offset += 4L;

        for(int i = 0; i < this.entryCount; ++i) {
            this.syncSampleBoxes[i].addAtomField(list, i, offset);
            offset += 4L;
        }

        return list;
    }

    public void parse(long position, ParsableByteArray input) throws IOException {
        super.parse(position, input);
        this.setOffset(position);
        this.setSize(input.readUnsignedInt());
        this.setType(input.readInt());
        this.version = input.readUnsignedByte();
        this.flag = input.readInt24();
        this.entryCount = input.readInt();
        this.syncSampleBoxes = new StssAtom.SyncSampleBox[this.entryCount];

        for(int i = 0; i < this.entryCount; ++i) {
            this.syncSampleBoxes[i] = new StssAtom.SyncSampleBox();
            this.syncSampleBoxes[i].parse(input);
        }

    }

    public StssAtom.SyncSampleBox[] getSyncSampleBoxes() {
        return this.syncSampleBoxes;
    }

    public static class SyncSampleBox {
        public int sampleNumber;

        public SyncSampleBox() {
        }

        void addAtomField(List<AtomField> list, int index, long offset) {
            list.add(new AtomField("" + index, "[sampleNumber=" + this.sampleNumber + "]", offset, 4L));
        }

        void parse(ParsableByteArray input) {
            this.sampleNumber = input.readInt();
        }
    }
}

