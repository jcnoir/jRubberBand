package org.black.jtranscribe.dsp.common;

import javax.sound.sampled.AudioInputStream;

/**
 * User: jcnoir
 * Date: 15/07/12
 */
public interface MusicProvider extends MusicHandler {

    public AudioInputStream getMusic();
}
