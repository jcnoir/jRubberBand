import org.black.jtranscribe.data.Music;
import org.black.jtranscribe.dsp.fx.rubberband.RubberbandStretcher;
import org.black.jtranscribe.dsp.player.MusicPLayer;
import org.black.jtranscribe.generated.rubberband.RubberbandLibrary;
import org.junit.Test;

import javax.sound.sampled.AudioInputStream;
import java.net.URL;


/**
 * User: jcnoir
 * Date: 14/07/12
 */
public class TestJni {


    @Test
    public void test_bridj_c() {

        RubberbandLibrary.RubberBandState state;
        state = RubberbandLibrary.rubberband_new(44100, 2, 0, 1.0, 1.0);
        System.out.println("INIT done !");
        RubberbandLibrary.rubberband_reset(state);
        System.out.println("Available ? : " + RubberbandLibrary.rubberband_available(state));
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


        RubberbandStretcher rubberbandStretcher = new RubberbandStretcher(music.getAudioInputStream());
        music.getAudioInputStream().read(bytes);
        rubberbandStretcher.process(bytes, true);

    }
}
