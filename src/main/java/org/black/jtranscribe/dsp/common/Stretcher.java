package org.black.jtranscribe.dsp.common;

/**
 * User: jcnoir
 * Date: 15/07/12
 */
public interface Stretcher extends MusicFX {

    public double getSpeed();

    public void setSpeed(double speed);

    public double getPitch();

    public void setPitch(double pitch);

}
