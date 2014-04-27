import com.google.common.eventbus.EventBus;
import org.black.jtranscribe.JRubberBandConfiguration;
import org.black.jtranscribe.data.Music;
import org.black.jtranscribe.dsp.common.Stretcher;
import org.black.jtranscribe.dsp.common.command.*;
import org.black.jtranscribe.exceptions.MediumNotSupportedException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * User: jcnoir
 * Date: 15/07/12
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = JRubberBandConfiguration.class)
public class TestMusicPlayer {
    private static final Logger log = LoggerFactory.getLogger(Stretcher.class);
    public static final String WAV_AUDIO_PATH = "/home/jcnoir/Téléchargements/Stevie Wonder - Sir Duke.wav";
    private static final String FLAC_LOCAL_PATH = "/media/donnees/musiques/processed/original/sedentaire/flac/Karl Frierson - Soulprint/02 - Walkin' in New York.flac";
    public static final String MP3_LOCAL_PATH = "/home/jcnoir/Téléchargements/repet/Lady - Get Ready.mp3";
    @Autowired
    EventBus bus;
    @Autowired
    Music music;
    @Autowired
    Stretcher stretcher;

    @Test
    public void playLocalWav() throws MalformedURLException, MediumNotSupportedException {
        getMusic((WAV_AUDIO_PATH));
        bus.post(music);
    }

    @Test
    public void playRemoteWav() throws MalformedURLException, MediumNotSupportedException {
        music.setUrl(new URL("http://freewavesamples.com/files/Roland-GR-1-Trumpet-C5.wav"));
        bus.post(music);

    }

    @Test
    public void playLocalMp3() throws MalformedURLException, MediumNotSupportedException, InterruptedException {
        getMusic(MP3_LOCAL_PATH);
        bus.post(music);
        Thread.sleep(1000 * 20);
    }

    @Test
    public void playLocalMp3WithFx() throws MalformedURLException, MediumNotSupportedException, InterruptedException {
        getMusic(MP3_LOCAL_PATH);
        bus.post(new FxMusic(music));
        Thread.sleep(1000 * 5);
        bus.post(new FxSettings(0.7, 1));
        Thread.sleep(1000 * 5);
        bus.post(new Navigation());
        Thread.sleep(1000 * 5);
    }

    @Test
    public void playLocalOgg() throws MalformedURLException, MediumNotSupportedException, InterruptedException {
        getMusic(("/media/donnees/musiques/processed/original/sedentaire/ogg/Kenny Werner-Effortless Mastery-01-Meditation-Exercise #1.ogg"));
        bus.post(music);
        Thread.sleep(1000 * 20);
    }

    @Test
    public void playLocalFlac() throws MalformedURLException, MediumNotSupportedException, InterruptedException {
        getMusic(FLAC_LOCAL_PATH);
        bus.post(music);
        Thread.sleep(1000 * 5);
        bus.post(new Navigation());
        Thread.sleep(1000 * 30);
    }

    @Test
    public void playLocalWavWithIdentityFX() throws MalformedURLException, MediumNotSupportedException, InterruptedException {
        getMusic((WAV_AUDIO_PATH));
        bus.post(new FxMusic(music));
        Thread.sleep(1000 * 10);
        bus.post(new Mark());
        Thread.sleep(1000 * 10);
        bus.post(new ResetToMark());
        Thread.sleep(1000 * 10);

    }

    @Test
    public void playLocalWavWithSpeed() throws MalformedURLException, MediumNotSupportedException, InterruptedException {

        getMusic((WAV_AUDIO_PATH));
        bus.post(new FxMusic(music));
        bus.post(new FxSettings(0.3, 1));

    }

    @Test
    public void playLocalFlacWithSpeed() throws MalformedURLException, MediumNotSupportedException, InterruptedException {

        getMusic(FLAC_LOCAL_PATH);
        bus.post(new FxMusic(music));
        bus.post(new FxSettings(1.2, 1));
        Thread.sleep(1000 * 5);
        bus.post(new FxSettings(0.7, 1));
        Thread.sleep(1000 * 5);
        bus.post(new Navigation());
        Thread.sleep(1000 * 5);
    }

    @Test
    public void playLocalWavWithPitch() throws MalformedURLException, MediumNotSupportedException, InterruptedException {

        getMusic((WAV_AUDIO_PATH));
        bus.post(new FxMusic(music));
        bus.post(new FxSettings(1, 0.7));
        Thread.sleep(1000 * 40);

    }

    private Music getMusic(String filePath) throws MalformedURLException, MediumNotSupportedException {
        music.setUrl(new File(filePath).toURI().toURL());
        return music;
    }

    @Test
    public void loop() throws MalformedURLException, MediumNotSupportedException, InterruptedException {
        long start;
        long end;
        getMusic((WAV_AUDIO_PATH));
        bus.post(new FxMusic(music));
        bus.post(new FxSettings(1, 1));

        Thread.sleep(1000 * 5);
        start = stretcher.getPosition();
        Thread.sleep(1000 * 5);
        end = stretcher.getPosition();
        bus.post(new Loop(start, end, true));
        Thread.sleep(1000 * 30);
        bus.post(new Loop(start, end, false));
        Thread.sleep(1000 * 30);
    }

}
