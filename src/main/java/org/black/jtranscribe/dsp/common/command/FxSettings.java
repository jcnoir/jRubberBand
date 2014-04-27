package org.black.jtranscribe.dsp.common.command;

/**
 * Created by jcnoir on 27/04/14.
 */
public class FxSettings {
    private double speed;
    private double pitch;

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getPitch() {
        return pitch;
    }

    public void setPitch(double pitch) {
        this.pitch = pitch;
    }

    public FxSettings(double speed, double pitch) {
        this.speed = speed;
        this.pitch = pitch;
    }
}
