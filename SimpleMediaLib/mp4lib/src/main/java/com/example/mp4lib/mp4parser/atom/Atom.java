package com.example.mp4lib.mp4parser.atom;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import com.example.mp4lib.mp4parser.annotation.AtomFieldAnnotation;
import com.example.mp4lib.mp4parser.util.ParsableByteArray;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class Atom {
    public static final int HEADER_SIZE = 8;
    public static final int FULL_HEADER_SIZE = 12;
    public static final int LONG_HEADER_SIZE = 16;
    public static final int DEFINES_LARGE_SIZE = 1;
    public static final int EXTENDS_TO_END_SIZE = 0;
    public static final int TYPE_ftyp = 1718909296;
    public static final int TYPE_avc1 = 1635148593;
    public static final int TYPE_avc3 = 1635148595;
    public static final int TYPE_avcC = 1635148611;
    public static final int TYPE_hvc1 = 1752589105;
    public static final int TYPE_hev1 = 1751479857;
    public static final int TYPE_hvcC = 1752589123;
    public static final int TYPE_vp08 = 1987063864;
    public static final int TYPE_vp09 = 1987063865;
    public static final int TYPE_vpcC = 1987076931;
    public static final int TYPE_av01 = 1635135537;
    public static final int TYPE_av1C = 1635135811;
    public static final int TYPE_dvav = 1685479798;
    public static final int TYPE_dva1 = 1685479729;
    public static final int TYPE_dvhe = 1685481573;
    public static final int TYPE_dvh1 = 1685481521;
    public static final int TYPE_dvcC = 1685480259;
    public static final int TYPE_dvvC = 1685485123;
    public static final int TYPE_s263 = 1932670515;
    public static final int TYPE_d263 = 1681012275;
    public static final int TYPE_mdat = 1835295092;
    public static final int TYPE_mp4a = 1836069985;
    public static final int TYPE__mp2 = 778924082;
    public static final int TYPE__mp3 = 778924083;
    public static final int TYPE_wave = 2002876005;
    public static final int TYPE_lpcm = 1819304813;
    public static final int TYPE_sowt = 1936684916;
    public static final int TYPE_ac_3 = 1633889587;
    public static final int TYPE_dac3 = 1684103987;
    public static final int TYPE_ec_3 = 1700998451;
    public static final int TYPE_dec3 = 1684366131;
    public static final int TYPE_ac_4 = 1633889588;
    public static final int TYPE_dac4 = 1684103988;
    public static final int TYPE_dtsc = 1685353315;
    public static final int TYPE_dtsh = 1685353320;
    public static final int TYPE_dtsl = 1685353324;
    public static final int TYPE_dtse = 1685353317;
    public static final int TYPE_ddts = 1684305011;
    public static final int TYPE_tfdt = 1952867444;
    public static final int TYPE_tfhd = 1952868452;
    public static final int TYPE_trex = 1953654136;
    public static final int TYPE_trun = 1953658222;
    public static final int TYPE_sidx = 1936286840;
    public static final int TYPE_moov = 1836019574;
    public static final int TYPE_mpvd = 1836086884;
    public static final int TYPE_mvhd = 1836476516;
    public static final int TYPE_trak = 1953653099;
    public static final int TYPE_mdia = 1835297121;
    public static final int TYPE_minf = 1835626086;
    public static final int TYPE_stbl = 1937007212;
    public static final int TYPE_esds = 1702061171;
    public static final int TYPE_moof = 1836019558;
    public static final int TYPE_traf = 1953653094;
    public static final int TYPE_mvex = 1836475768;
    public static final int TYPE_mehd = 1835362404;
    public static final int TYPE_tkhd = 1953196132;
    public static final int TYPE_edts = 1701082227;
    public static final int TYPE_elst = 1701606260;
    public static final int TYPE_mdhd = 1835296868;
    public static final int TYPE_hdlr = 1751411826;
    public static final int TYPE_stsd = 1937011556;
    public static final int TYPE_pssh = 1886614376;
    public static final int TYPE_sinf = 1936289382;
    public static final int TYPE_schm = 1935894637;
    public static final int TYPE_schi = 1935894633;
    public static final int TYPE_tenc = 1952804451;
    public static final int TYPE_encv = 1701733238;
    public static final int TYPE_enca = 1701733217;
    public static final int TYPE_frma = 1718775137;
    public static final int TYPE_saiz = 1935763834;
    public static final int TYPE_saio = 1935763823;
    public static final int TYPE_sbgp = 1935828848;
    public static final int TYPE_sgpd = 1936158820;
    public static final int TYPE_uuid = 1970628964;
    public static final int TYPE_senc = 1936027235;
    public static final int TYPE_pasp = 1885434736;
    public static final int TYPE_TTML = 1414810956;
    public static final int TYPE_m1v_ = 1831958048;
    public static final int TYPE_mp4v = 1836070006;
    public static final int TYPE_stts = 1937011827;
    public static final int TYPE_stss = 1937011571;
    public static final int TYPE_ctts = 1668576371;
    public static final int TYPE_stsc = 1937011555;
    public static final int TYPE_stsz = 1937011578;
    public static final int TYPE_stz2 = 1937013298;
    public static final int TYPE_stco = 1937007471;
    public static final int TYPE_co64 = 1668232756;
    public static final int TYPE_tx3g = 1954034535;
    public static final int TYPE_wvtt = 2004251764;
    public static final int TYPE_stpp = 1937010800;
    public static final int TYPE_c608 = 1664495672;
    public static final int TYPE_samr = 1935764850;
    public static final int TYPE_sawb = 1935767394;
    public static final int TYPE_udta = 1969517665;
    public static final int TYPE_meta = 1835365473;
    public static final int TYPE_smta = 1936553057;
    public static final int TYPE_saut = 1935766900;
    public static final int TYPE_keys = 1801812339;
    public static final int TYPE_ilst = 1768715124;
    public static final int TYPE_mean = 1835360622;
    public static final int TYPE_name = 1851878757;
    public static final int TYPE_data = 1684108385;
    public static final int TYPE_emsg = 1701671783;
    public static final int TYPE_st3d = 1936995172;
    public static final int TYPE_sv3d = 1937126244;
    public static final int TYPE_proj = 1886547818;
    public static final int TYPE_camm = 1667329389;
    public static final int TYPE_mett = 1835365492;
    public static final int TYPE_alac = 1634492771;
    public static final int TYPE_alaw = 1634492791;
    public static final int TYPE_ulaw = 1970037111;
    public static final int TYPE_Opus = 1332770163;
    public static final int TYPE_dOps = 1682927731;
    public static final int TYPE_fLaC = 1716281667;
    public static final int TYPE_dfLa = 1684425825;
    public static final int TYPE_twos = 1953984371;
    public static final int TYPE_free = 1718773093;
    public static final int TYPE_vmhd = 1986881636;
    public static final int TYPE_smhd = 1936549988;
    public static final int TYPE_dinf = 1684631142;
    public static final int TYPE_dref = 1685218662;
    protected List<Atom> mAtomList = new ArrayList();
    protected Atom preAtom;
    private int type;
    private long offset;
    private long size;
    private byte[] data;

    public Atom() {
    }

    public abstract String getName();

    public long getOffset() {
        return this.offset;
    }

    protected void setOffset(long offset) {
        this.offset = offset;
    }

    public long getSize() {
        return this.size;
    }

    protected void setSize(long size) {
        this.size = size;
    }

    public long getEndPosition() {
        return this.offset + this.size;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return this.type;
    }

    public List<AtomField> getAtomFieldList() {
        List<AtomField> list = new ArrayList();
        list.add(new AtomField<Long>("offset", this.offset, 0L, 4L) {
            public String toString() {
                return "offset = " + Atom.this.offset + " , len = " + Atom.this.size;
            }
        });
        return list;
    }

    public static List<AtomField> parseAtomFieldFromAnnotations(Object atom, long... manualSet) {
        Field[] superFields = atom.getClass().getSuperclass().getDeclaredFields();
        Field[] currentFields = atom.getClass().getDeclaredFields();
        Field[] fields = new Field[(superFields == null ? 0 : superFields.length) + (currentFields == null ? 0 : currentFields.length)];
        if (superFields != null) {
            System.arraycopy(superFields, 0, fields, 0, superFields.length);
        }

        if (currentFields != null) {
            System.arraycopy(currentFields, 0, fields, superFields == null ? 0 : superFields.length, currentFields.length);
        }

        List<AtomField> result = new ArrayList();
        AtomField lastAtomField = null;
        int index = 0;
        int position = 0;
        Field[] var9 = fields;
        int var10 = fields.length;

        for(int var11 = 0; var11 < var10; ++var11) {
            Field field = var9[var11];
            if (field != null) {
                Annotation[] annotations = field.getAnnotations();
                if (annotations != null && annotations.length > 0) {
                    Annotation[] var14 = annotations;
                    int var15 = annotations.length;

                    for(int var16 = 0; var16 < var15; ++var16) {
                        Annotation annotation = var14[var16];
                        if (annotation instanceof AtomFieldAnnotation) {
                            long manualSetOffset = -1L;
                            long manualSetSize = -1L;
                            if (position < manualSet.length && manualSet[position] == (long)index) {
                                manualSetOffset = manualSet[position + 1];
                                manualSetSize = manualSet[position + 2];
                                position += 3;
                            }

                            AtomFieldAnnotation atomFieldAnnotation = (AtomFieldAnnotation)annotation;

                            try {
                                field.setAccessible(true);
                                AtomField atomField = new AtomField(atomFieldAnnotation.name(), field.get(atom), (long)atomFieldAnnotation.offset(), (long)atomFieldAnnotation.size());
                                if (manualSetOffset != -1L) {
                                    atomField.setOffset(manualSetOffset);
                                } else if (lastAtomField != null) {
                                    atomField.setOffset(lastAtomField.getEndPosition());
                                }

                                if (manualSetSize != -1L) {
                                    atomField.setSize(manualSetSize);
                                }

                                lastAtomField = atomField;
                                result.add(atomField);
                            } catch (IllegalAccessException var24) {
                                var24.printStackTrace();
                            }

                            ++index;
                        }
                    }
                }
            }
        }

        return result;
    }

    public List<Atom> getChildAtoms() {
        return this.mAtomList;
    }

    public void setPreAtom(Atom atom) {
        this.preAtom = atom;
    }

    public Atom getPreAtom() {
        return this.preAtom;
    }

    public void addChildAtom(Atom atom) {
        atom.setPreAtom(this);
        this.mAtomList.add(atom);
    }

    public Atom getChildAtom(int type) {
        if (this.mAtomList != null) {
            Iterator var2 = this.mAtomList.iterator();

            while(var2.hasNext()) {
                Atom atom = (Atom)var2.next();
                if (atom.type == type) {
                    return atom;
                }
            }
        }

        return null;
    }

    public void parse(long position, ParsableByteArray input) throws IOException {
    }

    public String toString() {
        return this.getName();
    }
}

