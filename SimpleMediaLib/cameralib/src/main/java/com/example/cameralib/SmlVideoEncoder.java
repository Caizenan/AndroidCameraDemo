package com.example.cameralib;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.LinkedBlockingDeque;

public class SmlVideoEncoder implements ISmlCameraObserver{

    private static final String VIDEO_MIME_TYPE = "video/avc";
    private static final int I_FRAME_INTERVAL = 120;
    private MediaCodec encoder;
    private long startTime;
    private boolean videoEncoderLoop;
    private WorkThread worker;
    private LinkedBlockingDeque<byte[]> frameDataLinkedBlockingQueue;
    private LinkedBlockingDeque<Message> inputIndexQueue;
    private MediaCodec.Callback codecCallback;
    private boolean hasPrepare = false;

    public SmlVideoEncoder(){
        frameDataLinkedBlockingQueue = new LinkedBlockingDeque<>();
        inputIndexQueue = new LinkedBlockingDeque<>();
        initCallback();
    }

    private void initCallback(){
        codecCallback = new MediaCodec.Callback() {
            @Override
            public void onInputBufferAvailable(@NonNull MediaCodec codec, int index) {
                Message msg = new Message();
                msg.obj = codec;
                msg.arg1 = index;
                inputIndexQueue.offer(msg);
            }

            @Override
            public void onOutputBufferAvailable(@NonNull MediaCodec codec, int index, @NonNull MediaCodec.BufferInfo info) {
                if(!videoEncoderLoop){
                    return;
                }
                //https://blog.csdn.net/wq892373445/article/details/118334871
                ByteBuffer buffer = codec.getOutputBuffer(index);
                if (VIDEO_MIME_TYPE.equals(MediaFormat.MIMETYPE_VIDEO_AVC)){
                    int typeH264 = buffer.get(4) & 0x1F;//获取缓冲区类型
                    if(typeH264 == 7||typeH264 == 8){
                        byte[] configFps = new byte[info.size];
                        buffer.get(configFps);
                        searchSPSandPPSFromH264(ByteBuffer.wrap(configFps),info);
                    }
                }else if(VIDEO_MIME_TYPE.equals(MediaFormat.MIMETYPE_VIDEO_HEVC)){
                    int typeH265 = (buffer.get(4) & 0x7e) >> 1;
                    Log.d("czn", "chris H265type= " + typeH265);
                    if (typeH265 == 32 || typeH265 == 33 || typeH265 == 34) { //32代表VPS帧   33代表SPS帧，34代表PPS帧(只有第一帧会出现此类帧（目前发现只有32），其余都是1或者19)
                        byte[] configFps = new byte[info.size];
                        buffer.get(configFps);
                        Log.d("czn", "chris H265type= " + typeH265 +",value = "+bytesToHex(configFps));
                        //H265type= 32, value = 0000000140010C01FFFF016000000300B0000003000003005DAC5900000001420101016000000300B0000003000003005DA00280802E1F1396BB9324BB948281010176850940000000014401C0F1800420

                        searchVpsSpsPpsFromH265(ByteBuffer.wrap(configFps));
                    }
                }
                codec.releaseOutputBuffer(index, false);
            }

            @Override
            public void onError(@NonNull MediaCodec codec, @NonNull MediaCodec.CodecException e) {

            }

            @Override
            public void onOutputFormatChanged(@NonNull MediaCodec codec, @NonNull MediaFormat format) {
                if (VIDEO_MIME_TYPE == MediaFormat.MIMETYPE_VIDEO_HEVC) {
                    searchVpsSpsPpsFromH265(format.getByteBuffer("csd-0"));
                } else if (VIDEO_MIME_TYPE == MediaFormat.MIMETYPE_VIDEO_AVC) {
                    ByteBuffer sps = format.getByteBuffer("csd-0"); //000000016764001FACB402802DD2905020206D0A1350
                    ByteBuffer pps = format.getByteBuffer("csd-1"); //0000000168EE06E2C0
                    Log.d("czn","H264 onOutputFormatChanged sps="+bytesToHex(sps.array()) + ",pps=" + bytesToHex(pps.array()));
                    //onOutputFormatChanged sps=000000016764001FACB402802DD2905020206D0A1350,pps=0000000168EE06E2C0
                }
            }
        };
    }

    public void prepareEncoder(int height, int width, int fps) {
        MediaFormat videoFormat = MediaFormat.createVideoFormat(VIDEO_MIME_TYPE, height, width);
        videoFormat.setInteger(MediaFormat.KEY_FRAME_RATE, fps);
        videoFormat.setInteger(MediaFormat.KEY_BIT_RATE, (int) width * height);
        videoFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, selectColorFormat(VIDEO_MIME_TYPE));
        videoFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, I_FRAME_INTERVAL);
        if (VIDEO_MIME_TYPE.equals(MediaFormat.MIMETYPE_VIDEO_AVC)) {
            videoFormat.setInteger(MediaFormat.KEY_BITRATE_MODE, MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_VBR);
            videoFormat.setInteger(MediaFormat.KEY_PROFILE, MediaCodecInfo.CodecProfileLevel.AVCProfileHigh);
            videoFormat.setInteger(MediaFormat.KEY_LEVEL, MediaCodecInfo.CodecProfileLevel.AVCLevel31);
        } else {
            videoFormat.setInteger(MediaFormat.KEY_BITRATE_MODE, MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_VBR);
            videoFormat.setInteger(MediaFormat.KEY_PROFILE, MediaCodecInfo.CodecProfileLevel.HEVCProfileMain);
            videoFormat.setInteger(MediaFormat.KEY_LEVEL, MediaCodecInfo.CodecProfileLevel.HEVCHighTierLevel4);
        }
        try {
            encoder = MediaCodec.createEncoderByType(VIDEO_MIME_TYPE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        encoder.setCallback(codecCallback);
        encoder.configure(videoFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
    }

    public void startEncodec() {
        encoder.start();
        startTime = System.currentTimeMillis() * 1000;
        videoEncoderLoop = true;
        worker = new WorkThread();
        worker.start();
    }

    private int selectColorFormat(String mimeType) {
        MediaCodecInfo codecInfo = selectCodec(mimeType);
        MediaCodecInfo.CodecCapabilities capabilities = codecInfo.getCapabilitiesForType(mimeType);
        for (int i = 0; i < capabilities.colorFormats.length; i++) {
            int colorFormat = capabilities.colorFormats[i];
            if (isRecognizedFormat(colorFormat)) {
                return colorFormat;
            }
        }
        return 0;
    }

    private boolean isRecognizedFormat(int colorFormat) {
        switch (colorFormat) {
            case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar:
            case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedPlanar:
            case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar:
            case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedSemiPlanar:
            case MediaCodecInfo.CodecCapabilities.COLOR_TI_FormatYUV420PackedSemiPlanar:
                return true;
            default:
                return false;
        }
    }

    private MediaCodecInfo selectCodec(String mimeType) {
        int numCodecs = MediaCodecList.getCodecCount();
        for (int i = 0; i < numCodecs; i++) {
            MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);
            if (!codecInfo.isEncoder()) {
                continue;
            }
            String[] types = codecInfo.getSupportedTypes();
            for (int j = 0; j < types.length; j++) {
                if (types[j].equalsIgnoreCase(mimeType)) {
                    return codecInfo;
                }
            }
        }
        return null;
    }

    @Override
    public void onObserved(byte[] data, int width, int height) {
        try{
            frameDataLinkedBlockingQueue.put(data);
            if(!hasPrepare){
                prepareEncoder(width,height,30);
                hasPrepare = true;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private class WorkThread extends Thread {
        @Override
        public void run() {
            while (videoEncoderLoop && !Thread.interrupted()) {
                int inputBufferIndex = 0;
                try {
                    inputBufferIndex = inputIndexQueue.take().arg1;
                    if (inputBufferIndex >= 0) {
                        byte[] nv21Data = frameDataLinkedBlockingQueue.take();
                        ByteBuffer inputBuffer = encoder.getInputBuffer(inputBufferIndex);
                        inputBuffer.put(nv21Data);
                        long pts = System.currentTimeMillis() * 1000 - startTime;
                        encoder.queueInputBuffer(inputBufferIndex, 0, nv21Data.length, pts, 0);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            inputIndexQueue.clear();
            frameDataLinkedBlockingQueue.clear();
        }
    }

    //查找sps pps vps
    public void searchSPSandPPSFromH264(ByteBuffer buffer, MediaCodec.BufferInfo bufferInfo){

        byte[] csd = new byte[128];
        int len = 0, p = 4, q = 4;

        len = bufferInfo.size;
        Log.d("czn","len="+len);
        if (len<128) {
            buffer.get(csd,0,len);
            if (len>0 && csd[0]==0 && csd[1]==0 && csd[2]==0 && csd[3]==1) {
                // Parses the SPS and PPS, they could be in two different packets and in a different order
                //depending on the phone so we don't make any assumption about that
                while (p<len) {
                    while (!(csd[p+0]==0 && csd[p+1]==0 && csd[p+2]==0 && csd[p+3]==1) && p+3<len) p++;
                    if (p+3>=len) p=len;
                    if ((csd[q]&0x1F)==7) {
                        byte[] sps = new byte[p-q];
                        System.arraycopy(csd, q, sps, 0, p-q);
                        Log.d("czn","chris, searchSPSandPPSFromH264 SPS="+bytesToHex(sps));
                        //chris, searchSPSandPPSFromH264 SPS=6764001FACB402802DD2905020206D0A1350
                    } else {
                        byte[] pps = new byte[p-q];
                        System.arraycopy(csd, q, pps, 0, p-q);
                        Log.d("czn","chris, searchSPSandPPSFromH264 PPS="+bytesToHex(pps));
                        //chris, searchSPSandPPSFromH264 PPS=68EE06E2C0
                    }
                    p += 4;
                    q = p;
                }
            }
        }
    }

    public void searchVpsSpsPpsFromH265(ByteBuffer csd0byteBuffer) {
        int vpsPosition = -1;
        int spsPosition = -1;
        int ppsPosition = -1;
        int contBufferInitiation = 0;
        byte[] csdArray = csd0byteBuffer.array();
        for (int i = 0; i < csdArray.length; i++) {
            if (contBufferInitiation == 3 && csdArray[i] == 1) {
                if (vpsPosition == -1) {
                    vpsPosition = i - 3;
                } else if (spsPosition == -1) {
                    spsPosition = i - 3;
                } else {
                    ppsPosition = i - 3;
                }
            }
            if (csdArray[i] == 0) {
                contBufferInitiation++;
            } else {
                contBufferInitiation = 0;
            }
        }
        byte[] vps = new byte[spsPosition];
        byte[] sps = new byte[ppsPosition - spsPosition];
        byte[] pps = new byte[csdArray.length - ppsPosition];
        for (int i = 0; i < csdArray.length; i++) {
            if (i < spsPosition) {
                vps[i] = csdArray[i];
            } else if (i < ppsPosition) {
                sps[i - spsPosition] = csdArray[i];
            } else {
                pps[i - ppsPosition] = csdArray[i];
            }
        }

        Log.d("czn", "searchVpsSpsPpsFromH265: vps="+ bytesToHex(vps)+",sps="+bytesToHex(sps)+",pps="+bytesToHex(pps));
        //vps=0000000140010C01FFFF016000000300B0000003000003005DAC59,sps=00000001420101016000000300B0000003000003005DA00280802E1F1396BB9324BB948281010176850940,pps=000000014401C0F1800420
    }

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}