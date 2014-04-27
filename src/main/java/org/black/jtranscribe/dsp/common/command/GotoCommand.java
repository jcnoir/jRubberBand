package org.black.jtranscribe.dsp.common.command;

/**
 * Created by jcnoir on 27/04/14.
 */
public class GotoCommand {

    private long framePosition;

    public long getFramePosition() {
        return framePosition;
    }

    public void setFramePosition(long framePosition) {
        this.framePosition = framePosition;
    }

    public GotoCommand(long framePosition) {
        this.framePosition = framePosition;
    }
}
