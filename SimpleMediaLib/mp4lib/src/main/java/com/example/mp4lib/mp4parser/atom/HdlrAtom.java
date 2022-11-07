package com.example.mp4lib.mp4parser.atom;


import com.example.mp4lib.mp4parser.annotation.AtomFieldAnnotation;
import com.example.mp4lib.mp4parser.util.ParsableByteArray;
import com.example.mp4lib.mp4parser.util.Util;

import java.io.IOException;
import java.util.List;

public class HdlrAtom extends Atom{
    public static final int TYPE_vide = 1986618469;
    public static final int TYPE_soun = 1936684398;
    public static final int TYPE_text = 1952807028;
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
            name = "componentType",
            size = 4
    )
    public int componentType;
    private int componentSubType;
    @AtomFieldAnnotation(
            name = "componentSubType",
            size = 4
    )
    public String componentSubTypeStr;
    @AtomFieldAnnotation(
            name = "componentManufacture",
            size = 4
    )
    public int componentManufacture;
    @AtomFieldAnnotation(
            name = "componentFlags",
            size = 4
    )
    public int componentFlags;
    @AtomFieldAnnotation(
            name = "componentFlagsMask",
            size = 4
    )
    public int componentFlagsMask;
    @AtomFieldAnnotation(
            name = "componentName",
            size = 0
    )
    public String componentName;
    public int componentNameSize;

    public HdlrAtom() {
    }

    @Override
    public String getName() {
        return "hdlr";
    }

    public int getHandlerType() {
        return this.componentSubType;
    }

    @Override
    public List<AtomField> getAtomFieldList() {
        List<AtomField> list = super.getAtomFieldList();
        list.addAll(parseAtomFieldFromAnnotations(this, new long[]{7L, -1L, (long)this.componentNameSize}));
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
        this.componentType = input.readInt();
        this.componentSubType = input.readInt();
        input.setPosition(input.getPosition() - 4);
        byte[] buffer = new byte[4];
        input.readBytes(buffer, 0, buffer.length);
        this.componentSubTypeStr = Util.fromUtf8Bytes(buffer);
        this.componentManufacture = input.readInt();
        this.componentFlags = input.readInt();
        this.componentFlagsMask = input.readInt();
        int titleSize = input.bytesLeft();
        this.componentNameSize = titleSize;
        if (titleSize <= 0) {
            this.componentName = "";
        } else {
            this.componentName = input.readString(titleSize);
        }
    }

}
