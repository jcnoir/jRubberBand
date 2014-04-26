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

    @Test
    public void playLocalWav() throws MalformedURLException, MediumNotSupportedException {

        Music music;
        MusicPLayer dataPLayer;
        URL url;


        url = this.getClass().getResource("/sounds/grapevine.wav");
        music = new Music(url);
        dataPLayer = new MusicPLayer();
        dataPLayer.listen(music);
    }

    @Test
    public void playRemoteWav() throws MalformedURLException, MediumNotSupportedException {

        Music music;
        MusicPLayer musicPLayer;
        URL url;


        url = new URL("http://freewavesamples.com/files/Roland-GR-1-Trumpet-C5.wav");
        music = new Music(url);
        musicPLayer = new MusicPLayer();
        musicPLayer.listen(music);
    }

    @Test
    public void playLocalWavWithIdentityFX() throws MalformedURLException, MediumNotSupportedException, InterruptedException {

        Music music;
        MusicPLayer dataPLayer;
        URL url;
        FxStretcher fxStretcher;


        url = this.getClass().getResource("/sounds/grapevine.wav");
        music = new Music(url);
        fxStretcher = new FxStretcher(music);
        fxStretcher.setSpeed(1);
        fxStretcher.setPitch(1);

        dataPLayer = new MusicPLayer();
        dataPLayer.listen(fxStretcher);

        fxStretcher.exit();
    }

    @Test
    public void playLocalWavWithSpeed() throws MalformedURLException, MediumNotSupportedException, InterruptedException {

        Music music;
        MusicPLayer dataPLayer;
        URL url;
        FxStretcher fxStretcher;


        url = this.getClass().getResource("/sounds/grapevine.wav");
        //url = new File("/home/jcnoir/Téléchargements/Karl Frierson - Walkin In New York.wav").toURL();
        music = new Music(url);
        fxStretcher = new FxStretcher(music);
        fxStretcher.setSpeed(1.1);
        fxStretcher.setPitch(1);

        dataPLayer = new MusicPLayer();
        dataPLayer.listen(fxStretcher);

        fxStretcher.exit();
    }

    @Test
    public void playLocalWavWithPitch() throws MalformedURLException, MediumNotSupportedException, InterruptedException {

        Music music;
        MusicPLayer dataPLayer;
        URL url;
        FxStretcher fxStretcher;


        url = this.getClass().getResource("/sounds/grapevine.wav");
        //url = new File("/home/jcnoir/Téléchargements/Karl Frierson - Walkin In New York.wav").toURL();
        music = new Music(url);
        fxStretcher = new FxStretcher(music);
        fxStretcher.setSpeed(1);
        fxStretcher.setPitch(0.8);

        dataPLayer = new MusicPLayer();
        dataPLayer.listen(fxStretcher);

        fxStretcher.exit();
    }


}
