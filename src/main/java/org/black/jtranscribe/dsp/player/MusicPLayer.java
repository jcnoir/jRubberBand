package org.black.jtranscribe.dsp.player;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.black.jtranscribe.dsp.common.AudioCommon;
import org.black.jtranscribe.dsp.common.MusicConsumer;
import org.black.jtranscribe.dsp.common.MusicProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;
import java.io.IOException;

/**
 * User: jcnoir
 * Date: 15/07/12
 */
@Component
public class MusicPLayer implements MusicConsumer {

    @Autowired
    private EventBus eventBus;

    private static int DEFAULT_EXTERNAL_BUFFER_SIZE = 128000;

    private static final Logger log = LoggerFactory.getLogger(MusicPLayer.class);

    private String mixer;
    int internalBufferSize = AudioSystem.NOT_SPECIFIED;
    int externalBufferSize = DEFAULT_EXTERNAL_BUFFER_SIZE;

    private long startFrame = 0;
    private long readFrames = 0;

    private AudioInputStream inputStream;

    public MusicPLayer() {
    }

    @PostConstruct
    private void init() {
        eventBus.register(this);
    }

    @Override
    @Subscribe
    public void listen(MusicProvider data) {
        inputStream = data.getMusic();
        log.debug("Starting music provider read ...");
        SourceDataLine line = AudioCommon.getSourceDataLine(mixer, data.getMusic().getFormat(), internalBufferSize);


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


        log.debug("Audio line configured, reading data stream ...");

        log.debug("Music length = {} frames", data.getMusic().getFrameLength());

        int readBytes = 0;
        externalBufferSize = (data.getMusic().getFormat().getSampleSizeInBits() / 8) * 44100 * 2;
        byte[] buffer = new byte[externalBufferSize];

        log.debug("Stream length in frames : " + data.getMusic().getFrameLength());

        while (readBytes != -1) {
            try {
                readBytes = data.getMusic().read(buffer, 0, buffer.length);
            } catch (IOException e) {
                log.error("Audio stream read failure : {}", e);
            }
            if (readBytes >= 0) {
                int writtenBytes = line.write(buffer, 0, readBytes);
                readFrames = readFrames + (writtenBytes / data.getMusic().getFormat().getFrameSize());
               // log.debug("{}/{} read frames", readFrames, data.getMusic().getFrameLength());
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

        log.debug("Data stream read is over ...");

        line.drain();

        /*
           *	All data are played. We can close the shop.
           */
        line.close();

        data.close();

        log.debug("Data stream resources closed");
    }

    @Override
    public long getPosition() {
        return readFrames;
    }
}
