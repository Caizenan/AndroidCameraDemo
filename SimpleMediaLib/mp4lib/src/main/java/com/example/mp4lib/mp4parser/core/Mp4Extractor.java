package com.example.mp4lib.mp4parser.input.core;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//


import com.example.mp4lib.mp4parser.atom.Atom;
import com.example.mp4lib.mp4parser.atom.Co64Atom;
import com.example.mp4lib.mp4parser.atom.CttsAtom;
import com.example.mp4lib.mp4parser.atom.DinfAtom;
import com.example.mp4lib.mp4parser.atom.DrefAtom;
import com.example.mp4lib.mp4parser.atom.EdtsAtom;
import com.example.mp4lib.mp4parser.atom.ElstAtom;
import com.example.mp4lib.mp4parser.atom.FreeAtom;
import com.example.mp4lib.mp4parser.atom.FtypAtom;
import com.example.mp4lib.mp4parser.atom.HdlrAtom;
import com.example.mp4lib.mp4parser.atom.MdatAtom;
import com.example.mp4lib.mp4parser.atom.MdhdAtom;
import com.example.mp4lib.mp4parser.atom.MdiaAtom;
import com.example.mp4lib.mp4parser.atom.MinfAtom;
import com.example.mp4lib.mp4parser.atom.MoovAtom;
import com.example.mp4lib.mp4parser.atom.MvhdAtom;
import com.example.mp4lib.mp4parser.atom.OtherAtom;
import com.example.mp4lib.mp4parser.atom.RootAtom;
import com.example.mp4lib.mp4parser.atom.SmhdAtom;
import com.example.mp4lib.mp4parser.atom.StblAtom;
import com.example.mp4lib.mp4parser.atom.StcoAtom;
import com.example.mp4lib.mp4parser.atom.StscAtom;
import com.example.mp4lib.mp4parser.atom.StsdAtom;
import com.example.mp4lib.mp4parser.atom.StssAtom;
import com.example.mp4lib.mp4parser.atom.StszAtom;
import com.example.mp4lib.mp4parser.atom.SttsAtom;
import com.example.mp4lib.mp4parser.atom.Stz2Atom;
import com.example.mp4lib.mp4parser.atom.TkhdAtom;
import com.example.mp4lib.mp4parser.atom.TrackAtom;
import com.example.mp4lib.mp4parser.atom.UdtaAtom;
import com.example.mp4lib.mp4parser.atom.VmhdAtom;
import com.example.mp4lib.mp4parser.input.ExtractorInput;
import com.example.mp4lib.mp4parser.core.TrackSampleTable;
import com.example.mp4lib.mp4parser.util.ParsableByteArray;
import com.example.mp4lib.mp4parser.util.Sniff;

import java.io.EOFException;
import java.io.IOException;

public class Mp4Extractor {
    private static final int STATE_READING_ATOM_HEADER = 0;
    private static final int STATE_READING_ATOM_PAYLOAD = 1;
    private static final int STATE_READING_SAMPLE = 2;
    private int parserState = 0;
    private int atomType;
    private long atomSize;
    private int atomHeaderBytesRead;
    private ParsableByteArray atomData;
    private ParsableByteArray scratch = new ParsableByteArray();
    private final ParsableByteArray atomHeader = new ParsableByteArray(16);
    private Atom containerAtom;
    private Atom currentAtom;
    private int currentTrackType = -1;
    private TrackSampleTable[] trackSampleTables;

    public Mp4Extractor() {
    }

    public boolean sniff(ExtractorInput input) {
        try {
            boolean var2 = Sniff.sniffMp4(input);
            return var2;
        } catch (IOException var6) {
            var6.printStackTrace();
        } finally {
            input.resetPeekPosition();
        }

        return false;
    }

    public Atom readHeader(ExtractorInput input) throws IOException {
        this.containerAtom = new RootAtom();
        this.currentAtom = this.containerAtom;

        try {
            boolean running = true;

            while(running && input.getPosition() < input.getLength()) {
                switch(this.parserState) {
                    case 0:
                        if (!this.readAtomHeader(input)) {
                            running = false;
                        }
                        break;
                    case 1:
                        if (!this.readAtomPayload(input)) {
                            running = false;
                        }
                        break;
                    default:
                        running = false;
                }
            }

            this.trackSampleTables = TrackSampleTable.parse(this.currentAtom);
        } catch (EOFException var3) {
        }

        System.out.println("read header complete " + input.getPosition());
        return this.containerAtom;
    }

    private boolean readAtomHeader(ExtractorInput input) throws IOException {
        if (this.atomHeaderBytesRead == 0) {
            if (!input.readFully(this.atomHeader.getData(), 0, 8, true)) {
                return false;
            }

            this.atomHeaderBytesRead = 8;
            this.atomHeader.setPosition(0);
            this.atomSize = this.atomHeader.readUnsignedInt();
            this.atomType = this.atomHeader.readInt();
        }

        if (this.atomSize == 1L) {
            int headerBytesRemaining = 8;
            input.readFully(this.atomHeader.getData(), 8, headerBytesRemaining);
            this.atomHeaderBytesRead += headerBytesRemaining;
            this.atomSize = this.atomHeader.readUnsignedLongToLong();
        } else if (this.atomSize == 0L) {
            long endPosition = input.getLength();
            this.atomSize = endPosition - input.getPosition() + (long)this.atomHeaderBytesRead;
        }

        if (this.atomSize < (long)this.atomHeaderBytesRead) {
            throw new IOException("Atom size less than header length (unsupported).");
        } else {
            if (shouldParseContainerAtom(this.atomType)) {
                if (this.atomSize != (long)this.atomHeaderBytesRead && this.atomType == 1835365473) {
                    this.maybeSkipRemainingMetaAtomHeaderBytes(input);
                }

                Atom atom = this.createContainerAtom(this.atomType);
                this.atomHeader.setPosition(0);
                atom.parse(input.getPosition() - (long)this.atomHeaderBytesRead, this.atomHeader);
                this.currentAtom.addChildAtom(atom);
                this.currentAtom = atom;
                this.atomHeaderBytesRead = 0;
            } else if (shouldParseLeafAtom(this.atomType)) {
                ParsableByteArray atomData = new ParsableByteArray((int)this.atomSize);
                System.arraycopy(this.atomHeader.getData(), 0, atomData.getData(), 0, 8);
                this.atomData = atomData;
                this.parserState = 1;
            } else {
                this.atomData = null;
                this.parserState = 1;
            }

            return true;
        }
    }

    private boolean readAtomPayload(ExtractorInput input) throws IOException {
        long atomPayloadSize = this.atomSize - (long)this.atomHeaderBytesRead;
        long atomEndPosition = input.getPosition() + atomPayloadSize;
        boolean seekRequired = false;
        ParsableByteArray atomData = this.atomData;
        if (atomData != null) {
            input.readFully(atomData.getData(), this.atomHeaderBytesRead, (int)atomPayloadSize);
            Atom atom = this.createLeafAtom(this.atomType);
            atom.parse(input.getPosition() - atomPayloadSize - (long)this.atomHeaderBytesRead, atomData);
            if (atom instanceof HdlrAtom && this.currentAtom instanceof MdiaAtom) {
                System.out.println("readAtomPayload " + atom + " " + ((HdlrAtom)atom).getHandlerType());
                this.currentTrackType = ((HdlrAtom)atom).getHandlerType();
            }

            this.currentAtom.addChildAtom(atom);
            this.processAtomEnded(atom.getEndPosition());
        } else {
            atomData = new ParsableByteArray(8);
            Atom atom = new OtherAtom(this.atomSize, this.atomType);
            atom.parse(input.getPosition() - 8L, atomData);
            this.currentAtom.addChildAtom(atom);
            input.skipFully((int)atomPayloadSize);
            this.processAtomEnded(input.getPosition());
        }

        return true;
    }

    private void processAtomEnded(long atomEndPosition) {
        while(this.currentAtom.getPreAtom() != null && atomEndPosition == this.currentAtom.getEndPosition()) {
            this.currentAtom = this.currentAtom.getPreAtom() != null ? this.currentAtom.getPreAtom() : this.currentAtom;
        }

        if (this.parserState != 2) {
            this.atomHeaderBytesRead = 0;
            this.parserState = 0;
        }

    }

    private static boolean shouldParseLeafAtom(int atom) {
        return atom == 1835296868 || atom == 1836476516 || atom == 1751411826 || atom == 1937011556 || atom == 1937011827 || atom == 1937011571 || atom == 1668576371 || atom == 1701606260 || atom == 1937011555 || atom == 1937011578 || atom == 1937013298 || atom == 1937007471 || atom == 1668232756 || atom == 1953196132 || atom == 1718909296 || atom == 1969517665 || atom == 1801812339 || atom == 1718773093 || atom == 1835295092 || atom == 1986881636 || atom == 1936549988 || atom == 1685218662 || atom == 1768715124;
    }

    private static boolean shouldParseContainerAtom(int atom) {
        return atom == 1836019574 || atom == 1953653099 || atom == 1835297121 || atom == 1835626086 || atom == 1937007212 || atom == 1701082227 || atom == 1684631142 || atom == 1835365473;
    }

    private Atom createLeafAtom(int atom) {
        switch(atom) {
            case 1668232756:
                return new Co64Atom();
            case 1668576371:
                return new CttsAtom();
            case 1685218662:
                return new DrefAtom();
            case 1701606260:
                return new ElstAtom();
            case 1718773093:
                return new FreeAtom();
            case 1718909296:
                return new FtypAtom();
            case 1751411826:
                return new HdlrAtom();
            case 1835295092:
                return new MdatAtom();
            case 1835296868:
                return new MdhdAtom();
            case 1836476516:
                return new MvhdAtom();
            case 1936549988:
                return new SmhdAtom();
            case 1937007471:
                return new StcoAtom();
            case 1937011555:
                return new StscAtom();
            case 1937011556:
                return new StsdAtom(this.currentTrackType);
            case 1937011571:
                return new StssAtom();
            case 1937011578:
                return new StszAtom();
            case 1937011827:
                return new SttsAtom();
            case 1937013298:
                return new Stz2Atom();
            case 1953196132:
                return new TkhdAtom();
            case 1969517665:
                return new UdtaAtom();
            case 1986881636:
                return new VmhdAtom();
            default:
                return new OtherAtom();
        }
    }

    private Atom createContainerAtom(int atom) {
        switch(atom) {
            case 1684631142:
                return new DinfAtom();
            case 1701082227:
                return new EdtsAtom();
            case 1835297121:
                return new MdiaAtom();
            case 1835626086:
                return new MinfAtom();
            case 1836019574:
                return new MoovAtom();
            case 1937007212:
                return new StblAtom();
            case 1953653099:
                return new TrackAtom();
            default:
                return new OtherAtom();
        }
    }

    private void maybeSkipRemainingMetaAtomHeaderBytes(ExtractorInput input) throws IOException {
        this.scratch.reset(8);
        input.peekFully(this.scratch.getData(), 0, 8);
        maybeSkipRemainingMetaAtomHeaderBytes(this.scratch);
        input.skipFully(this.scratch.getPosition());
        input.resetPeekPosition();
    }

    public static void maybeSkipRemainingMetaAtomHeaderBytes(ParsableByteArray meta) {
        int endPosition = meta.getPosition();
        meta.skipBytes(4);
        if (meta.readInt() != 1751411826) {
            endPosition += 4;
        }

        meta.setPosition(endPosition);
    }

    public TrackSampleTable[] getTrackSampleTables() {
        return this.trackSampleTables;
    }
}

