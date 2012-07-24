package org.black.jtranscribe.dsp.fx.tarsos;

import be.hogent.tarsos.dsp.AudioDispatcher;
import be.hogent.tarsos.dsp.AudioPlayer;
import org.black.jtranscribe.dsp.common.MusicProvider;
import org.black.jtranscribe.dsp.common.Stretcher;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

/**
 * User: jcnoir
 * Date: 23/07/12
 */
public class TarsosStretcher implements Stretcher {
    private AudioDispatcher dispatcher;

    @Override
    public double getSpeed() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setSpeed(double speed) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public double getPitch() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setPitch(double pitch) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void listen(MusicProvider data) {
        AudioFormat format;
        try {
            format = data.getMusic().getFormat();

            dispatcher = new AudioDispatcher(data.getMusic(), format.getSampleSizeInBits() * data.getMusic().getFormat().getChannels() * 512 * 1024, 512);
            dispatcher.addAudioProcessor(new AudioPlayer(data.getMusic().getFormat()));

            Thread t = new Thread(dispatcher);
            t.start();
        } catch (Exception e) {
// TODO Auto-generated catch block
        }

    }

    @Override
    public AudioInputStream getMusic() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

}
