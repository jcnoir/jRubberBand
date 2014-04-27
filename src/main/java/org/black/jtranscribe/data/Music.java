package org.black.jtranscribe.data;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.black.jtranscribe.dsp.common.MusicProvider;
import org.black.jtranscribe.dsp.common.command.Navigation;
import org.black.jtranscribe.exceptions.MediumNotSupportedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * User: jcnoir
 * Date: 15/07/12
 */
@Component
public class Music implements MusicProvider {

    private AudioInputStream audioInputStream;
    private static final Logger log = LoggerFactory.getLogger(Music.class);
    @Autowired
    private EventBus eventBus;

    @PostConstruct
    private void init() {
        eventBus.register(this);
    }

    public void setUrl(URL url) throws MediumNotSupportedException {
        try {

            audioInputStream = AudioSystem.getAudioInputStream((url));
            //audioInputStream = AudioSystem.getAudioInputStream(getByteArrayStream(url));
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
            this.audioInputStream = new AudioInputStream(getByteArrayStream(audioInputStream), audioInputStream.getFormat(), audioInputStream.getFrameLength());
            log.debug("Music length = {} frames", this.audioInputStream.getFrameLength());
            audioInputStream.mark(0);
        } catch (UnsupportedAudioFileException | IOException e) {
            throw new MediumNotSupportedException(e);
        }
    }

    public Music() {
    }

    public Music(File file) throws MalformedURLException, MediumNotSupportedException {
        setUrl(file.toURI().toURL());
    }

    public Music(URL url) throws MediumNotSupportedException {
        setUrl(url);
    }

    private ByteArrayInputStream getByteArrayStream(InputStream inputStream) throws IOException {
        int bufSize = 1024;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(bufSize);

        byte[] buffer = new byte[bufSize];
        int read;

        while ((read = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, read);
        }
        return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());

    }

    public AudioInputStream getAudioInputStream() {
        return audioInputStream;
    }

    @Override
    public AudioInputStream getMusic() {
        return getAudioInputStream();  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void close() {
        try {
            log.debug("Closing resources ...");
            audioInputStream.close();
            log.debug("Closing resources done");
        } catch (IOException e) {
            log.warn("Audio stream close failure : {}", e);
        }
    }

    @Subscribe
    public void navigationCommand(Navigation command) throws IOException {
        log.debug("Event received from the guava bus : {} !!!", command);
        try {
            audioInputStream.reset();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
