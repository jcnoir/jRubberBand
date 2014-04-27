package org.black.jtranscribe.dsp.fx.rubberband;

import com.breakfastquay.rubberband.RubberBandStretcher;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.black.jtranscribe.dsp.common.AudioCommon;
import org.black.jtranscribe.dsp.common.MusicProvider;
import org.black.jtranscribe.dsp.common.Stretcher;
import org.black.jtranscribe.dsp.common.command.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Arrays;

/**
 * User: jcnoir
 * Date: 15/07/12
 */
@Component
public class FxStretcher implements Stretcher {

    private static final Logger log = LoggerFactory.getLogger(Stretcher.class);
    @Autowired
    private EventBus bus;
    private AudioInputStream originalInputStream;
    private AudioInputStream fxInputStream;

    private PipedInputStream pipedInputStream;
    private PipedOutputStream pipedOutputStream;

    private RubberBandStretcher rubberBand;

    private double speed = 1;
    private double pitch = 1;

    private long position = 0;
    private long loopStart = 0;
    private long loopEnd = 0;
    private boolean loop = false;


    public FxStretcher() {
        super();
    }

    @PostConstruct
    private void init() {
        bus.register(this);
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
                    , speed, pitch
            );
            this.fxInputStream = new AudioInputStream(pipedInputStream, originalInputStream.getFormat(), originalInputStream.getFrameLength());
            rubberBand.reset();
            //Notify the music player to listen to the output of the stretcher.
            bus.post(this);
            bus.post(new ProcessFx());
        } catch (IOException e) {
            log.error("Listen failure : {}", e);
        }
    }

    @Override
    public long getPosition() {
        return position;
    }

    @Override
    public AudioInputStream getMusic() {
        return this.fxInputStream;
    }

    @Override
    public void close() {

        log.debug("Closing resources ...");
        rubberBand.dispose();

        try {
            originalInputStream.close();
        } catch (IOException e) {
            log.warn("Stream close failure : {}", e);
        }

        try {
            fxInputStream.close();
        } catch (IOException e) {
            log.warn("Stream close failure : {}", e);
        }

        try {
            pipedInputStream.close();
        } catch (IOException e) {
            log.warn("Stream close failure : {}", e);
        }

        try {
            pipedOutputStream.close();
        } catch (IOException e) {
            log.warn("Stream close failure : {}", e);
        }

        log.debug("Closing resources done");

    }

    @Override
    public double getSpeed() {
        return speed;
    }

    @Override
    public void setSpeed(double speed) {
        this.speed = speed;
        if (rubberBand != null) {
            rubberBand.setTimeRatio(speed);
        }
    }

    @Override
    public double getPitch() {
        return pitch;
    }

    @Override
    public void setPitch(double pitch) {
        this.pitch = pitch;
        if (rubberBand != null) {
            rubberBand.setPitchScale(pitch);
        }
    }


    public void process(byte[] bytes, boolean last, int offset, int size) {

        int bytesPerSample;
        float[][] samples;
        // log.debug("Processing " + size + " interleaved bytes (last=" + last + ", offset=" + offset + ")");
        bytes = Arrays.copyOfRange(bytes, offset, size);
        samples = AudioCommon.convertBeforeFx(bytes, this.getMusic().getFormat());
        bytesPerSample = originalInputStream.getFormat().getFrameSize();
        /**
         log.debug(bytesPerSample + " bytes per sample detected");
         log.debug("Processing " + samples[0].length + " samples on " + samples.length + " channels " + "(last=" + last + ", offset=" + offset / bytesPerSample + ")");
         log.debug("Rubberband channels={}", rubberBand.getChannelCount());
         log.debug("Rubberband latency={}", rubberBand.getLatency());
         **/
        rubberBand.process(samples, offset / bytesPerSample, size / bytesPerSample, last);
        //log.debug("Processing request done");
    }

    /**
     * @return the available number of samples
     */
    public int available() {
        int available = rubberBand.available();
        // log.debug(available + " samples available from rubberband");
        return available;
    }

    public byte[] retrieve(int channelNumber, int offset, int length) {

        byte[] bytes;
        int actualRetrieved;
        float[][] floats = new float[channelNumber][length];
        actualRetrieved = rubberBand.retrieve(floats, offset, length);
        //log.debug(actualRetrieved + " samples retrieved from rubberband");
        bytes = AudioCommon.convertAfterFx(floats, originalInputStream.getFormat(), actualRetrieved);
        //log.debug(bytes.length + " bytes after conversion retrieved from rubberband, matching bytes per sample=" + (actualRetrieved != 0 ? bytes.length / (actualRetrieved * originalInputStream.getFormat().getChannels()) : 0));
        return bytes;

    }

    @Subscribe
    public void process(ProcessFx processFx) {
        byte[] originalStreamBuffer = new byte[rubberBand.getSamplesRequired() * originalInputStream.getFormat().getFrameSize()];
        byte[] processedBytes;
        int read;
        int requiredSamples = rubberBand.getSamplesRequired();

        try {
            log.debug("Processing audio : originalStreamBuffer=" + originalStreamBuffer.length + ", requiredSamples=" + requiredSamples);
            this.originalInputStream.mark(0);
            while ((read = originalInputStream.read(originalStreamBuffer, 0, (requiredSamples = rubberBand.getSamplesRequired()) * originalInputStream.getFormat().getFrameSize()) + 1) > 0) {
                position = position + read;
                if (loop && position >= loopEnd) {
                    log.info("Looping : Resetting audio stream from {} ==> {}", position, loopStart);
                    move(loopStart);
                } else {
                    //log.debug("Rubberband required samples={}", requiredSamples);
                    //log.debug("{} bytes have been read from original stream", read);
                    //log.info("Processing data with speed={}, pitch={}", rubberBand.getTimeRatio(), rubberBand.getPitchScale());
                    process(originalStreamBuffer, false, 0, read);
                    processedBytes = retrieve(originalInputStream.getFormat().getChannels(), 0, available());
                    pipedOutputStream.write(processedBytes);
                }
            }
        } catch (Exception ex) {
            log.error("Music stream read failure", ex);
        }
    }

    @Subscribe
    public void stretch(FxMusic fxMusic) {
        log.info("Stretching resource : {}", fxMusic.getMusicProvider());
        this.listen(fxMusic.getMusicProvider());
    }

    @Subscribe
    public void updateStretch(FxSettings stretchUpdateCommand) {
        log.info("Stretching updated : {}", stretchUpdateCommand);
        setPitch(stretchUpdateCommand.getPitch());
        setSpeed(stretchUpdateCommand.getSpeed());
    }

    @Subscribe
    public void mark(Mark mark) {
        log.info("Mark : {}", position);
        this.originalInputStream.mark(0);
    }

    @Subscribe
    public void reset(ResetToMark resetToMark) {
        log.info("ResetToMark");
        try {
            this.originalInputStream.skip(25698);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void loop(Loop loop) {
        log.info("Loop command received : {}", loop);
        this.loop = loop.isLoopEnabled();
        this.loopEnd = loop.getLoopEnd();
        this.loopStart = loop.getLoopStart();
    }

    @Subscribe
    public void gotoCommand(GotoCommand gotoCommand) throws IOException {
        log.info("Goto command received : {}", gotoCommand);
        move(gotoCommand.getFramePosition());
    }

    private void move(long position) {
        try {

            long frameNumber;
            float time;

            frameNumber = position / (originalInputStream.getFormat().getFrameSize());
            time = frameNumber / (originalInputStream.getFormat().getFrameRate());

            log.info("Moving to byte={}, frame={}, time={}", new Object[]{position, frameNumber, DurationFormatUtils.formatDurationHMS((long) (time * 1000))});
            originalInputStream.reset();
            originalInputStream.skip(position);
            this.position = position;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
