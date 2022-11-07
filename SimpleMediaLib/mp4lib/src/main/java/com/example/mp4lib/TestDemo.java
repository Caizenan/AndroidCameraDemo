package com.example.mp4lib;

import android.util.Log;

import com.example.mp4lib.mp4parser.atom.Atom;
import com.example.mp4lib.mp4parser.input.core.Mp4Extractor;
import com.example.mp4lib.mp4parser.core.TrackSampleTable;
import com.example.mp4lib.mp4parser.input.DefaultExtractorInput;
import com.example.mp4lib.mp4parser.input.ExtractorInput;
import com.example.mp4lib.mp4parser.input.FileDataReader;

import java.io.IOException;

public class TestDemo {
    private FileDataReader reader;
    private ExtractorInput input;
    private Mp4Extractor extractor;
    private FileDataReader fileDataReader;
    private TrackSampleTable[] trackSampleTables;

    public void reload(String videoPath) {
        try {
            this.fileDataReader = new FileDataReader("test.mp4");
            ExtractorInput mediaSource = new DefaultExtractorInput(this.fileDataReader, 0L, (long)this.fileDataReader.getFileSize());
            Mp4Extractor extractor = new Mp4Extractor();
            if (!extractor.sniff(mediaSource)) {
                Log.d("czn", "格式错误");
                return;
            }
            Atom atom = extractor.readHeader(mediaSource);
            this.trackSampleTables = extractor.getTrackSampleTables();
            for(TrackSampleTable table:trackSampleTables){
                Log.d("czn", ""+table.timestampsUs[0]+table.timestampsUs[1]);
            }
        } catch (IOException var5) {
            var5.printStackTrace();
            Log.d("czn", "文件解析失败");
        }

    }

}
