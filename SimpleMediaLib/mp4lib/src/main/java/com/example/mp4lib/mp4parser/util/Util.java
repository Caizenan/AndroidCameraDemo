package com.example.mp4lib.mp4parser.util;

import java.nio.charset.Charset;
import java.util.Arrays;

public class Util {
    public static final Charset CHARSET_UTF8 = Charset.forName("UTF-8");
    public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];

    public Util() {
    }

    public static String fromUtf8Bytes(byte[] bytes) {
        return new String(bytes, CHARSET_UTF8);
    }

    public static String fromUtf8Bytes(byte[] bytes, int offset, int length) {
        return new String(bytes, offset, length, CHARSET_UTF8);
    }

    public static boolean isLinebreak(int c) {
        return c == 10 || c == 13;
    }

    public static int constrainValue(int value, int min, int max) {
        return Math.max(min, Math.min(value, max));
    }

    public static float intToDecimalPart(int x) {
        float ans;
        for(ans = (float)x; ans >= 1.0F; ans /= 10.0F) {
        }

        return ans;
    }

    public static byte[] intToByteArray(int a) {
        return new byte[]{(byte)(a >> 24 & 255), (byte)(a >> 16 & 255), (byte)(a >> 8 & 255), (byte)(a & 255)};
    }

    public static long scaleLargeTimestamp(long timestamp, long multiplier, long divisor) {
        long multiplicationFactor;
        if (divisor >= multiplier && divisor % multiplier == 0L) {
            multiplicationFactor = divisor / multiplier;
            return timestamp / multiplicationFactor;
        } else if (divisor < multiplier && multiplier % divisor == 0L) {
            multiplicationFactor = multiplier / divisor;
            return timestamp * multiplicationFactor;
        } else {
            double multiplicationFactors = (double)multiplier / (double)divisor;
            return (long)((double)timestamp * multiplicationFactors);
        }
    }

    public static void scaleLargeTimestampsInPlace(long[] timestamps, long multiplier, long divisor) {
        int i;
        long multiplicationFactor;
        if (divisor >= multiplier && divisor % multiplier == 0L) {
            multiplicationFactor = divisor / multiplier;

            for(i = 0; i < timestamps.length; ++i) {
                timestamps[i] /= multiplicationFactor;
            }
        } else if (divisor < multiplier && multiplier % divisor == 0L) {
            multiplicationFactor = multiplier / divisor;

            for(i = 0; i < timestamps.length; ++i) {
                timestamps[i] *= multiplicationFactor;
            }
        } else {
            double multiplicationFactors = (double)multiplier / (double)divisor;
            for(i = 0; i < timestamps.length; ++i) {
                timestamps[i] = (long)((double)timestamps[i] * multiplicationFactors);
            }
        }

    }

    //????????????val,??????val?????????????????????????????????????????????????????????val?????????????????????
    //??????val?????????????????????????????????-1???
    public static int binarySearchFloor(long[] array, long value, boolean inclusive, boolean stayInBounds) {
        int index = Arrays.binarySearch(array, value);
        if (index < 0) {
            index = -(index + 2);
        } else {
            while(true) {
                --index;
                if (index < 0 || array[index] != value) {
                    if (inclusive) {
                        ++index;
                    }
                    break;
                }
            }
        }

        return stayInBounds ? Math.max(0, index) : index;
    }


    //????????????val,??????val?????????????????????(?????????)?????????????????????val?????????????????????
    //??????val??????????????????????????????????????????n
    public static int binarySearchCeil(long[] array, long value, boolean inclusive, boolean stayInBounds) {
        int index = Arrays.binarySearch(array, value);
        if (index < 0) {
            index = ~index;
        } else {
            while(true) {
                ++index;
                if (index >= array.length || array[index] != value) {
                    if (inclusive) {
                        --index;
                    }
                    break;
                }
            }
        }

        return stayInBounds ? Math.min(array.length - 1, index) : index;
    }
}
