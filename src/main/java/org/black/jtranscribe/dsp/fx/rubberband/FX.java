package org.black.jtranscribe.dsp.fx.rubberband;

import org.black.jtranscribe.dsp.common.AudioCommon;
import org.black.jtranscribe.dsp.common.MusicFx;
import org.black.jtranscribe.dsp.common.MusicProvider;
import org.black.jtranscribe.dsp.fx.AudioInputStreamProxy;
import org.black.jtranscribe.generated.rubberband.RubberbandLibrary;
import org.bridj.Pointer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.AudioInputStream;

import static org.bridj.Pointer.allocateFloats;
import static org.bridj.Pointer.pointerToFloats;

/**
 * User: jcnoir
 * Date: 15/07/12
 */
public class FX implements MusicFx {

    private static final Logger log = LoggerFactory.getLogger(MusicFx.class);

    RubberbandLibrary.RubberBandState state;

    private AudioInputStream inputStream;


    @Override
    public void listen(MusicProvider data) {
        this.inputStream = new AudioInputStreamProxy(data.getMusic());

    }

    @Override
    public AudioInputStream getMusic() {
        return this.inputStream;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public double getSpeed() {
        return RubberbandLibrary.rubberband_get_time_ratio(state);
    }

    @Override
    public void setSpeed(double speed) {
        RubberbandLibrary.rubberband_set_time_ratio(state, speed);
    }

    @Override
    public double getPitch() {
        return RubberbandLibrary.rubberband_get_pitch_scale(state);
    }

    @Override
    public void setPitch(double pitch) {
        RubberbandLibrary.rubberband_set_pitch_scale(state, pitch);
    }


    public void process(byte[] bytes, boolean last) {

        float[][] samples;
        Pointer<Pointer<Float>> floatPointers;
        int lastSample;

        lastSample = last ? 1 : 0;
        samples = AudioCommon.bytesToFloats(bytes, this.getMusic().getFormat());
        floatPointers = pointerToFloats(samples);
        RubberbandLibrary.rubberband_process(state, floatPointers, samples[0].length, lastSample);


    }

    private float[][] convertBytesToFloats(byte[] bytes) {

        return AudioCommon.bytesToFloats(bytes, this.getMusic().getFormat());

    }


    public int available() {
        return RubberbandLibrary.rubberband_available(state);
    }

    public byte[] retrieve(int samples, int channelNumber) {

        int effectiveFrames = 0;
        Pointer<Pointer<Float>> allChannels = allocateFloats(channelNumber, samples);
        RubberbandLibrary.rubberband_retrieve(state, allChannels, samples);
        return new byte[0];

    }


    public FX() {
    }

    public void initRubberBand(AudioInputStream stream) {

        this.state = RubberbandLibrary.rubberband_new(Float.floatToIntBits(stream.getFormat().getSampleRate()), stream.getFormat().getChannels(), (int) RubberbandLibrary.RubberBandOption.RubberBandOptionProcessRealTime.value, 1.0, 1.0);


    }
}
