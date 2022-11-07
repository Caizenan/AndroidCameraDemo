package com.example.mp4lib.mp4parser.atom;


import com.example.mp4lib.mp4parser.annotation.AtomFieldAnnotation;
import com.example.mp4lib.mp4parser.util.ParsableByteArray;
import com.example.mp4lib.mp4parser.util.Util;

import java.io.IOException;
import java.util.List;

public class ElstAtom extends Atom{
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
            name = "entryCount",
            size = 4
    )
    public int entryCount;
    public ElstAtom.EditListTableItem[] editListTableItems;

    public ElstAtom() {
    }

    @Override
    public String getName() {
        return "elst";
    }

    @Override
    public List<AtomField> getAtomFieldList() {
        List<AtomField> list = super.getAtomFieldList();
        list.addAll(parseAtomFieldFromAnnotations(this, new long[0]));
        long offset = ((AtomField)list.get(list.size() - 1)).getEndPosition();

        for(int i = 0; i < this.entryCount; ++i) {
            AtomField atomField = this.editListTableItems[i].toAtomField(i, this.version, offset);
            list.add(atomField);
            offset = atomField.getEndPosition();
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
        this.editListTableItems = new ElstAtom.EditListTableItem[this.entryCount];

        for(int i = 0; i < this.entryCount; ++i) {
            this.editListTableItems[i] = new ElstAtom.EditListTableItem();
            this.editListTableItems[i].parse(input, this.version);
        }

    }

    public static class EditListTableItem {
        public long segmentDuration;
        public long mediaTime;
        public float mediaRate;

        public EditListTableItem() {
        }

        public void parse(ParsableByteArray input, int version) {
            if (version == 0) {
                this.segmentDuration = input.readUnsignedInt();
                this.mediaTime = input.readUnsignedInt();
            } else {
                this.segmentDuration = input.readLong();
                this.mediaTime = input.readLong();
            }

            this.mediaRate = (float)input.readUnsignedShort() + Util.intToDecimalPart(input.readUnsignedShort());
        }

        public AtomField<ElstAtom.EditListTableItem> toAtomField(int index, int version, long offset) {
            return new AtomField("Edit List Table{" + index + "}", this, offset, version == 0 ? 12L : 20L);
        }

        public String toString() {
            return "[segmentDuration=" + this.segmentDuration + ", mediaTime=" + this.mediaTime + ", mediaRate=" + this.mediaRate + ']';
        }
    }
}
