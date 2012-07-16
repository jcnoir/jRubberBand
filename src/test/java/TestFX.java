import org.black.jtranscribe.data.Music;
import org.black.jtranscribe.dsp.common.AudioCommon;
import org.black.jtranscribe.dsp.common.MusicConsumer;
import org.black.jtranscribe.dsp.common.MusicFx;
import org.black.jtranscribe.dsp.common.MusicProvider;
import org.black.jtranscribe.dsp.fx.rubberband.FX;
import org.black.jtranscribe.dsp.player.MusicPLayer;
import org.black.jtranscribe.exceptions.MediumNotSupportedException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.AudioInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.*;

import static org.junit.Assert.assertTrue;

/**
 * User: jcnoir
 * Date: 15/07/12
 */
public class TestFX {

    private static final Logger log = LoggerFactory.getLogger(TestFX.class);

    @Test
    public void testFX() throws MediumNotSupportedException, ExecutionException, InterruptedException {

        final MusicFx musicFx;
        final MusicConsumer musicConsumer;
        final URL url;
        ThreadPoolExecutor threadPoolExecutor;
        Future futureMusic;


        ThreadPoolExecutor pool =
                new ThreadPoolExecutor(10,
                        10,
                        500,
                        TimeUnit.MILLISECONDS,
                        new SynchronousQueue<Runnable>());

        musicFx = new FX();
        musicConsumer = new MusicPLayer();
        url = this.getClass().getResource("/sounds/grapevine.wav");
        final MusicProvider musicProvider = new Music(url);


        futureMusic = pool.submit(new Runnable() {
            @Override
            public void run() {
                musicFx.listen(musicProvider);
                musicConsumer.listen(musicFx);
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }

            }
        });

        pool.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                //musicFx.getPitch();
                //musicFx.setPitch(2);
                //musicFx.setSpeed(2);

            }
        });
        futureMusic.get();
        pool.shutdown();


    }

    @Test
    public void testConverter() throws MediumNotSupportedException, IOException {
        Music music;
        MusicPLayer dataPLayer;
        URL url;
        AudioInputStream stream;

        url = this.getClass().getResource("/sounds/grapevine.wav");
        music = new Music(url);

        stream = music.getAudioInputStream();

        int nBytesRead = 0;
        int nExternalBufferSize = 128000;
        byte[] abData = new byte[nExternalBufferSize];
        while (nBytesRead != -1) {
            try {
                nBytesRead = stream.read(abData, 0, abData.length);

                AudioCommon.bytesToFloats(abData, stream.getFormat());

            } catch (IOException e) {
                e.printStackTrace();
            }

        }


    }

    @Test
    public void testIdentity() throws MediumNotSupportedException {

        float[][] floats = {{0f, 0.1f, 0.3f, 0.4f}, {0.5f, 0.6f, 0.7f, 0.8f}};
        byte[] bytes;
        float[][] computedFloats;
        Music music;
        URL url;
        AudioInputStream stream;
        int floatNumer = 4;
        Random random = new Random(15697);

//        floats = new float[2][floatNumer];

        for (int c = 0; c < 2; c++) {
            for (int i = 1; i < floatNumer; i++) {
                floats[c][i] = (random.nextFloat() - random.nextFloat());
            }

        }

        log.debug("Initial floats : {}", floats);

        url = this.getClass().getResource("/sounds/grapevine.wav");
        music = new Music(url);

        stream = music.getAudioInputStream();


        bytes = AudioCommon.floatsToBytes(floats, stream.getFormat());

        log.debug("Matching Bytes : {}", bytes);


        computedFloats = AudioCommon.bytesToFloats(bytes, stream.getFormat());

        log.debug("Matching Floats : {}", computedFloats);


        assertTrue(Arrays.equals(floats, computedFloats));


    }


}
