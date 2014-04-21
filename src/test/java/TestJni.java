import com.breakfastquay.rubberband.RubberBandStretcher;
import org.black.jtranscribe.data.Music;
import org.black.jtranscribe.dsp.fx.rubberband.FxStretcher;
import org.black.jtranscribe.dsp.player.MusicPLayer;
import org.junit.Test;

import javax.sound.sampled.AudioInputStream;
import java.net.URL;


/**
 * User: jcnoir
 * Date: 14/07/12
 */
public class TestJni {


    @Test
    public void testNativeLibInit() {

        RubberBandStretcher rubberBand = new RubberBandStretcher(44100, 2, 0, 1.0, 1.0);
        System.out.println("INIT done !");
        rubberBand.reset();
        System.out.println("Available ? : " + rubberBand.available());
    }


    @Test
    public void testBAsicRubberBand() throws Exception {

        Music music;
        MusicPLayer dataPLayer;
        URL url;
        AudioInputStream stream;
        byte[] bytes = new byte[512];

        url = this.getClass().getResource("/sounds/grapevine.wav");
        music = new Music(url);


        FxStretcher rubberbandStretcher = new FxStretcher(music.getAudioInputStream());
        music.getAudioInputStream().read(bytes);
        rubberbandStretcher.process(bytes, true, 0, bytes.length);

    }

    @Test
    public void testJNI() throws Exception {

        Music music;
        MusicPLayer dataPLayer;
        URL url;
        AudioInputStream stream;
        byte[] bytes = new byte[512];

        url = this.getClass().getResource("/sounds/grapevine.wav");
        music = new Music(url);

        FxStretcher rubberbandStretcher = new FxStretcher(44100, 2, 0, 1, 1);
        rubberbandStretcher.listen(music);


    }
}
