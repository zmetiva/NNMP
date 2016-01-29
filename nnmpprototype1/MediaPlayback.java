/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nnmpprototype1;

import com.xuggle.mediatool.MediaToolAdapter;
import com.xuggle.mediatool.event.IAudioSamplesEvent;
import com.xuggle.xuggler.IAudioSamples;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;
import java.nio.ShortBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

/**
 *
 * @author zmmetiva, tmetiva
 */
public class MediaPlayback {
    
    /**
     * The audio line we'll output sound to; it'll be the default audio device on your system if available
     */
    
    private static SourceDataLine curLine = null;
    private volatile boolean isPlaying = false; 
    private Thread refThread;
    private FloatControl volume;
    private float volLevel = -37;
    private PlaybackQueueController pqc;
    private volatile boolean changeData = false;
    private int currentIndex = 0;
    private volatile boolean isPaused = false;
    private volatile boolean isDone = false;
    
    public MediaPlayback() {
        
    }
    
    /**
     * Takes a media container (file) as the first argument, opens it,
     * opens up the default audio device on your system, and plays back the audio.
     *  
     * @param args Must contain one string which represents a filename
     * @throws java.lang.InterruptedException
     */
    public void playAudio() {
        isPlaying = true;
        
        Thread t = new Thread() {
            @Override
            public void run() {

                while (isPlaying && currentIndex < pqc.getPlaybackList().getSize() && !this.isInterrupted()) {

                    changeData = false;
                    isDone = false;

                    //IMediaWriter writer = ToolFactory.makeWriter("test1.wav");

                    //String filename = mediaFile;
                    String filename = pqc.getPlaybackList().getFileAt(currentIndex).getLocation();

                    // Create a Xuggler container object
                    IContainer container = IContainer.make();

                    // Open up the container
                    if (container.open(filename, IContainer.Type.READ, null) < 0) {
                        throw new IllegalArgumentException("could not open file: " + filename);
                    }

                    // query how many streams the call to open found
                    int numStreams = container.getNumStreams();

                    // and iterate through the streams to find the first audio stream
                    int audioStreamId = -1;
                    IStreamCoder audioCoder = null;
                    for (int i = 0; i < numStreams; i++) {
                        // Find the stream object
                        IStream stream = container.getStream(i);

                        // Get the pre-configured decoder that can decode this stream;
                        IStreamCoder coder = stream.getStreamCoder();

                        if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_AUDIO) {
                            audioStreamId = i;
                            audioCoder = coder;
                            break;
                        }
                    }
                    if (audioStreamId == -1) {
                        throw new RuntimeException("could not find audio stream in container: " + filename);
                    }

                    /*
                     * Now we have found the audio stream in this file.  Let's open up our decoder so it can
                     * do work.
                     */
                    if (audioCoder.open() < 0) {
                        throw new RuntimeException("could not open audio decoder for container: " + filename);
                    }

                    /*
                     * And once we have that, we ask the Java Sound System to get itself ready.
                     */
                    SourceDataLine mLine = openJavaSound(audioCoder);

                    //writer.addAudioStream(0, 0, audioCoder.getChannels(), audioCoder.getSampleRate());

                    /*
                     * Now, we start walking through the container looking at each packet.
                     */
                    IPacket packet = IPacket.make();

                    while (!this.isInterrupted() && /*container.readNextPacket(packet) >= 0*/ !isDone) {
                        /*
                         * Now we have a packet, let's see if it belongs to our audio stream
                         */

                        if (!isPaused && container.readNextPacket(packet) < 0) {
                            isDone = true;
                        }

                        if (!isPaused) {

                            if (packet.getStreamIndex() == audioStreamId) {
                            /*
                             * We allocate a set of samples with the same number of channels as the
                             * coder tells us is in this buffer.
                             * 
                             * We also pass in a buffer size (1024 in our example), although Xuggler
                             * will probably allocate more space than just the 1024 (it's not important why).
                             */
                                IAudioSamples samples = IAudioSamples.make(1024, audioCoder.getChannels());

                            /*
                             * A packet can actually contain multiple sets of samples (or frames of samples
                             * in audio-decoding speak).  So, we may need to call decode audio multiple
                             * times at different offsets in the packet's data.  We capture that here.
                             */
                                int offset = 0;

                            /*
                             * Keep going until we've processed all data
                             */

                                while (!this.isInterrupted() && offset < packet.getSize()) {
                                    int bytesDecoded = audioCoder.decodeAudio(samples, packet, offset);
                                    if (bytesDecoded < 0) {
                                        //throw new RuntimeException("got error decoding audio in: " + filename);
                                        break;
                                    }
                                    offset += bytesDecoded;
                                /*
                                 * Some decoder will consume data in a packet, but will not be able to construct
                                 * a full set of samples yet.  Therefore you should always check if you
                                 * got a complete set of samples from the decoder
                                 */
                                    if (samples.isComplete() && !this.isInterrupted()) {
                                        //playJavaSound(samples);
                                        try {
                                            //writer.encodeAudio(0, samples);
                                            playJavaSound(samples, mLine);
                                        } catch (Exception e) {
                                            System.out.println(e);
                                        }
                                    }
                                }
                            } else {
                            /*
                             * This packet isn't part of our audio stream, so we just silently drop it.
                             */
                                do {
                                } while (false);
                            }
                        }
                    }

                    /*
                     * Technically since we're exiting anyway, these will be cleaned up by 
                     * the garbage collector... but we're going to show how to clean up.
                     */
                    closeJavaSound(mLine);
                    //writer.close();

                    if (audioCoder != null) {
                        audioCoder.close();
                        audioCoder = null;
                    }
                    if (container != null) {
                        container.close();
                        container = null;
                    }
                    System.out.println("DONE!");
                    
                    if (!this.isInterrupted()) {

                        changeData = true;
                        try {
                            Thread.sleep(5);
                        } catch (InterruptedException ex) {

                        }
                        ++currentIndex;
                    }
                }
                isPlaying = false;
                curLine = null;
            }
        };
        t.start();
        refThread = t;
    }

    public void pauseAudio() {
        if (isPlaying) {
            isPaused = true;
            if (curLine != null) {
                curLine.stop();
            }
        }
    }

    public void resumeAudio() {
        if (isPaused) {
            curLine.start();
            isPaused = false;
        }
    }

    public void stopAudio() {
        if (curLine != null) {
            isPlaying = false;
            //curLine.stop();
            //curLine.flush();
            curLine.close();
            refThread.interrupt();
            isPaused = false;
            curLine = null;
        }
    }

    public void playNextTrack() {
        if (currentIndex < pqc.getPlaybackList().getSize() - 1) {
            //curLine.stop();
            //curLine.flush();
            curLine.close();
            refThread.interrupt();
            isPlaying = false;
            ++currentIndex;
        }
    }
    
    public void playPreviousTrack() {
        if (currentIndex > 0) {
            //curLine.stop();
            //curLine.flush();
            curLine.close();
            refThread.interrupt();
            isPlaying = false;
            --currentIndex;
        }
    }
    
    private SourceDataLine openJavaSound(IStreamCoder aAudioCoder) {
        AudioFormat audioFormat = new AudioFormat(aAudioCoder.getSampleRate(),
                (int) IAudioSamples.findSampleBitDepth(aAudioCoder.getSampleFormat()),
                aAudioCoder.getChannels(),
                true, /* xuggler defaults to signed 16 bit samples */
                false);
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
        try {
            SourceDataLine mLine = (SourceDataLine) AudioSystem.getLine(info);
            curLine = mLine;
            /**
             * if that succeeded, try opening the line.
             */
            mLine.open(audioFormat);
            /**
             * And if that succeed, start the line.
             */
            mLine.start();
            setVolume(volLevel);
            
            return mLine;
        } catch (LineUnavailableException e) {
            throw new RuntimeException("could not open audio line");
        }
    }

    private void playJavaSound(IAudioSamples aSamples, SourceDataLine mLine) {
        /**
         * We're just going to dump all the samples into the line.
         */
        byte[] rawBytes = aSamples.getData().getByteArray(0, aSamples.getSize());
        mLine.write(rawBytes, 0, aSamples.getSize());
        //System.out.println("playjavasound");
        
    }

    private void closeJavaSound(SourceDataLine mLine) {
        if (mLine != null) {
            /*
             * Wait for the line to finish playing
             */
            mLine.drain();
            /*
             * Close the line.
             */
            mLine.close();
            mLine = null;
        }
    }
    
    public boolean lineRunning() {
        if (curLine != null) {
            return curLine.isRunning();
        }
        else {
            return false;
        }
    }
    
    private class VolumeAdjustMediaTool extends MediaToolAdapter {
        
        // the amount to adjust the volume by
        private double mVolume;
        
        public VolumeAdjustMediaTool(double volume) {
            mVolume = volume;
        }

        @Override
        public void onAudioSamples(IAudioSamplesEvent event) {
            
            // get the raw audio bytes and adjust it's value
            ShortBuffer buffer = 
               event.getAudioSamples().getByteBuffer().asShortBuffer();
            
            for (int i = 0; i < buffer.limit(); ++i) {
                buffer.put(i, (short) (buffer.get(i) * mVolume));
            }

            // call parent which will pass the audio onto next tool in chain
            super.onAudioSamples(event);
        }
    }
    
    public boolean active() {
        return isPlaying;
    }
    
    public void setVolume(float level) {
        volLevel = level;
        
        if (isPlaying && curLine.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            volume = (FloatControl) curLine.getControl(FloatControl.Type.MASTER_GAIN);
            volume.setValue(level);
        }
    }
    
    public void setPlaybackQueueController(PlaybackQueueController pqc) {
        this.pqc = pqc;
    }
    
    public void setCurrentIndex(int index) {
        currentIndex = index;
    }
    
    public void setChangeData(boolean val) {
        changeData = val;
    }
    
    public boolean getChangeData() {
        return changeData;
    }
    
    public int getCurrentIndex() {
        return currentIndex;
    }

    public boolean getIsPaused() {
        return isPaused;
    }

}
