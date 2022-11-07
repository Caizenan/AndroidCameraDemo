package com.example.mp4lib.mp4parser.atom;

import com.example.sml_mp4_parser.annotation.AtomFieldAnnotation;
import com.example.sml_mp4_parser.util.ParsableByteArray;
import com.example.sml_mp4_parser.util.Util;

import java.util.ArrayList;
import java.util.List;

public class FtypAtom extends Atom{
    @AtomFieldAnnotation(
            name = "majorBrand",
            size = 4,
            offset = 8
    )
    public String majorBrand;
    @AtomFieldAnnotation(
            name = "minorVersion",
            size = 4
    )
    public int minorVersion;
    public List<String> compatibleBrands;

    public FtypAtom() {
    }

    @Override
    public String getName() {
        return "ftyp";
    }

    @Override
    public List<AtomField> getAtomFieldList() {
        List<AtomField> list = super.getAtomFieldList();
        list.addAll(parseAtomFieldFromAnnotations(this, new long[0]));
        AtomField<List<String>> compatibleBrandsField = new AtomField("compatibleBrands", this.compatibleBrands, ((AtomField)list.get(list.size() - 1)).getEndPosition(), (long)(this.compatibleBrands.size() * 4));
        list.add(compatibleBrandsField);
        return list;
    }

    @Override
    public void parse(long position, ParsableByteArray input) {
        this.setOffset(position);
        this.setSize(input.readUnsignedInt());
        this.setType(input.readInt());
        byte[] buffer = new byte[4];
        input.readBytes(buffer, 0, buffer.length);
        this.majorBrand = Util.fromUtf8Bytes(buffer);
        this.minorVersion = input.readInt();
        int length = input.bytesLeft() / 4;
        this.compatibleBrands = new ArrayList();

        for(int i = 0; i < length; ++i) {
            input.readBytes(buffer, 0, buffer.length);
            this.compatibleBrands.add(Util.fromUtf8Bytes(buffer));
        }

    }
}
