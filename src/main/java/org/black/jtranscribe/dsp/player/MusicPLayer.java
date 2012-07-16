package org.black.jtranscribe.dsp.player;

import org.black.jtranscribe.dsp.common.AudioCommon;
import org.black.jtranscribe.dsp.common.MusicConsumer;
import org.black.jtranscribe.dsp.common.MusicProvider;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;
import java.io.IOException;

/**
 * User: jcnoir
 * Date: 15/07/12
 */
public class MusicPLayer implements MusicConsumer {

    private static int DEFAULT_EXTERNAL_BUFFER_SIZE = 128000;

    private String strMixerName;
    int nInternalBufferSize = AudioSystem.NOT_SPECIFIED;
    int nExternalBufferSize = DEFAULT_EXTERNAL_BUFFER_SIZE;


    @Override
    public void listen(MusicProvider data) {
        SourceDataLine line = AudioCommon.getSourceDataLine(strMixerName, data.getMusic().getFormat(), nInternalBufferSize);


        /*
        *	Still not enough. The line now can receive data,
        *	but will not pass them on to the audio output device
        *	(which means to your sound card). This has to be
        *	activated.
        */
        line.start();

        /*
           *	Ok, finally the line is prepared. Now comes the real
           *	job: we have to write data to the line. We do this
           *	in a loop. First, we read data from the
           *	AudioInputStream to a buffer. Then, we write from
           *	this buffer to the Line. This is done until the end
           *	of the file is reached, which is detected by a
           *	return value of -1 from the read method of the
           *	AudioInputStream.
           */
        int nBytesRead = 0;
        byte[] abData = new byte[nExternalBufferSize];
        while (nBytesRead != -1) {
            try {
                nBytesRead = data.getMusic().read(abData, 0, abData.length);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (nBytesRead >= 0) {
                int nBytesWritten = line.write(abData, 0, nBytesRead);
            }
        }


        /*
           *	Wait until all data is played.
           *	This is only necessary because of the bug noted below.
           *	(If we do not wait, we would interrupt the playback by
           *	prematurely closing the line and exiting the VM.)
           *
           *	Thanks to Margie Fitch for bringing me on the right
           *	path to this solution.
           */
        line.drain();

        /*
           *	All data are played. We can close the shop.
           */
        line.close();
    }
}
