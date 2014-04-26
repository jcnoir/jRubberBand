package org.black.jtranscribe.data;

import org.black.jtranscribe.dsp.common.MusicProvider;
import org.black.jtranscribe.exceptions.MediumNotSupportedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * User: jcnoir
 * Date: 15/07/12
 */
public class Music implements MusicProvider {

    private AudioInputStream audioInputStream;
    private static final Logger log = LoggerFactory.getLogger(Music.class);


    public Music(URL url) throws MediumNotSupportedException {
        try {
            audioInputStream = AudioSystem.getAudioInputStream(url);
            AudioFormat sourceFormat = audioInputStream.getFormat();
            log.info("Creating music resource from : {}", url);
            log.info("Original music format : {}", sourceFormat);
            AudioFormat decodedFormat = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    sourceFormat.getSampleRate(), 16, sourceFormat.getChannels(),
                    sourceFormat.getChannels() * 2, sourceFormat.getSampleRate(),
                    false);
            log.info("Decoded music format : {}", decodedFormat);
            this.audioInputStream = AudioSystem.getAudioInputStream(decodedFormat, audioInputStream);

        } catch (UnsupportedAudioFileException | IOException e) {
            throw new MediumNotSupportedException(e);
        }
    }

    public Music(File file) throws MalformedURLException, MediumNotSupportedException {
        this(file.toURI().toURL());
    }

    public AudioInputStream getAudioInputStream() {
        return audioInputStream;
    }

    @Override
    public AudioInputStream getMusic() {
        return getAudioInputStream();  //To change body of implemented methods use File | Settings | File Templates.
    }
}
