package org.black.jtranscribe.dsp.common;/*
*	AudioCommon.java
*
*	This file is part of jsresources.org
*/

/*
 * Copyright (c) 1999 - 2001 by Matthias Pfisterer
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/*
|<---            this code is formatted to fit into 80 columns             --->|
*/

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;


/**
 * Common methods for audio examples.
 */
public class AudioCommon {
    private static boolean DEBUG = false;
    private static final Logger log = LoggerFactory.getLogger(AudioCommon.class);
    public static final int BYTE_PER_FLOAT = 2;


    public static void setDebug(boolean bDebug) {
        DEBUG = bDebug;
    }


    /**
     * TODO:
     */
    public static void listSupportedTargetTypes() {
        String strMessage = "Supported target types:";
        AudioFileFormat.Type[] aTypes = AudioSystem.getAudioFileTypes();
        for (int i = 0; i < aTypes.length; i++) {
            strMessage += " " + aTypes[i].getExtension();
        }
        out(strMessage);
    }


    /**
     * Trying to get an audio file type for the passed extension.
     * This works by examining all available file types. For each
     * type, if the extension this type promisses to handle matches
     * the extension we are trying to find a type for, this type is
     * returned.
     * If no appropriate type is found, null is returned.
     */
    public static AudioFileFormat.Type findTargetType(String strExtension) {
        AudioFileFormat.Type[] aTypes = AudioSystem.getAudioFileTypes();
        for (int i = 0; i < aTypes.length; i++) {
            if (aTypes[i].getExtension().equals(strExtension)) {
                return aTypes[i];
            }
        }
        return null;
    }


    /**
     * TODO:
     */
    public static void listMixersAndExit() {
        out("Available Mixers:");
        Mixer.Info[] aInfos = AudioSystem.getMixerInfo();
        for (int i = 0; i < aInfos.length; i++) {
            out(aInfos[i].getName());
        }
        if (aInfos.length == 0) {
            out("[No mixers available]");
        }
        System.exit(0);
    }


    /**
     * List Mixers.
     * Only Mixers that support either TargetDataLines or SourceDataLines
     * are listed, depending on the value of bPlayback.
     */
    public static void listMixersAndExit(boolean bPlayback) {
        out("Available Mixers:");
        Mixer.Info[] aInfos = AudioSystem.getMixerInfo();
        for (int i = 0; i < aInfos.length; i++) {
            Mixer mixer = AudioSystem.getMixer(aInfos[i]);
            Line.Info lineInfo = new Line.Info(bPlayback ?
                    SourceDataLine.class :
                    TargetDataLine.class);
            if (mixer.isLineSupported(lineInfo)) {
                out(aInfos[i].getName());
            }
        }
        if (aInfos.length == 0) {
            out("[No mixers available]");
        }
        System.exit(0);
    }


    /**
     * TODO:
     * This method tries to return a Mixer.Info whose name
     * matches the passed name. If no matching Mixer.Info is
     * found, null is returned.
     */
    public static Mixer.Info getMixerInfo(String strMixerName) {
        Mixer.Info[] aInfos = AudioSystem.getMixerInfo();
        for (int i = 0; i < aInfos.length; i++) {
            if (aInfos[i].getName().equals(strMixerName)) {
                return aInfos[i];
            }
        }
        return null;
    }


    /**
     * TODO:
     */
    public static TargetDataLine getTargetDataLine(String strMixerName,
                                                   AudioFormat audioFormat,
                                                   int nBufferSize) {
        /*
              Asking for a line is a rather tricky thing.
              We have to construct an Info object that specifies
              the desired properties for the line.
              First, we have to say which kind of line we want. The
              possibilities are: SourceDataLine (for playback), Clip
              (for repeated playback)	and TargetDataLine (for
               recording).
              Here, we want to do normal capture, so we ask for
              a TargetDataLine.
              Then, we have to pass an AudioFormat object, so that
              the Line knows which format the data passed to it
              will have.
              Furthermore, we can give Java Sound a hint about how
              big the internal buffer for the line should be. This
              isn't used here, signaling that we
              don't care about the exact size. Java Sound will use
              some default value for the buffer size.
          */
        TargetDataLine targetDataLine = null;
        DataLine.Info info = new DataLine.Info(TargetDataLine.class,
                audioFormat, nBufferSize);
        try {
            if (strMixerName != null) {
                Mixer.Info mixerInfo = getMixerInfo(strMixerName);
                if (mixerInfo == null) {
                    out("AudioCommon.getTargetDataLine(): mixer not found: " + strMixerName);
                    return null;
                }
                Mixer mixer = AudioSystem.getMixer(mixerInfo);
                targetDataLine = (TargetDataLine) mixer.getLine(info);
            } else {
                if (DEBUG) {
                    out("AudioCommon.getTargetDataLine(): using default mixer");
                }
                targetDataLine = (TargetDataLine) AudioSystem.getLine(info);
            }

            /*
                *	The line is there, but it is not yet ready to
                *	receive audio data. We have to open the line.
                */
            if (DEBUG) {
                out("AudioCommon.getTargetDataLine(): opening line...");
            }
            targetDataLine.open(audioFormat, nBufferSize);
            if (DEBUG) {
                out("AudioCommon.getTargetDataLine(): opened line");
            }
        } catch (LineUnavailableException e) {
            if (DEBUG) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            if (DEBUG) {
                e.printStackTrace();
            }
        }
        if (DEBUG) {
            out("AudioCommon.getTargetDataLine(): returning line: " + targetDataLine);
        }
        return targetDataLine;
    }


    /**
     * Checks if the encoding is PCM.
     */
    public static boolean isPcm(AudioFormat.Encoding encoding) {
        return encoding.equals(AudioFormat.Encoding.PCM_SIGNED)
                || encoding.equals(AudioFormat.Encoding.PCM_UNSIGNED);
    }


    /**
     * TODO:
     */
    private static void out(String strMessage) {
        System.out.println(strMessage);
    }

    // TODO: maybe can used by others. AudioLoop?
    // In this case, move to AudioCommon.
    public static SourceDataLine getSourceDataLine(String strMixerName,
                                                   AudioFormat audioFormat,
                                                   int nBufferSize) {
        /*
           *	Asking for a line is a rather tricky thing.
           *	We have to construct an Info object that specifies
           *	the desired properties for the line.
           *	First, we have to say which kind of line we want. The
           *	possibilities are: SourceDataLine (for playback), Clip
           *	(for repeated playback)	and TargetDataLine (for
           *	 recording).
           *	Here, we want to do normal playback, so we ask for
           *	a SourceDataLine.
           *	Then, we have to pass an AudioFormat object, so that
           *	the Line knows which format the data passed to it
           *	will have.
           *	Furthermore, we can give Java Sound a hint about how
           *	big the internal buffer for the line should be. This
           *	isn't used here, signaling that we
           *	don't care about the exact size. Java Sound will use
           *	some default value for the buffer size.
           */
        SourceDataLine line = null;
        DataLine.Info info = new DataLine.Info(SourceDataLine.class,
                audioFormat, nBufferSize);
        try {
            if (strMixerName != null) {
                Mixer.Info mixerInfo = AudioCommon.getMixerInfo(strMixerName);
                if (mixerInfo == null) {
                    out("AudioPlayer: mixer not found: " + strMixerName);
                    System.exit(1);
                }
                Mixer mixer = AudioSystem.getMixer(mixerInfo);
                line = (SourceDataLine) mixer.getLine(info);
            } else {
                line = (SourceDataLine) AudioSystem.getLine(info);
            }

            /*
                *	The line is there, but it is not yet ready to
                *	receive audio data. We have to open the line.
                */
            line.open(audioFormat, nBufferSize);
        } catch (LineUnavailableException e) {
            if (DEBUG) e.printStackTrace();
        } catch (Exception e) {
            if (DEBUG) e.printStackTrace();
        }
        return line;
    }

    private static ByteOrder getbyteorder(boolean bigEndian) {
        if (bigEndian) {
            return (ByteOrder.BIG_ENDIAN);
        } else {
            return (ByteOrder.LITTLE_ENDIAN);
        }
    }


//    public static float[][] bytesToFloats(byte[] allSampleBytes, AudioFormat format) {
//
//
//        float[][] floats = new float[0][0];
//        int bytePerFrame;
//        int bytePerSample;
//        int channels;
//        int frames;
//
//        ByteBuffer totalBuffer = ByteBuffer.wrap(allSampleBytes);
//        order(totalBuffer, format);
//
//
//        bytePerFrame = format.getFrameSize();
//        bytePerSample = format.getSampleSizeInBits() / 8;
//        channels = format.getChannels();
//
//
//        frames = allSampleBytes.length / bytePerFrame;
//        floats = new float[channels][frames];
//
//
//        log.debug("Input stream format : {}", format.toString());
//
//        for (int s = 0; s < frames; s++) {
//
//            for (int c = 0; c < channels; c++) {
//                byte[] sampleBytes = new byte[bytePerSample];
//                ByteBuffer sampleBuffer;
//                totalBuffer.get(sampleBytes, 0, bytePerSample);
//                sampleBuffer = ByteBuffer.wrap(sampleBytes);
//                order(sampleBuffer, format);
//                floats[c][s] = sampleBuffer.getShort();
////                log.debug("Read float : {}", floats[c][s]);
//            }
//
//
//        }
//
//
//        return floats;
//    }

    public static float[][] bytesToFloats2(byte[] allSampleBytes, AudioFormat format) {


        float[][] floats;
        float[] interleavedFloats;
        int floatCOunter = 0;

        interleavedFloats = new float[allSampleBytes.length / 2];
        floats = new float[allSampleBytes.length / 2 / format.getChannels()][format.getChannels()];

        for (int offset = 0; offset < allSampleBytes.length - 1; offset = offset + 2) {
            float sample =
                    ((allSampleBytes[offset + 0] & 0xFF)
                            | (allSampleBytes[offset + 1] << 8))
                            / 32768.0F;
            interleavedFloats[floatCOunter++] = sample;


        }

        floats = deinterleave(interleavedFloats, format.getChannels());
        return floats;
    }

    public static float[][] bytesToFloats(byte[] allSampleBytes, AudioFormat format) {


        float[] interleavedFloats;
        int floatCounter = 0;

        ByteBuffer byteBuffer = ByteBuffer.wrap(allSampleBytes);
        byteBuffer.order(getbyteorder(format.isBigEndian()));
        interleavedFloats = new float[byteBuffer.capacity()];

        while (byteBuffer.hasRemaining()) {
            interleavedFloats[floatCounter] = byteBuffer.getFloat();
        }

        return deinterleave(interleavedFloats, format.getChannels());

    }


    public static byte[] floatsToBytes2(float[][] floats, AudioFormat format) {

        ByteBuffer byteBuffer;
        int channels;

        channels = format.getChannels();

        byteBuffer = ByteBuffer.allocate(floats[0].length);
        byteBuffer.order(getbyteorder(format.isBigEndian()));

        for (int channel = 0; channel < channels; channel++) {
            for (int sample = 0; sample < floats.length; sample++) {
                byteBuffer.putFloat(floats[channel][sample]);

            }

        }


        return byteBuffer.array();


    }

    /**
     * Deinterleaves an interleaved array.
     *
     * @param source the interleaved array.
     * @return the de-interleaved array (channels x frames).
     */
    private static float[][] deinterleave(float[] source, int channels) {
        int nFrames = source.length / channels;
        float[][] result = new float[channels][nFrames];
        for (int i = 0, count = 0; i < nFrames; i++) {
            for (int j = 0; j < channels; j++) {
                result[j][i] = source[count++];
            }
        }
        return result;
    }

    public static byte[] floatsToBytes(float[][] floats, AudioFormat format) {

        ByteBuffer byteBuffer = ByteBuffer.allocate(floats.length * floats[0].length * 2);
        byteBuffer.order(getbyteorder(format.isBigEndian()));

        for (int sample = 0; sample < floats.length; sample++) {

            for (int channel = 0; channel < format.getChannels(); channel++) {

                byteBuffer.putFloat(floats[channel][sample]);

            }

        }
        return byteBuffer.array();
    }


    public static byte[] getBytes(float[] samples, AudioFormat format) {

        ByteBuffer byteBuffer;
        byteBuffer = ByteBuffer.allocate(samples.length * BYTE_PER_FLOAT);
        byteBuffer.order(getbyteorder(format.isBigEndian()));


        for (float sample : samples) {
            byteBuffer.putShort((short) sample);
        }
        byteBuffer.rewind();
        return byteBuffer.array();
    }

    // For now only Stream format : PCM_SIGNED 44100.0 Hz, 16 bit, stereo, 4 bytes/frame, little-endian
    public static float[] getFloats(byte[] bytes, AudioFormat format) {

        float[] samples;
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        byteBuffer.order(getbyteorder(format.isBigEndian()));

        samples = new float[bytes.length / BYTE_PER_FLOAT];

        for (int index = 0; index < samples.length; index++) {

            // TODO : read short (2 bytes) - must manage other sample size :  4 bytes ...
            samples[index] = byteBuffer.getShort();
        }
        return samples;
    }

    public static byte[][] deinterleave(byte[] source, int channels) {
        int nSample;
        byte[][] target;

        nSample = source.length / channels;
        target = new byte[channels][nSample];

        for (int channel = 0; channel < channels; channel++) {
            for (int sample = 0, channelOffset = 0; sample < nSample; sample++, channelOffset += channels) {
                target[channel][sample] = source[channel + channelOffset];
            }
        }
        return target;
    }

    public static byte[] interleave(byte[][] source) {

        byte[] target;
        int position = 0;

        target = new byte[source.length * source[0].length];

        for (int sample = 0; sample < source[0].length; sample++) {
            for (int channel = 0; channel < source.length; channel++) {
                target[position++] = source[channel][sample];
            }

        }

        return target;
    }

    /**
     * public static float[][] convertBeforeFx(byte[] bytes, AudioFormat format) {
     * <p>
     * byte[][] byteChannels;
     * float[][] floatChannels;
     * <p>
     * byteChannels = deinterleave(bytes, format.getChannels());
     * floatChannels = new float[byteChannels.length][byteChannels[0].length / (format.getSampleSizeInBits() / 8)];
     * <p>
     * for (int channel = 0; channel < format.getChannels(); channel++) {
     * floatChannels[channel] = getFloats(byteChannels[channel], format);
     * }
     * <p>
     * return floatChannels;
     * <p>
     * }
     * <p>
     * public static byte[] convertAfterFx(float[][] floatChannels, AudioFormat format, int actualRetrieved) {
     * <p>
     * byte[][] byteChannels;
     * byte[] interleavedChannels;
     * byteChannels = new byte[floatChannels.length][actualRetrieved * (format.getSampleSizeInBits() / 8)];
     * <p>
     * for (int channel = 0; channel < floatChannels.length; channel++) {
     * byteChannels[channel] = getBytes(Arrays.copyOf(floatChannels[channel], actualRetrieved), format);
     * <p>
     * }
     * <p>
     * interleavedChannels = AudioCommon.interleave(byteChannels);
     * <p>
     * return interleavedChannels;
     * <p>
     * }
     */

    public static float[][] convertBeforeFx(byte[] bytes, AudioFormat format) {


        float[] interleavedFloats = Sound.byteToFloatArray(bytes);
        float[][] floats = Sound.stereoToMono(interleavedFloats);
        return floats;

    }

    public static byte[] convertAfterFx(float[][] floatChannels, AudioFormat format, int actualRetrieved) {

        for (int channel = 0; channel < floatChannels.length; channel++) {
            floatChannels[channel] = (Arrays.copyOf(floatChannels[channel], actualRetrieved));
        }

        float[] interleavedFloats = Sound.monoToStereo(floatChannels);
        return Sound.floatToByteArray(interleavedFloats);


    }
}
/*** AudioCommon.java ***/

