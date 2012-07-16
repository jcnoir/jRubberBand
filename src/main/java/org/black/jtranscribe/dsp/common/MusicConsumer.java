package org.black.jtranscribe.dsp.common;

/**
 * User: jcnoir
 * Date: 15/07/12
 */
public interface MusicConsumer extends MusicHandler {

    public void listen(MusicProvider data);

}
