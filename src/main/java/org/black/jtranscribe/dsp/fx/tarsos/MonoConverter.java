package org.black.jtranscribe.dsp.fx.tarsos;

import org.black.jtranscribe.dsp.common.MusicFX;
import org.black.jtranscribe.dsp.common.MusicProvider;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

/**
 * User: jcnoir
 * Date: 23/07/12
 */
public class MonoConverter implements MusicFX {


    private AudioInputStream inputStream;
    private AudioInputStream outputStream;

    @Override
    public void listen(MusicProvider data) {
        inputStream = data.getMusic();
    }

    @Override
    public AudioInputStream getMusic() {

        AudioFormat format = inputStream.getFormat();
        return AudioSystem.getAudioInputStream(new AudioFormat(format.getEncoding(), format.getSampleRate(), format.getSampleSizeInBits(), 1, format.getFrameSize(), format.getFrameRate(), format.isBigEndian()), inputStream);
    }
}
