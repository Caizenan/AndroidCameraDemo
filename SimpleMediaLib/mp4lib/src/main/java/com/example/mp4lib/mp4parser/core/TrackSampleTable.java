package com.example.mp4lib.mp4parser.core;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//
import com.example.mp4lib.mp4parser.atom.Atom;
import com.example.mp4lib.mp4parser.atom.CttsAtom;
import com.example.mp4lib.mp4parser.atom.ElstAtom;
import com.example.mp4lib.mp4parser.atom.MdhdAtom;
import com.example.mp4lib.mp4parser.atom.MvhdAtom;
import com.example.mp4lib.mp4parser.atom.SampleSizeAtom;
import com.example.mp4lib.mp4parser.atom.StblAtom;
import com.example.mp4lib.mp4parser.atom.StcoAtom;
import com.example.mp4lib.mp4parser.atom.StscAtom;
import com.example.mp4lib.mp4parser.atom.StsdAtom;
import com.example.mp4lib.mp4parser.atom.StssAtom;
import com.example.mp4lib.mp4parser.atom.StszAtom;
import com.example.mp4lib.mp4parser.atom.SttsAtom;
import com.example.mp4lib.mp4parser.atom.TrackAtom;
import com.example.mp4lib.mp4parser.util.Util;

import java.util.Iterator;
import java.util.List;

public class TrackSampleTable {
    private int trackType;
    private int movieTimeScale;
    private int timeScale;
    public int sampleCount;
    public long[] offsets;
    public long[] sizes;
    public long[] timestampsUs;
    public int[] flags;
    private static final int MAX_GAPLESS_TRIM_SIZE_SAMPLES = 4;

    public TrackSampleTable(int trackType) {
        this.trackType = trackType;
    }

    public int getTrackType() {
        return this.trackType;
    }

    void setSampleCount(int sampleCount) {
        this.sampleCount = sampleCount;
    }

    public int getSampleCount() {
        return this.sampleCount;
    }

    void setOffsets(long[] offsets) {
        this.offsets = offsets;
    }

    public long[] getOffsets() {
        return this.offsets;
    }

    void setFlags(int[] flags) {
        this.flags = flags;
    }

    public int[] getFlags() {
        return this.flags;
    }

    void setSizes(long[] sizes) {
        this.sizes = sizes;
    }

    public long[] getSizes() {
        return this.sizes;
    }

    void setTimestampsUs(long[] timestampsUs) {
        this.timestampsUs = timestampsUs;
    }

    public long[] getTimestampsUs() {
        return this.timestampsUs;
    }

    void setTimeScale(int timeScale) {
        this.timeScale = timeScale;
    }

    public int getTimeScale() {
        return this.timeScale;
    }

    void setMovieTimeScale(int movieTimeScale) {
        this.movieTimeScale = movieTimeScale;
    }

    public int getMovieTimeScale() {
        return this.movieTimeScale;
    }

    public static TrackSampleTable[] parse(Atom rootAtom) {
        try {
            TrackSampleTable[] trackSampleTables = new TrackSampleTable[2];
            parseSampleTable(trackSampleTables, rootAtom, new TrackSampleTable.AtomParserCache());
            return trackSampleTables;
        } catch (Exception var2) {
            var2.printStackTrace();
            return null;
        }
    }

    private static void parseSampleTable(TrackSampleTable[] trackSampleTables, Atom atom, TrackSampleTable.AtomParserCache atomParserCache) {
        if (atom instanceof MvhdAtom) {
            atomParserCache.movieTimeScale = ((MvhdAtom)atom).timescale;
        }

        if (atom instanceof MdhdAtom) {
            atomParserCache.timeScale = ((MdhdAtom)atom).timescale;
        }

        if (atom instanceof ElstAtom) {
            atomParserCache.elstAtom = (ElstAtom)atom;
        }

        if (atom instanceof TrackAtom) {
            atomParserCache.elstAtom = null;
        }

        if (atom instanceof StblAtom) {
            StsdAtom stsdAtom = null;
            StcoAtom stcoAtom = null;
            StscAtom stscAtom = null;
            SampleSizeAtom stszAtom = null;
            SttsAtom sttsAtom = null;
            StssAtom stssAtom = null;
            CttsAtom cttsAtom = null;
            ElstAtom elstAtom = atomParserCache.elstAtom;
            List<Atom> list = atom.getChildAtoms();
            if (list == null || list.isEmpty()) {
                return;
            }

            Iterator var12 = list.iterator();

            while(var12.hasNext()) {
                Atom sub = (Atom)var12.next();
                if (sub instanceof StsdAtom) {
                    stsdAtom = (StsdAtom)sub;
                }

                if (sub instanceof StcoAtom) {
                    stcoAtom = (StcoAtom)sub;
                }

                if (sub instanceof StscAtom) {
                    stscAtom = (StscAtom)sub;
                }

                if (sub instanceof SampleSizeAtom) {
                    stszAtom = (SampleSizeAtom)sub;
                }

                if (sub instanceof SttsAtom) {
                    sttsAtom = (SttsAtom)sub;
                }

                if (sub instanceof StssAtom) {
                    stssAtom = (StssAtom)sub;
                }

                if (sub instanceof CttsAtom) {
                    cttsAtom = (CttsAtom)sub;
                }
            }

            TrackSampleTable trackSampleTable = null;
            if (stsdAtom != null) {
                int trackType = stsdAtom.getTrackType();
                if (trackType != 1986618469 && trackType != 1936684398) {
                    return;
                }

                trackSampleTable = new TrackSampleTable(trackType);
                int pos = trackType == 1986618469 ? 0 : 1;
                trackSampleTables[pos] = trackSampleTable;
            }

            if (stsdAtom != null && stcoAtom != null && stscAtom != null && stszAtom != null) {
                parseTrackSizeAndOffset(trackSampleTable, stcoAtom, stscAtom, stszAtom);
            }

            if (stszAtom != null && stsdAtom != null && sttsAtom != null) {
                parseTimeStampAndFlags(trackSampleTable, stszAtom.getSampleCount(), sttsAtom, stssAtom, cttsAtom, elstAtom, atomParserCache.movieTimeScale, atomParserCache.timeScale);
            }
        }

        List<Atom> list = atom.getChildAtoms();
        if (list != null && !list.isEmpty()) {
            Iterator var16 = list.iterator();

            while(var16.hasNext()) {
                Atom sub = (Atom)var16.next();
                parseSampleTable(trackSampleTables, sub, atomParserCache);
            }

        }
    }

    private static void parseTrackSizeAndOffset(TrackSampleTable trackSampleTable, StcoAtom stcoAtom, StscAtom stscAtom, SampleSizeAtom stszAtom) {
        StscAtom.SampleToChunkItem[] sampleToChunkItems = stscAtom.getSampleToChunkItems();
        StcoAtom.ChunkOffsetItem[] chunkOffsetItems = stcoAtom.getChunkOffsetItems();
        StszAtom.SampleSizeItem[] sampleSizeItems = stszAtom.getSampleSizeItems();
        int chunkCount = chunkOffsetItems.length;
        int sampleCount = stszAtom.getSampleCount();
        long[] offset = new long[sampleCount];
        long[] size = new long[sampleCount];
        int index = 0;
        int fixedSampleSize = stszAtom.getFixedSampleSize();

        for(int i = 0; i < sampleToChunkItems.length; ++i) {
            int nextChunkId = i < sampleToChunkItems.length - 1 ? sampleToChunkItems[i + 1].firstChunk : chunkCount + 1;
            int curChunkId = sampleToChunkItems[i].firstChunk;
            int samplePerChunk = sampleToChunkItems[i].samplesPerChunk;

            for(int j = curChunkId; j < nextChunkId; ++j) {
                long chunkOffset = chunkOffsetItems[j - 1].chunkOffset;

                for(int k = 0; k < samplePerChunk; ++k) {
                    offset[index] = chunkOffset;
                    size[index] = fixedSampleSize != 0 ? (long)fixedSampleSize : (long)sampleSizeItems[index].entrySize;
                    chunkOffset += size[index];
                    ++index;
                }
            }
        }

        trackSampleTable.setSampleCount(sampleCount);
        trackSampleTable.setOffsets(offset);
        trackSampleTable.setSizes(size);
    }

    private static void parseTimeStampAndFlags(TrackSampleTable trackSampleTable, int sampleCount, SttsAtom sttsAtom, StssAtom stssAtom, CttsAtom cttsAtom, ElstAtom elstAtom, int movieTimeScale, int timeScale) {
        long[] timeStamps = new long[sampleCount];//pts时间戳队列
        int[] isKeyFrame = new int[sampleCount];//关键帧队列
        CttsAtom.CompositionOffsetItem[] compositionOffsetItems = cttsAtom == null ? null : cttsAtom.getTimeToSampleItems();//偏移量队列
        StssAtom.SyncSampleBox[] sampleBoxes = stssAtom != null ? stssAtom.getSyncSampleBoxes() : null;
        SttsAtom.TimeToSampleItem[] timeToSampleItems = sttsAtom.getTimeToSampleItems();//dts队列
        ElstAtom.EditListTableItem[] editListTableItems = elstAtom != null ? elstAtom.editListTableItems : null;
        long timestampTimeUnits = 0L;
        long timestampOffset = 0L;
        int cttsPos = 0;
        int stssPos = 0;
        int sttsPos = 0;
        int remainingSamplesAtTimestampOffset = 0;
        int remainingSamplesAtTimestampDelta = 0;
        int timestampDeltaInTimeUnits = 0;
        //计算出每个sample的offset和dts，然后标记出keyframe
        for(int i = 0; i < sampleCount; ++i) {
            if (cttsAtom != null) {
                if (cttsPos < compositionOffsetItems.length && remainingSamplesAtTimestampOffset == 0) {
                    remainingSamplesAtTimestampOffset = compositionOffsetItems[cttsPos].sampleCount;
                    timestampOffset = (long)compositionOffsetItems[cttsPos].sampleOffset;
                    ++cttsPos;
                }
                --remainingSamplesAtTimestampOffset;
            }

            if (sttsPos < timeToSampleItems.length && remainingSamplesAtTimestampDelta == 0) {
                remainingSamplesAtTimestampDelta = timeToSampleItems[sttsPos].sampleCount;
                timestampDeltaInTimeUnits = timeToSampleItems[sttsPos].sampleDelta;
            }

            --remainingSamplesAtTimestampDelta;
            timeStamps[i] = timestampTimeUnits + timestampOffset;
            //没有stss的视频全是关键帧，音频本来就没有stss概念。
            isKeyFrame[i] = stssAtom == null ? 1 : 0;
            if (sampleBoxes != null && stssPos < sampleBoxes.length && i + 1 == sampleBoxes[stssPos].sampleNumber) {
                ++stssPos;
                isKeyFrame[i] = 1;
            }

            timestampTimeUnits += (long)timestampDeltaInTimeUnits;
        }

        boolean timestampTransformed = false;
        long duration = timestampTimeUnits + timestampOffset;
        //这个mp4不存在elst，track没有时间偏移
        if (editListTableItems == null) {
            Util.scaleLargeTimestampsInPlace(timeStamps, 1000000L, (long)timeScale);
            timestampTransformed = true;
        }

        //mediaTime = 800;
        //timestampTransformed 是否要发生偏移
        //计算音频偏移
        long editStartTime;
        if (!timestampTransformed && editListTableItems.length == 1 && trackSampleTable.trackType == 1936684398) {
            editStartTime = editListTableItems[0].mediaTime;
            long editEndTime = editStartTime + Util.scaleLargeTimestamp(editListTableItems[0].segmentDuration, (long)timeScale, (long)movieTimeScale);
            if (canApplyEditWithGaplessInfo(timeStamps, duration, editStartTime, editEndTime)) {
                Util.scaleLargeTimestampsInPlace(timeStamps, 1000000L, (long)timeScale);
                timestampTransformed = true;
            }
        }

        //r如果需要偏移，但是需要偏移的这段时间等于0，则所有的sample都要偏移
        int endIndex;
        if (!timestampTransformed && editListTableItems.length == 1 && editListTableItems[0].segmentDuration == 0L) {
            editStartTime = editListTableItems[0].mediaTime;

            for(endIndex = 0; endIndex < timeStamps.length; ++endIndex) {
                timeStamps[endIndex] = Util.scaleLargeTimestamp(timeStamps[endIndex] - editStartTime, 1000000L, (long)timeScale);
            }

            timestampTransformed = true;
        }

        //如果需要偏移，但是duration不等于0，从pts队列里找到开始时间的索引，停止时间的索引，然后这个范围内的sample进行偏移。
        if (!timestampTransformed) {
            //对于音频来说，endindex是开集。
            boolean omitClippedSample = trackSampleTable.getTrackType() == 1936684398;
            long pts = 0L;
            int sampleIndex = 0;

            for(int i = 0; i < editListTableItems.length; ++i) {
                //查找duration的开始时间，结束时间
                //开始时间
                long editMediaTime = editListTableItems[i].mediaTime;
                if (editMediaTime != -1L) {
                    //这里乘timeScale是为了转成以timeScale为单位
                    long editDuration = Util.scaleLargeTimestamp(editListTableItems[i].segmentDuration, (long)timeScale, (long)movieTimeScale);
                    int startIndex = Util.binarySearchFloor(timeStamps, editMediaTime, true, true);

                    //计算出endIndex，并且startIndex是关键帧
                    for(endIndex = Util.binarySearchCeil(timeStamps, editMediaTime + editDuration, omitClippedSample, false); startIndex < endIndex && isKeyFrame[startIndex] == 0; ++startIndex) {
                    }

                    //计算pts
                    for(int j = startIndex; j < endIndex; ++j) {
                        long ptsUs = Util.scaleLargeTimestamp(pts, 1000000L, (long)movieTimeScale);
                        long timeInSegmentUs = Util.scaleLargeTimestamp(Math.max(0L, timeStamps[j] - editMediaTime), 1000000L, (long)timeScale);
                        timeStamps[sampleIndex] = ptsUs + timeInSegmentUs;
                        ++sampleIndex;
                    }
                }
            }
        }

        trackSampleTable.setMovieTimeScale(movieTimeScale);
        trackSampleTable.setTimeScale(timeScale);
        trackSampleTable.setTimestampsUs(timeStamps);
        trackSampleTable.setFlags(isKeyFrame);
    }

    private static boolean canApplyEditWithGaplessInfo(long[] timestamps, long duration, long editStartTime, long editEndTime) {
        int lastIndex = timestamps.length - 1;
        int latestDelayIndex = Util.constrainValue(4, 0, lastIndex);
        int earliestPaddingIndex = Util.constrainValue(timestamps.length - 4, 0, lastIndex);
        return timestamps[0] <= editStartTime && editStartTime < timestamps[latestDelayIndex] && timestamps[earliestPaddingIndex] < editEndTime && editEndTime <= duration;
    }

    public String formatSampleInfo(int index) {
        return "offset = " + this.offsets[index] + "\nsize = " + this.sizes[index] + "\ntimestamp = " + this.timestampsUs[index] + "us\nflag = " + (this.flags[index] == 1 ? "key Frame" : 0);
    }

    private static class AtomParserCache {
        int timeScale;
        int movieTimeScale;
        ElstAtom elstAtom;

        private AtomParserCache() {
        }
    }
}

