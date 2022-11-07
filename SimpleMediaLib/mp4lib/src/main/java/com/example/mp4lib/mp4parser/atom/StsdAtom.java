package com.example.mp4lib.mp4parser.atom;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//


import com.example.sml_mp4_parser.annotation.AtomFieldAnnotation;
import com.example.sml_mp4_parser.util.ParsableByteArray;
import com.example.sml_mp4_parser.util.Util;

import java.io.IOException;
import java.util.List;

public class StsdAtom extends Atom {
    public int trackType;
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
    public StsdAtom.SampleEntry[] sampleEntries;

    public StsdAtom(int trackType) {
        this.trackType = trackType;
    }

    @Override
    public String getName() {
        return "stsd";
    }

    public int getTrackType() {
        return this.trackType;
    }

    @Override
    public List<AtomField> getAtomFieldList() {
        List<AtomField> list = super.getAtomFieldList();
        list.addAll(parseAtomFieldFromAnnotations(this, new long[0]));

        for(int i = 0; i < this.entryCount; ++i) {
            long offset = ((AtomField)list.get(list.size() - 1)).getEndPosition();
            this.sampleEntries[i].addAtomFields(list, i, offset);
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
        this.sampleEntries = new StsdAtom.SampleEntry[this.entryCount];

        for(int i = 0; i < this.entryCount; ++i) {
            if (this.trackType == 1986618469) {
                this.sampleEntries[i] = new StsdAtom.VideoSampleEntry();
                this.sampleEntries[i].parse(input);
            } else if (this.trackType == 1936684398) {
                this.sampleEntries[i] = new StsdAtom.AudioSampleEntry();
                this.sampleEntries[i].parse(input);
            } else {
                this.sampleEntries[i] = new StsdAtom.SampleEntry() {
                    @Override
                    void addAtomFields(List<AtomField> list, int index, long offset) {
                    }
                };
            }
        }

    }

    public static class AudioSampleEntry extends StsdAtom.SampleEntry {
        @AtomFieldAnnotation(
                name = "   channelCount",
                size = 2
        )
        public int channelCount;
        @AtomFieldAnnotation(
                name = "   sampleSize",
                size = 2
        )
        public int sampleSize;
        @AtomFieldAnnotation(
                name = "   audioCid",
                size = 2
        )
        public int audioCid;
        @AtomFieldAnnotation(
                name = "   packetSize",
                size = 2
        )
        public int packetSize;
        @AtomFieldAnnotation(
                name = "   sampleRate",
                size = 4
        )
        public int sampleRate;
        public StsdAtom.BaseExtension extension;

        public AudioSampleEntry() {
        }

        @Override
        void addAtomFields(List<AtomField> list, int index, long offset) {
            AtomField<Integer> titleField = new AtomField("Audio Entry, index ", index, offset, 0L);
            list.add(titleField);
            list.addAll(parseAtomFieldFromAnnotations(this, new long[]{0L, titleField.getEndPosition(), -1L}));
            this.extension.addAtomFields(list, ((AtomField)list.get(list.size() - 1)).getEndPosition());
        }

        @Override
        void parse(ParsableByteArray input) {
            super.parse(input);
            this.channelCount = input.readUnsignedShort();
            this.sampleSize = input.readUnsignedShort();
            this.audioCid = input.readUnsignedShort();
            this.packetSize = input.readUnsignedShort();
            this.sampleRate = input.readUnsignedShort();
            input.skipBytes(2);
            this.extension = StsdAtom.BaseExtension.parseExtension(input);
            this.extension.parse(input);
        }
    }

    public static class AvcCExtension extends StsdAtom.BaseExtension {
        @AtomFieldAnnotation(
                name = "     size",
                size = 4
        )
        public int size;
        public int type;
        @AtomFieldAnnotation(
                name = "     type",
                size = 4
        )
        private String typeHex;
        @AtomFieldAnnotation(
                name = "     version",
                size = 1
        )
        public int version;
        public int profile;
        @AtomFieldAnnotation(
                name = "     profile",
                size = 1
        )
        private String profileHex;
        @AtomFieldAnnotation(
                name = "     reserved",
                size = 1
        )
        public int reserved;
        @AtomFieldAnnotation(
                name = "     level",
                size = 1
        )
        public int level;
        @AtomFieldAnnotation(
                name = "     nalUnitLengthFieldLength",
                size = 1
        )
        public int nalUnitLengthFieldLength;
        @AtomFieldAnnotation(
                name = "     numOfSps",
                size = 1
        )
        public int numOfSps;
        @AtomFieldAnnotation(
                name = "     spsLength",
                size = 2
        )
        public int spsLength;
        @AtomFieldAnnotation(
                name = "     sps data",
                size = 1
        )
        public byte[] sps;
        @AtomFieldAnnotation(
                name = "     numOfPps",
                size = 1
        )
        public int numOfPps;
        @AtomFieldAnnotation(
                name = "     ppsLength",
                size = 2
        )
        public int ppsLength;
        @AtomFieldAnnotation(
                name = "     pps data",
                size = 1
        )
        public byte[] pps;

        public AvcCExtension() {
        }

        @Override
        void addAtomFields(List<AtomField> list, long offset) {
            AtomField titleField = new AtomField("   avcc data", ":", offset + 2L, 0L);
            list.add(titleField);
            list.addAll(parseAtomFieldFromAnnotations(this, new long[]{0L, offset + 2L, -1L, 9L, -1L, (long)this.spsLength, 12L, -1L, (long)this.ppsLength}));
        }

        @Override
        void parse(ParsableByteArray input) {
            input.skipBytes(2);
            this.size = input.readInt();
            this.type = input.readInt();
            this.typeHex = String.format("0x%X", this.type);
            this.version = input.readUnsignedByte();
            this.profile = input.readUnsignedByte();
            this.profileHex = String.format("0x%X", this.profile);
            this.reserved = input.readUnsignedByte();
            this.level = input.readUnsignedByte();
            this.nalUnitLengthFieldLength = (input.readUnsignedByte() & 3) + 1;
            this.numOfSps = input.readUnsignedByte() & 31;
            this.spsLength = input.readUnsignedShort();
            this.sps = new byte[this.spsLength];
            input.readBytes(this.sps, 0, this.spsLength);
            this.numOfPps = input.readUnsignedByte();
            this.ppsLength = input.readUnsignedShort();
            this.pps = new byte[this.ppsLength];
        }
    }

    public static class BaseExtension {
        public static final int TYPE_AVCC = 1635148611;
        public byte[] extensions;

        public BaseExtension() {
        }

        void addAtomFields(List<AtomField> list, long offset) {
            AtomField<Integer> extensionsField = new AtomField("   extensions, length ", this.extensions.length, ((AtomField)list.get(list.size() - 1)).getEndPosition(), (long)this.extensions.length);
            list.add(extensionsField);
        }

        void parse(ParsableByteArray input) {
            int bytesLeft = input.bytesLeft();
            this.extensions = new byte[bytesLeft];
            if (bytesLeft > 0) {
                input.readBytes(this.extensions, 0, bytesLeft);
            }

        }

        static StsdAtom.BaseExtension parseExtension(ParsableByteArray input) {
            if (input.bytesLeft() < 10) {
                return new StsdAtom.BaseExtension();
            } else {
                input.skipBytes(2);
                input.readInt();
                int type = input.readInt();
                input.setPosition(input.getPosition() - 10);
                return (StsdAtom.BaseExtension)(type == 1635148611 ? new StsdAtom.AvcCExtension() : new StsdAtom.BaseExtension());
            }
        }
    }

    public static class VideoSampleEntry extends StsdAtom.SampleEntry {
        @AtomFieldAnnotation(
                name = "   temporalQuality",
                size = 4
        )
        public int temporalQuality;
        @AtomFieldAnnotation(
                name = "   spatialQuality",
                size = 4
        )
        public int spatialQuality;
        @AtomFieldAnnotation(
                name = "   width",
                size = 2
        )
        public int width;
        @AtomFieldAnnotation(
                name = "   height",
                size = 2
        )
        public int height;
        @AtomFieldAnnotation(
                name = "   horizontalSolution",
                size = 4
        )
        public float horizontalSolution;
        @AtomFieldAnnotation(
                name = "   verticalSolution",
                size = 4
        )
        public float verticalSolution;
        @AtomFieldAnnotation(
                name = "   reserved",
                size = 4
        )
        public int reserved;
        @AtomFieldAnnotation(
                name = "   frameCount",
                size = 2
        )
        public int frameCount;
        @AtomFieldAnnotation(
                name = "   codecName",
                size = 4
        )
        public String codecName;
        public int codecNameLength;
        @AtomFieldAnnotation(
                name = "   depth",
                size = 2
        )
        public int depth;
        public StsdAtom.BaseExtension extension;

        public VideoSampleEntry() {
        }

        @Override
        void addAtomFields(List<AtomField> list, int index, long offset) {
            AtomField<Integer> titleField = new AtomField("Video Entry, index ", index, offset, 0L);
            list.add(titleField);
            list.addAll(parseAtomFieldFromAnnotations(this, new long[]{0L, titleField.getEndPosition(), -1L, 16L, -1L, (long)this.codecNameLength}));
            this.extension.addAtomFields(list, ((AtomField)list.get(list.size() - 1)).getEndPosition());
        }

        @Override
        void parse(ParsableByteArray input) {
            super.parse(input);
            this.temporalQuality = input.readInt();
            this.spatialQuality = input.readInt();
            this.width = input.readUnsignedShort();
            this.height = input.readUnsignedShort();
            this.horizontalSolution = (float)input.readUnsignedShort() + Util.intToDecimalPart(input.readUnsignedShort());
            this.verticalSolution = (float)input.readUnsignedShort() + Util.intToDecimalPart(input.readUnsignedShort());
            this.reserved = input.readInt();
            this.frameCount = input.readUnsignedShort();
            int len = input.readUnsignedByte();
            if (len > 31) {
                len = 31;
            }

            this.codecName = input.readString(len);
            this.codecNameLength = 1 + len;
            if (len < 31) {
                this.codecNameLength += 31 - len;
                input.skipBytes(31 - len);
            }

            if (this.codecName == null || this.codecName.length() <= 0) {
                this.codecName = this.formatStr + "(from codecId)";
            }

            this.depth = input.readUnsignedShort();
            this.extension = StsdAtom.BaseExtension.parseExtension(input);
            this.extension.parse(input);
        }
    }

    public abstract static class SampleEntry {
        @AtomFieldAnnotation(
                name = "   size",
                size = 4
        )
        public int size;
        @AtomFieldAnnotation(
                name = "   codecId",
                size = 4
        )
        public int format;
        protected String formatStr;
        @AtomFieldAnnotation(
                name = "   reserved",
                size = 4
        )
        public int reserved;
        @AtomFieldAnnotation(
                name = "   reserved2",
                size = 2
        )
        public int reserved2;
        @AtomFieldAnnotation(
                name = "   dataReferenceIndex",
                size = 2
        )
        public int dataReferenceIndex;
        @AtomFieldAnnotation(
                name = "   version",
                size = 2
        )
        public int version;
        @AtomFieldAnnotation(
                name = "   revisionLevel",
                size = 2
        )
        public int revisionLevel;
        @AtomFieldAnnotation(
                name = "   vendor",
                size = 4
        )
        public String vendor;

        public SampleEntry() {
        }

        abstract void addAtomFields(List<AtomField> var1, int var2, long var3);

        void parse(ParsableByteArray input) {
            this.size = input.readInt();
            this.format = input.readInt();
            input.setPosition(input.getPosition() - 4);
            this.formatStr = input.readString(4);
            this.reserved = input.readInt();
            this.reserved2 = input.readUnsignedShort();
            this.dataReferenceIndex = input.readUnsignedShort();
            this.version = input.readUnsignedShort();
            this.revisionLevel = input.readUnsignedShort();
            this.vendor = input.readString(4);
        }
    }
}

