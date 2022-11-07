package com.example.mp4lib.mp4parser.atom;

import com.example.sml_mp4_parser.annotation.AtomFieldAnnotation;
import com.example.sml_mp4_parser.util.ParsableByteArray;
import com.example.sml_mp4_parser.util.Util;

import java.io.IOException;
import java.util.List;

public class DrefAtom extends Atom{
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
            name = "count",
            size = 4
    )
    public int entryCount;
    public DrefAtom.DrefEntryItem[] drefEntryItems;

    public DrefAtom() {
    }

    @Override
    public String getName() {
        return "dref";
    }

    @Override
    public List<AtomField> getAtomFieldList() {
        List<AtomField> list = super.getAtomFieldList();
        list.addAll(parseAtomFieldFromAnnotations(this, new long[0]));
        AtomField<Integer> entryCountField = (AtomField)list.get(list.size() - 1);
        long offset = entryCountField.getEndPosition();

        for(int i = 0; i < this.entryCount; ++i) {
            this.drefEntryItems[i].addAtomFields(list, i, offset);
            offset += (long)this.drefEntryItems[i].size;
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
        this.drefEntryItems = new DrefAtom.DrefEntryItem[this.entryCount];

        for(int i = 0; i < this.entryCount; ++i) {
            this.drefEntryItems[i] = new DrefAtom.DrefEntryItem();
            this.drefEntryItems[i].parse(input);
        }

    }

    public static class DrefEntryItem {
        public int size;
        @AtomFieldAnnotation(
                name = "   type",
                size = 4
        )
        private String type;
        @AtomFieldAnnotation(
                name = "   version",
                size = 1
        )
        private int version;
        @AtomFieldAnnotation(
                name = "   flag",
                size = 3
        )
        private int flag;
        private int dataSize;

        public DrefEntryItem() {
        }

        public void parse(ParsableByteArray input) {
            this.size = input.readInt();
            byte[] buffer = new byte[4];
            input.readBytes(buffer, 0, buffer.length);
            this.type = Util.fromUtf8Bytes(buffer);
            this.version = input.readUnsignedByte();
            this.flag = input.readInt24();
            this.dataSize = input.bytesLeft();
        }

        public void addAtomFields(List<AtomField> list, int index, long offset) {
            AtomField<Integer> titleField = new AtomField("Entry: " + index + " , size ", this.size, offset, 4L);
            AtomField<Integer> dataField = new AtomField("   Entry Data, length ", this.dataSize, ((AtomField)list.get(list.size() - 1)).getEndPosition(), (long)this.dataSize);
            list.add(titleField);
            list.addAll(parseAtomFieldFromAnnotations(this, new long[]{0L, titleField.getEndPosition(), -1L}));
            list.add(dataField);
        }
    }
}
