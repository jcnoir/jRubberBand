package org.black.jtranscribe.dsp.fx.tarsos;

import be.hogent.tarsos.dsp.AudioEvent;
import be.hogent.tarsos.dsp.AudioProcessor;

/**
 * User: jcnoir
 * Date: 24/07/12
 */
public class MultiChannelStretcher implements AudioProcessor {
    @Override
    public boolean process(AudioEvent audioEvent) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void processingFinished() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    private AudioEvent[] deinterleave(AudioEvent audioEvent) {

        AudioEvent[] audioEvents;

        // audioEvent = new AudioEvent[audioEvent.get];
        return null;
    }

    private AudioEvent interleave(AudioEvent[] audioEvents) {

        return null;
    }


}
