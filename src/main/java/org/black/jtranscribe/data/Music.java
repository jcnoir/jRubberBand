package org.black.jtranscribe.data;

import org.black.jtranscribe.dsp.common.MusicProvider;
import org.black.jtranscribe.exceptions.MediumNotSupportedException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.net.URL;

/**
 * User: jcnoir
 * Date: 15/07/12
 */
public class Music implements MusicProvider {

    private AudioInputStream audioInputStream;

    public Music(URL url) throws MediumNotSupportedException {
        try {
            loadMedium(url);
        } catch (UnsupportedAudioFileException | IOException e) {
            throw new MediumNotSupportedException(e);
        }
    }

    private void loadMedium(URL mediumUrl) throws UnsupportedAudioFileException, IOException {

        audioInputStream = AudioSystem.getAudioInputStream(mediumUrl);
    }


    public AudioInputStream getAudioInputStream() {
        return audioInputStream;
    }

    @Override
    public AudioInputStream getMusic() {
        return getAudioInputStream();  //To change body of implemented methods use File | Settings | File Templates.
    }
}
