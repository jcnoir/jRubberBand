package org.black.jtranscribe.dsp.fx;

import org.black.jtranscribe.dsp.fx.rubberband.RubberbandStretcher;

import javax.sound.sampled.AudioInputStream;
import java.io.IOException;

/**
 * User: jcnoir
 * Date: 15/07/12
 */
public class AudioInputStreamProxy extends AudioInputStream {

    private RubberbandStretcher rubberbandStretcher;

    public AudioInputStreamProxy(AudioInputStream audioInputStream, RubberbandStretcher rubberbandStretcher) {
        super(audioInputStream, audioInputStream.getFormat(), audioInputStream.getFrameLength());    //To change body of overridden methods use File | Settings | File Templates.
        this.rubberbandStretcher = rubberbandStretcher;

    }


    @Override
    public int read(byte[] b, int off, int len) throws IOException {

        int available;

        rubberbandStretcher.process(b, false);
        available = rubberbandStretcher.available();
        rubberbandStretcher.retrieve(available, this.getFormat().getChannels());


        return super.read(b, off, len);
    }


}
