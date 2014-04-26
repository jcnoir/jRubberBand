package org.black.jtranscribe.dsp.fx.rubberband;

import com.breakfastquay.rubberband.RubberBandStretcher;
import org.black.jtranscribe.dsp.common.AudioCommon;
import org.black.jtranscribe.dsp.common.MusicProvider;
import org.black.jtranscribe.dsp.common.Stretcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * User: jcnoir
 * Date: 15/07/12
 */
public class FxStretcher implements Stretcher {

    private static final Logger log = LoggerFactory.getLogger(Stretcher.class);

    private AudioInputStream originalInputStream;
    private AudioInputStream fxInputStream;

    private PipedInputStream pipedInputStream;
    private PipedOutputStream pipedOutputStream;

    private RubberBandStretcher rubberBand;

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    public FxStretcher() {
        super();
    }

    public FxStretcher(MusicProvider musicProvider) {
        this();
        this.listen(musicProvider);
    }

    @Override
    public void listen(MusicProvider data) {
        try {
            this.originalInputStream = data.getMusic();
            this.pipedInputStream = new PipedInputStream();
            this.pipedOutputStream = new PipedOutputStream(pipedInputStream);
            AudioFormat sourceFormat = originalInputStream.getFormat();
            AudioFormat decodedFormat = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    sourceFormat.getSampleRate(), 16, sourceFormat.getChannels(),
                    sourceFormat.getChannels() * 2, sourceFormat.getSampleRate(),
                    false);
            this.originalInputStream = AudioSystem.getAudioInputStream(decodedFormat, originalInputStream);
            this.rubberBand = new RubberBandStretcher((int) originalInputStream.getFormat().getSampleRate(), originalInputStream.getFormat().getChannels(),
                    RubberBandStretcher.OptionProcessRealTime |
                            RubberBandStretcher.OptionStretchPrecise |
                            RubberBandStretcher.OptionTransientsSmooth |
                            RubberBandStretcher.OptionDetectorCompound |
                            RubberBandStretcher.OptionPhaseLaminar |
                            RubberBandStretcher.OptionThreadingAuto |
                            RubberBandStretcher.OptionWindowStandard |
                            RubberBandStretcher.OptionSmoothingOn |
                            RubberBandStretcher.OptionFormantShifted |
                            RubberBandStretcher.OptionPitchHighQuality |
                            RubberBandStretcher.OptionChannelsTogether
                    , 1, 1
            );
            this.fxInputStream = new AudioInputStream(pipedInputStream, originalInputStream.getFormat(),originalInputStream.getFrameLength());
            rubberBand.reset();
            process();
        } catch (IOException e) {
            log.error("Listen failure : {}", e);
        }
    }

    @Override
    public AudioInputStream getMusic() {
        return this.fxInputStream;
    }

    @Override
    public double getSpeed() {
        return rubberBand.getTimeRatio();
    }

    @Override
    public void setSpeed(double speed) {
        rubberBand.setTimeRatio(speed);
    }

    @Override
    public double getPitch() {
        return rubberBand.getPitchScale();
    }

    @Override
    public void setPitch(double pitch) {
        rubberBand.setPitchScale(pitch);
    }


    public void process(byte[] bytes, boolean last, int offset, int size) {

        int bytesPerSample;
        float[][] samples;
        log.debug("Processing " + size + " interleaved bytes (last=" + last + ", offset=" + offset + ")");
        bytes = Arrays.copyOfRange(bytes, offset, size);
        samples = AudioCommon.convertBeforeFx(bytes, this.getMusic().getFormat());
        bytesPerSample = originalInputStream.getFormat().getFrameSize();
        log.debug(bytesPerSample + " bytes per sample detected");
        log.debug("Processing " + samples[0].length + " samples on " + samples.length + " channels " + "(last=" + last + ", offset=" + offset / bytesPerSample + ")");
        log.debug("Rubberband channels={}", rubberBand.getChannelCount());
        log.debug("Rubberband latency={}", rubberBand.getLatency());
        rubberBand.process(samples, offset / bytesPerSample, size / bytesPerSample, last);
        log.debug("Processing request done");
    }

    /**
     * @return the available number of samples
     */
    public int available() {
        int available = rubberBand.available();
        log.debug(available + " samples available from rubberband");
        return available;
    }

    public byte[] retrieve(int channelNumber, int offset, int length) {

        byte[] bytes;
        int actualRetrieved;
        float[][] floats = new float[channelNumber][length];
        actualRetrieved = rubberBand.retrieve(floats, offset, length);
        log.debug(actualRetrieved + " samples retrieved from rubberband");
        bytes = AudioCommon.convertAfterFx(floats, originalInputStream.getFormat(), actualRetrieved);
        log.debug(bytes.length + " bytes after conversion retrieved from rubberband, matching bytes per sample=" + (actualRetrieved != 0 ? bytes.length / (actualRetrieved * originalInputStream.getFormat().getChannels()) : 0));
        return bytes;

    }

    private void process() {

        executorService.submit(new Runnable() {
            @Override
            public void run() {
                byte[] originalStreamBuffer = new byte[rubberBand.getSamplesRequired() * originalInputStream.getFormat().getFrameSize()];
                byte[] processedBytes;
                int read;
                int requiredSamples = rubberBand.getSamplesRequired();

                try {
                    log.debug("Processing audio : originalStreamBuffer=" + originalStreamBuffer.length + ", requiredSamples=" + requiredSamples);
                    while ((read = originalInputStream.read(originalStreamBuffer, 0, (requiredSamples = rubberBand.getSamplesRequired()) * originalInputStream.getFormat().getFrameSize()) + 1) > 0) {
                        log.debug("Rubberband required samples={}", requiredSamples);
                        log.debug("{} bytes have been read from original stream", read);
                        log.info("Processing data with speed={}, pitch={}", rubberBand.getTimeRatio(), rubberBand.getPitchScale());
                        process(originalStreamBuffer, false, 0, read);
                        processedBytes = retrieve(originalInputStream.getFormat().getChannels(), 0, available());
                        pipedOutputStream.write(processedBytes);
                        pipedOutputStream.flush();
                    }
                } catch (Exception ex) {
                    log.error("Music stream read failure", ex);
                }
            }
        });


    }

    public void exit() {
        this.rubberBand.dispose();
    }
}
