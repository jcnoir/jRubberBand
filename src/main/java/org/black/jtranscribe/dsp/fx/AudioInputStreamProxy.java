package org.black.jtranscribe.dsp.fx;

import org.black.jtranscribe.dsp.fx.rubberband.FxStretcher;

import javax.sound.sampled.AudioInputStream;
import java.io.IOException;

/**
 * User: jcnoir
 * Date: 15/07/12
 */
public class AudioInputStreamProxy extends AudioInputStream {

    private FxStretcher rubberbandStretcher;
    private AudioInputStream originalStream;

    public AudioInputStreamProxy(AudioInputStream audioInputStream, FxStretcher rubberbandStretcher) {
        super(audioInputStream, audioInputStream.getFormat(), audioInputStream.getFrameLength());
        this.rubberbandStretcher = rubberbandStretcher;
        this.originalStream = audioInputStream;

    }


    @Override
    public int read(byte[] b, int off, int len) throws IOException {

        int available;
        byte[] originalStreamBuffer = new byte[b.length];
        byte[] processedBytes;
        int originalStreamRead;

        originalStreamRead = originalStream.read(originalStreamBuffer, off, len);
        rubberbandStretcher.process(originalStreamBuffer, false, off, originalStreamRead);
        available = rubberbandStretcher.available();
        processedBytes = rubberbandStretcher.retrieve(this.getFormat().getChannels(), off, available);
        replaceBytes(processedBytes, b, originalStreamRead);
        return Math.min(processedBytes.length, originalStreamRead);
    }

    private void replaceBytes(byte[] source, byte[] target, int size) {
        for (int i = 0; i < source.length && i < size; i++) {
            target[i] = source[i];
        }
    }


}
