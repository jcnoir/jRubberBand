package org.black.jtranscribe.dsp.common.command;

/**
 * Created by jcnoir on 27/04/14.
 */
public class Loop {

    private long loopStart;
    private long loopEnd;
    private boolean loopEnabled;

    public long getLoopStart() {
        return loopStart;
    }

    public void setLoopStart(long loopStart) {
        this.loopStart = loopStart;
    }

    public long getLoopEnd() {
        return loopEnd;
    }

    public void setLoopEnd(long loopEnd) {
        this.loopEnd = loopEnd;
    }

    public boolean isLoopEnabled() {
        return loopEnabled;
    }

    public void setLoopEnabled(boolean loopEnabled) {
        this.loopEnabled = loopEnabled;
    }

    public Loop(long loopStart, long loopEnd, boolean loopEnabled) {
        this.loopStart = loopStart;
        this.loopEnd = loopEnd;
        this.loopEnabled = loopEnabled;
    }

    public Loop(boolean loopEnabled) {
        this.loopEnabled = loopEnabled;
    }

    @Override
    public String toString() {
        return "Loop{" +
                "loopStart=" + loopStart +
                ", loopEnd=" + loopEnd +
                ", loopEnabled=" + loopEnabled +
                '}';
    }
}
