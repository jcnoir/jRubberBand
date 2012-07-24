package org.black.jtranscribe.dsp.fx.rubberband;

import org.black.jtranscribe.dsp.common.AudioCommon;
import org.black.jtranscribe.dsp.common.Stretcher;
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
public class RubberbandStretcher implements Stretcher {

    private static final Logger log = LoggerFactory.getLogger(Stretcher.class);

    RubberbandLibrary.RubberBandState state;

    private AudioInputStream inputStream;


    @Override
    public void listen(MusicProvider data) {
        this.inputStream = new AudioInputStreamProxy(data.getMusic(), this);
//        this.initRubberBand(this.inputStream);


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
        samples = AudioCommon.convertBeforeFx(bytes, this.getMusic().getFormat());
        floatPointers = pointerToFloats(samples);
        RubberbandLibrary.rubberband_process(state, floatPointers, samples[0].length, lastSample);


    }

    private float[][] convertBytesToFloats(byte[] bytes) {

        return AudioCommon.convertBeforeFx(bytes, this.inputStream.getFormat());

    }


    public int available() {
        return RubberbandLibrary.rubberband_available(state);
    }

    public byte[] retrieve(int samples, int channelNumber) {

        float[][] floats;
        byte[] bytes;

        Pointer<Pointer<Float>> allChannels = allocateFloats(channelNumber, samples);
        RubberbandLibrary.rubberband_retrieve(state, allChannels, samples);
        floats = getfloats(allChannels);
        bytes = AudioCommon.convertAfterFx(floats, inputStream.getFormat());
        return bytes;

    }

    private float[][] getfloats(Pointer<Pointer<Float>> floatsPointer) {

        int channelIndex = 0;
        float[][] floatSamples = null;


        Pointer<Float>[] channels = floatsPointer.toArray();
        for (Pointer<Float> channel : channels) {

            float[] floatChannel;
            floatChannel = channel.getFloats();

            if (floatSamples == null) {
                floatSamples = new float[channels.length][floatChannel.length];
            }

            floatSamples[channelIndex] = floatChannel;
        }

        return floatSamples;
    }


    public RubberbandStretcher(AudioInputStream stream) {

        this((int) (stream.getFormat().getSampleRate()), stream.getFormat().getChannels(), (int) RubberbandLibrary.RubberBandOption.RubberBandOptionProcessRealTime.value, 1.0, 1.0);
        this.inputStream = stream;

    }

    public RubberbandStretcher(int sampleRate, int channels, int options, double timeScale, double pitchScale) {

        this.state = RubberbandLibrary.rubberband_new(sampleRate, channels, options, timeScale, pitchScale);


    }
}
