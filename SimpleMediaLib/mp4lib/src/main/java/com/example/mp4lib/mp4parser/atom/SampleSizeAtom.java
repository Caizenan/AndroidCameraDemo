package com.example.mp4lib.mp4parser.atom;

public interface SampleSizeAtom {
    int getSampleCount();

    int getFixedSampleSize();

    StszAtom.SampleSizeItem[] getSampleSizeItems();
}
