import org.black.jtranscribe.dsp.common.AudioCommon;
import org.junit.Test;

import javax.sound.sampled.AudioFormat;

import static org.junit.Assert.assertArrayEquals;

/**
 * Created by jcnoir on 21/04/14.
 */
public class TestCommon {

    int samples = 100;
    byte[][] bytesresult;


    byte[] leftByteChannel = new byte[samples];
    byte[] rightByteChannel = new byte[samples];
    byte[][] byteChannels = new byte[2][samples];
    AudioFormat audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_FLOAT, 44100, 16, 2, 2, 44100, true);

    float[] floats;

    @Test
    public void test() {

        for (byte i = 0; i < samples; i++) {
            leftByteChannel[i] = (byte) (i);
            rightByteChannel[i] = (byte) (i + samples);
        }
        byteChannels[0] = leftByteChannel;
        byteChannels[1] = rightByteChannel;


        floats = AudioCommon.getFloats(AudioCommon.interleave(byteChannels), audioFormat);
        bytesresult = AudioCommon.deinterleave(AudioCommon.getBytes(floats, audioFormat), 2);
        assertArrayEquals(leftByteChannel, bytesresult[0]);
        assertArrayEquals(rightByteChannel, bytesresult[1]);


    }


}
