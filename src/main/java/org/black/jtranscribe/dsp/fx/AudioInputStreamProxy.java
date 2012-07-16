package org.black.jtranscribe.dsp.fx;

import javax.sound.sampled.AudioInputStream;
import java.io.IOException;

/**
 * User: jcnoir
 * Date: 15/07/12
 */
public class AudioInputStreamProxy extends AudioInputStream {

    public AudioInputStreamProxy(AudioInputStream audioInputStream) {
        super(audioInputStream, audioInputStream.getFormat(), audioInputStream.getFrameLength());    //To change body of overridden methods use File | Settings | File Templates.
    }


    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return super.read(b, off, len);
    }


}
