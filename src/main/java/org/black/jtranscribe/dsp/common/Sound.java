package org.black.jtranscribe.dsp.common;/*
 * Created on Feb 19, 2005
 * 
 */
/**
 * @author Arthur Asuncion
 *
 * This class provides sound capabilities to the program
 */

import javax.sound.sampled.AudioFormat;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public class Sound {


    /**
     * This class turns an interleaved stereo file into two mono files
     *
     * @param in interleaved stereo float array
     * @return a Doubleton which contains two float arrays for left & right channels
     */
    static float[][] stereoToMono(float[] in) {

        float[][] floats = new float[2][in.length / 2];

        for (int i = 0; i < in.length; i = i + 2) {

            floats[0][i / 2] = in[i];
            floats[1][i / 2] = in[i + 1];
        }

        return floats;

    }


    /**
     * This performs the exact opposite of stereoToMono.  It turns two mono files
     * into an interleaved stereo file.
     *
     * @return an interleaved stereo file
     */
    static float[] monoToStereo(float[][] floats) {

        float[] stereo = new float[floats[0].length * floats.length];

        for (int i = 0; i < floats[0].length * floats.length; i = i + floats.length) {
            for (int channel = 0; channel < floats.length; channel++) {
                stereo[i + channel] = floats[channel][i / floats.length];
            }
        }

        return stereo;


    }


    /**
     * This function turns a byte array into a float array.
     *
     * @param in the byte array
     * @return the float array
     */
    static float[] byteToFloatArray(byte[] in) {

        float[] f = new float[in.length / 2];
        byte[] sampleByteArray = new byte[2];
        int sample;

        for (int i = 0; i < in.length - 1; i = i + 2) {
            sampleByteArray[0] = in[i + 1];
            sampleByteArray[1] = in[i];

            sample = (new BigInteger(sampleByteArray)).intValue();

            f[i / 2] = (int) sample;

        }

        return f;

    }

    static float[] byteToFloatArray2(byte[] in, AudioFormat format) {

        ByteBuffer byteBuffer = ByteBuffer.wrap(in);
        //byteBuffer.order(format.isBigEndian() ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
        FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();
        float[] b = new float[floatBuffer.remaining()];
        floatBuffer.get(b, 0, b.length);
        return b;
    }

    /**
     * This function turns a float array into a byte array so that Java can recognize it.
     * This was one of the most complicated methods as it required bit twiddling
     *
     * @param in the float array
     * @return the byte array
     */
    static byte[] floatToByteArray(float[] in) {
        byte[] b = new byte[in.length * 2];
        byte[] sampleByteArray = new byte[2];
        byte[] tempSampleArray;
        int sample;

        for (int i = 0; i < in.length; i++) {

            tempSampleArray = (new BigInteger(new Integer((int) in[i]).toString())).toByteArray();

            if (tempSampleArray.length == 1) // that means that temp is only 1 byte
            {
                if (tempSampleArray[0] < 0) {
                    sampleByteArray[0] = (byte) (0xFF);
                    sampleByteArray[1] = tempSampleArray[0]; // this just extends the minus sign for 2's complement
                    // -1 = 0xFF

                } else if (tempSampleArray[0] >= 0) {

                    sampleByteArray[0] = 0;
                    sampleByteArray[1] = tempSampleArray[0];

                }

            } else sampleByteArray = tempSampleArray;

            b[2 * i] = sampleByteArray[1];
            b[2 * i + 1] = sampleByteArray[0];

        }

        return b;
    }


}