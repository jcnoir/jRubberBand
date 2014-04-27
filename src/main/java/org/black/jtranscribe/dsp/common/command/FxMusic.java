package org.black.jtranscribe.dsp.common.command;

import org.black.jtranscribe.dsp.common.MusicProvider;

/**
 * Created by jcnoir on 27/04/14.
 */
public class FxMusic {

    private MusicProvider musicProvider;

    public MusicProvider getMusicProvider() {
        return musicProvider;
    }

    public void setMusicProvider(MusicProvider musicProvider) {
        this.musicProvider = musicProvider;
    }

    public FxMusic(MusicProvider musicProvider) {
        this.musicProvider = musicProvider;
    }

}
