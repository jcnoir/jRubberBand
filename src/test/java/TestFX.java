import org.black.jtranscribe.data.Music;
import org.black.jtranscribe.dsp.common.*;
import org.black.jtranscribe.dsp.fx.rubberband.RubberbandStretcher;
import org.black.jtranscribe.dsp.fx.tarsos.MonoConverter;
import org.black.jtranscribe.dsp.fx.tarsos.TarsosStretcher;
import org.black.jtranscribe.dsp.player.MusicPLayer;
import org.black.jtranscribe.exceptions.MediumNotSupportedException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.concurrent.*;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

/**
 * User: jcnoir
 * Date: 15/07/12
 */
public class TestFX {

    private static final Logger log = LoggerFactory.getLogger(TestFX.class);

    @Test
    public void testFX() throws MediumNotSupportedException, ExecutionException, InterruptedException {

        final Stretcher stretcher;
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

        musicConsumer = new MusicPLayer();
        url = this.getClass().getResource("/sounds/grapevine.wav");
        final MusicProvider musicProvider = new Music(url);
        stretcher = new RubberbandStretcher(musicProvider.getMusic());


        futureMusic = pool.submit(new Runnable() {
            @Override
            public void run() {
                stretcher.listen(musicProvider);
                musicConsumer.listen(stretcher);
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
                //stretcher.getPitch();
                //stretcher.setPitch(2);
                //stretcher.setSpeed(2);

            }
        });
        futureMusic.get();
        pool.shutdown();


    }

    @Test
    public void testTarsosFX() throws MediumNotSupportedException, ExecutionException, InterruptedException, MalformedURLException {

        final Stretcher stretcher;
        final URL url;
        ThreadPoolExecutor threadPoolExecutor;
        Future futureMusic;


        ThreadPoolExecutor pool =
                new ThreadPoolExecutor(10,
                        10,
                        500,
                        TimeUnit.MILLISECONDS,
                        new SynchronousQueue<Runnable>());

        url = new File("/home/jcnoir/Bureau/grapevine-mono.wav").toURI().toURL();
        final MusicProvider musicProvider = new Music(url);
        stretcher = new TarsosStretcher();


        futureMusic = pool.submit(new Runnable() {
            @Override
            public void run() {
                stretcher.listen(musicProvider);
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
                //stretcher.getPitch();
                //stretcher.setPitch(2);
                //stretcher.setSpeed(2);

            }
        });
        futureMusic.get();
        pool.shutdown();


    }

    @Test
    public void testTarsosMonoFX() throws MediumNotSupportedException, ExecutionException, InterruptedException, MalformedURLException {

        final Stretcher stretcher;
        final URL url;
        ThreadPoolExecutor threadPoolExecutor;
        Future futureMusic;


        ThreadPoolExecutor pool =
                new ThreadPoolExecutor(10,
                        10,
                        500,
                        TimeUnit.MILLISECONDS,
                        new SynchronousQueue<Runnable>());

        url = this.getClass().getResource("/sounds/grapevine.wav");
        final MusicProvider musicProvider = new Music(url);
        final MusicFX monoConverter = new MonoConverter();
        stretcher = new TarsosStretcher();


        futureMusic = pool.submit(new Runnable() {
            @Override
            public void run() {
                monoConverter.listen(musicProvider);
                stretcher.listen(monoConverter);

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
                //stretcher.getPitch();
                //stretcher.setPitch(2);
                //stretcher.setSpeed(2);

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

        log.debug("Stream format : {}", stream.getFormat());

        int nBytesRead = 0;
        int nExternalBufferSize = 128000;
        byte[] abData = new byte[nExternalBufferSize];
        while (nBytesRead != -1) {
            try {
                nBytesRead = stream.read(abData, 0, abData.length);
                log.debug("bytes read : {}", abData);
                AudioCommon.bytesToFloats(abData, stream.getFormat());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


    }


    @Test
    public void testConversion() throws Exception {

        //TODO : Make sure that values are integers only
        float[] floats = {1f, 2f, 3f, 4f, 5f};
        byte[] bytes;
        float[] computedFloats;
        Music music;
        URL url;
        AudioInputStream stream;

        log.debug("Initial floats : {}", floats);
        url = this.getClass().getResource("/sounds/grapevine.wav");
        music = new Music(url);
        stream = music.getAudioInputStream();
        log.debug("Fake audio format used : " + stream.getFormat());
        bytes = AudioCommon.getBytes(floats, stream.getFormat());
        log.debug("Matching Bytes : {}", bytes);
        computedFloats = AudioCommon.getFloats(bytes, stream.getFormat());
        log.debug("Matching Floats : {}", computedFloats);
        assertTrue(Arrays.equals(floats, computedFloats));

    }

    @Test
    public void testDeInterleave() throws Exception {

        byte[] source = {11, 21, 31, 12, 22, 32};
        byte[][] expectedResult = {{11, 12}, {21, 22}, {31, 32}};
        byte[][] result;

        result = AudioCommon.deinterleave(source, 3);
        log.debug("Deinterleaved samples : {} ", result);

        for (int i = 0; i < expectedResult.length; i++) {

            for (int j = 0; j < expectedResult[0].length; j++) {

                assertTrue(expectedResult[i][j] == result[i][j]);

            }

        }
    }

    @Test
    public void testInterleave() throws Exception {

        byte[] expectedResult = {11, 21, 31, 12, 22, 32};
        byte[][] source = {{11, 12}, {21, 22}, {31, 32}};
        byte[] result;

        result = AudioCommon.interleave(source);
        log.debug("interleaved samples : {} ", result);

        assertArrayEquals(expectedResult, result);


    }

    @Test
    public void testCycleInterleave() throws Exception {

        byte[] source = {11, 21, 31, 12, 22, 32};
        byte[][] deinterleavedSource;
        byte[] result;

        log.debug("Source channels bytes : {}", source);
        deinterleavedSource = AudioCommon.deinterleave(source, 3);
        log.debug("Deinterleaved Source channels bytes : {}", deinterleavedSource);
        result = AudioCommon.interleave(deinterleavedSource);
        log.debug("interleaved samples : {} ", result);

        assertArrayEquals(source, result);
    }

    @Test
    public void testPrepareFx() throws Exception {
        byte[] source = {011, 012, 111, 112, 021, 022, 111, 112};
        float[][] channels;
        byte[] computedBytes;

        log.debug("Source channels bytes : {}", source);
        channels = AudioCommon.convertBeforeFx(source, getTestAudioFormat());
        log.debug("deinterleaved channels floats : {}", channels);
        computedBytes = AudioCommon.convertAfterFx(channels, getTestAudioFormat());
        log.debug("Re-interleaved channels bytes : {}", source);
        assertArrayEquals(source, computedBytes);

    }

    private AudioFormat getTestAudioFormat() throws MediumNotSupportedException {
        Music music;
        URL url;
        AudioInputStream stream;

        url = this.getClass().getResource("/sounds/grapevine.wav");
        music = new Music(url);
        stream = music.getAudioInputStream();
        return stream.getFormat();
    }
}
