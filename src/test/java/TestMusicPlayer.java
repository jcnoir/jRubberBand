import org.black.jtranscribe.data.Music;
import org.black.jtranscribe.dsp.common.Stretcher;
import org.black.jtranscribe.dsp.fx.rubberband.FxStretcher;
import org.black.jtranscribe.dsp.player.MusicPLayer;
import org.black.jtranscribe.exceptions.MediumNotSupportedException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * User: jcnoir
 * Date: 15/07/12
 */
public class TestMusicPlayer {
    private static final Logger log = LoggerFactory.getLogger(Stretcher.class);
    public static final String WAV_AUDIO_PATH = "/home/jcnoir/Téléchargements/Stevie Wonder - Sir Duke.wav";
    private Music music;
    private MusicPLayer dataPLayer;


    public TestMusicPlayer() throws MalformedURLException, MediumNotSupportedException {
        dataPLayer = new MusicPLayer();
    }

    @Test
    public void playLocalWav() throws MalformedURLException, MediumNotSupportedException {
        music = new Music(new File(WAV_AUDIO_PATH));
        dataPLayer.listen(music);
    }

    @Test
    public void playRemoteWav() throws MalformedURLException, MediumNotSupportedException {
        music = new Music(new URL("http://freewavesamples.com/files/Roland-GR-1-Trumpet-C5.wav"));

        dataPLayer.listen(music);
    }

    @Test
    public void playLocalMp3() throws MalformedURLException, MediumNotSupportedException {
        music = new Music(new File("/home/jcnoir/Téléchargements/repet/Lady - Get Ready.mp3"));
        dataPLayer.listen(music);
    }

    @Test
    public void playLocalOgg() throws MalformedURLException, MediumNotSupportedException {
        music = new Music(new File("/media/donnees/musiques/processed/original/sedentaire/ogg/Kenny Werner-Effortless Mastery-01-Meditation-Exercise #1.ogg"));
        dataPLayer.listen(music);
    }

    @Test
    public void playLocalFlac() throws MalformedURLException, MediumNotSupportedException {
        music = new Music(new File("/media/donnees/musiques/processed/original/sedentaire/flac/Karl Frierson - Soulprint/02 - Walkin' in New York.flac"));
        dataPLayer.listen(music);
    }

    @Test
    public void playLocalWavWithIdentityFX() throws MalformedURLException, MediumNotSupportedException, InterruptedException {
        FxStretcher fxStretcher;
        music = new Music(new File(WAV_AUDIO_PATH));
        fxStretcher = new FxStretcher(music);
        fxStretcher.setSpeed(1);
        fxStretcher.setPitch(1);
        dataPLayer = new MusicPLayer();
        dataPLayer.listen(fxStretcher);
        fxStretcher.exit();
    }

    @Test
    public void playLocalWavWithSpeed() throws MalformedURLException, MediumNotSupportedException, InterruptedException {

        FxStretcher fxStretcher;
        music = new Music(new File(WAV_AUDIO_PATH));
        fxStretcher = new FxStretcher(music);
        fxStretcher.setSpeed(1.5);
        fxStretcher.setPitch(1);
        dataPLayer = new MusicPLayer();
        dataPLayer.listen(fxStretcher);
        fxStretcher.exit();
    }

    @Test
    public void playLocalWavWithPitch() throws MalformedURLException, MediumNotSupportedException, InterruptedException {

        FxStretcher fxStretcher;
        music = new Music(new File(WAV_AUDIO_PATH));
        fxStretcher = new FxStretcher(music);
        fxStretcher.setSpeed(1);
        fxStretcher.setPitch(0.8);
        dataPLayer = new MusicPLayer();
        dataPLayer.listen(fxStretcher);
        fxStretcher.exit();
    }


}
