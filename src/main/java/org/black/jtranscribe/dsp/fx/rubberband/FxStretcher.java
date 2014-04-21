package org.black.jtranscribe.dsp.fx.rubberband;

import com.breakfastquay.rubberband.RubberBandStretcher;
import org.black.jtranscribe.dsp.common.AudioCommon;
import org.black.jtranscribe.dsp.common.MusicProvider;
import org.black.jtranscribe.dsp.common.Stretcher;
import org.black.jtranscribe.dsp.fx.AudioInputStreamProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.AudioInputStream;

/**
 * User: jcnoir
 * Date: 15/07/12
 */
public class FxStretcher implements Stretcher {

    private static final Logger log = LoggerFactory.getLogger(Stretcher.class);

    private AudioInputStream inputStream;

    private RubberBandStretcher rubberBand;


    @Override
    public void listen(MusicProvider data) {
        this.inputStream = new AudioInputStreamProxy(data.getMusic(), this);
    }

    @Override
    public AudioInputStream getMusic() {
        return this.inputStream;  //To change body of implemented methods use File | Settings | File Templates.
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
        log.debug("Processing " + bytes.length + " interleaved bytes (last=" + last + ", offset=" + offset + ")");
        samples = AudioCommon.convertBeforeFx(bytes, this.getMusic().getFormat());
        bytesPerSample = bytes.length / samples[0].length;
        log.debug(bytesPerSample + " bytes per sample detected");
        log.debug("Processing " + samples[0].length + " samples on " + samples.length + " channels " + "(last=" + last + ", offset=" + offset / bytesPerSample + ")");
        rubberBand.process(samples, offset / bytesPerSample, size / bytesPerSample, last);
        log.debug("Processing request done");
    }

    private float[][] convertBytesToFloats(byte[] bytes) {

        return AudioCommon.convertBeforeFx(bytes, this.inputStream.getFormat());

    }


    public int available() {
        int available = rubberBand.available();
        log.debug(available + " samples available from rubberband");
        return available;
    }

    public byte[] retrieve(int channelNumber, int offset, int length) {

        byte[] bytes;
        float[][] floats = new float[channelNumber][length];


        rubberBand.retrieve(floats, offset, length);
        bytes = AudioCommon.convertAfterFx(floats, inputStream.getFormat());
        return bytes;

    }

    public FxStretcher(AudioInputStream stream) {

        this((int) (stream.getFormat().getSampleRate()), stream.getFormat().getChannels(), RubberBandStretcher.OptionProcessRealTime, 1, 1);
        this.inputStream = new AudioInputStreamProxy(stream, this);

    }

    public FxStretcher(int sampleRate, int channels, int options, double timeScale, double pitchScale) {

        this.rubberBand = new RubberBandStretcher(sampleRate, channels, options, timeScale, pitchScale);


    }
}
