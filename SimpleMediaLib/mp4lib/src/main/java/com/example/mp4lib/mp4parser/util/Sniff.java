package com.example.mp4lib.mp4parser.util;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import com.example.mp4lib.mp4parser.input.ExtractorInput;

import java.io.IOException;

public class Sniff {
    private static final int SEARCH_LENGTH = 4096;
    public static final int BRAND_QUICKTIME = 1903435808;
    private static final int[] COMPATIBLE_BRANDS = new int[]{1769172845, 1769172786, 1769172787, 1769172788, 1769172789, 1769172790, 1769172793, 1635148593, 1752589105, 1751479857, 1635135537, 1836069937, 1836069938, 862401121, 862401122, 862417462, 862417718, 862414134, 862414646, 1295275552, 1295270176, 1714714144, 1801741417, 1295275600, 1903435808, 1297305174, 1684175153, 1769172332, 1885955686};

    public Sniff() {
    }

    public static boolean sniffMp4(ExtractorInput input) throws IOException {
        long inputLength = input.getLength();
        int bytesToSearch = (int)(inputLength != -1L && inputLength <= 4096L ? inputLength : 4096L);
        ParsableByteArray buffer = new ParsableByteArray(64);
        int bytesSearched = 0;
        boolean foundGoodFileType = false;

        while(bytesSearched < bytesToSearch) {
            int headerSize = 8;
            buffer.reset(headerSize);
            boolean success = input.peekFully(buffer.getData(), 0, headerSize, true);
            if (!success) {
                break;
            }

            long atomSize = buffer.readUnsignedInt();
            int atomType = buffer.readInt();
            if (atomSize == 1L) {
                headerSize = 16;
                input.peekFully(buffer.getData(), 8, 8);
                buffer.setLimit(16);
                atomSize = buffer.readLong();
            } else if (atomSize == 0L) {
                long fileEndPosition = input.getLength();
                if (fileEndPosition != -1L) {
                    atomSize = fileEndPosition - input.getPeekPosition() + (long)headerSize;
                }
            }

            if (atomSize < (long)headerSize) {
                return false;
            }

            bytesSearched += headerSize;
            if (atomType == 1836019574) {
                bytesToSearch += (int)atomSize;
                if (inputLength != -1L && (long)bytesToSearch > inputLength) {
                    bytesToSearch = (int)inputLength;
                }
            } else {
                if (atomType == 1836019558 || atomType == 1836475768 || (long)bytesSearched + atomSize - (long)headerSize >= (long)bytesToSearch) {
                    break;
                }

                int atomDataSize = (int)(atomSize - (long)headerSize);
                bytesSearched += atomDataSize;
                if (atomType != 1718909296) {
                    if (atomDataSize != 0) {
                        input.advancePeekPosition(atomDataSize);
                    }
                } else {
                    if (atomDataSize < 8) {
                        return false;
                    }

                    buffer.reset(atomDataSize);
                    input.peekFully(buffer.getData(), 0, atomDataSize);
                    int brandsCount = atomDataSize / 4;

                    for(int i = 0; i < brandsCount; ++i) {
                        if (i == 1) {
                            buffer.skipBytes(4);
                        } else if (isCompatibleBrand(buffer.readInt())) {
                            foundGoodFileType = true;
                            break;
                        }
                    }

                    if (!foundGoodFileType) {
                        return false;
                    }
                }
            }
        }

        return foundGoodFileType;
    }

    private static boolean isCompatibleBrand(int brand) {
        if (brand >>> 8 == 3368816) {
            return true;
        } else {
            int[] var1 = COMPATIBLE_BRANDS;
            int var2 = var1.length;

            for(int var3 = 0; var3 < var2; ++var3) {
                int compatibleBrand = var1[var3];
                if (compatibleBrand == brand) {
                    return true;
                }
            }

            return false;
        }
    }
}

