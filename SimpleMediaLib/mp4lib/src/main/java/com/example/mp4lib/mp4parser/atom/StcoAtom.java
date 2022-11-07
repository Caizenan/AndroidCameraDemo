package com.example.mp4lib.mp4parser.atom;


import com.example.mp4lib.mp4parser.annotation.AtomFieldAnnotation;
import com.example.mp4lib.mp4parser.util.ParsableByteArray;

import java.io.IOException;
import java.util.List;

public class StcoAtom extends Atom{
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
    public StcoAtom.ChunkOffsetItem[] chunkOffsetItems;
    public boolean isCo64;

    public StcoAtom() {
        this.isCo64 = false;
    }

    StcoAtom(boolean isCo64) {
        this.isCo64 = isCo64;
    }

    public int getEntryCount() {
        return this.entryCount;
    }

    public StcoAtom.ChunkOffsetItem[] getChunkOffsetItems() {
        return this.chunkOffsetItems;
    }

    @Override
    public String getName() {
        return "stco";
    }

    @Override
    public List<AtomField> getAtomFieldList() {
        List<AtomField> list = super.getAtomFieldList();
        list.addAll(parseAtomFieldFromAnnotations(this, new long[0]));
        long offset = ((AtomField)list.get(list.size() - 1)).getEndPosition();
        list.add(new AtomField("Chunk Offset Table , size ", this.entryCount, offset, 4L));
        offset += 4L;

        for(int i = 0; i < this.entryCount; ++i) {
            this.chunkOffsetItems[i].addAtomField(list, i, offset, this.isCo64);
            offset += 4L;
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
        this.chunkOffsetItems = new StcoAtom.ChunkOffsetItem[this.entryCount];

        for(int i = 0; i < this.entryCount; ++i) {
            this.chunkOffsetItems[i] = new StcoAtom.ChunkOffsetItem();
            this.chunkOffsetItems[i].parse(input, this.isCo64);
        }

    }

    public static class ChunkOffsetItem {
        public long chunkOffset;

        public ChunkOffsetItem() {
        }

        void addAtomField(List<AtomField> list, int index, long offset, boolean isCo64) {
            list.add(new AtomField("" + index, "[chunkOffset = " + this.chunkOffset + "]", offset, isCo64 ? 8L : 4L));
        }

        void parse(ParsableByteArray input, boolean isCo64) {
            if (!isCo64) {
                this.chunkOffset = (long)input.readInt();
            } else {
                this.chunkOffset = input.readLong();
            }

        }
    }
}
