/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nnmpprototype1;

import com.xuggle.xuggler.*;
import com.xuggle.xuggler.io.IURLProtocolHandler;
import controllers.PlaybackQueueController;

import java.util.ArrayList;
import java.util.List;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

/**
 * The MediaPlayback Class is used to control all audio file playback in NNMP. Operations needed to play, stop,
 * and traverse a list of AudioFiles are implemented. Additionally, operations that allow for the adjustment of
 * playback volume are also present.
 */
public class MediaPlayback implements Observable {
    /** SourceDataLine used to hold decoded audio file information */
    private static SourceDataLine curLine = null;
    /** boolean representing playback status */
    private boolean isPlaying = false;
    /** boolean representing paused status */
    private boolean isPaused = false;
    /** boolean representing stopped status */
    private boolean isStopped = false;
    /** Thread reference used to access playback thread through the class */
    private Thread refThread;
    /** Reference to playback container object */
    private IContainer refContainer;
    /** int holding stream id value */
    private int refStreamId;
    /** FloatControl used in volume adjustment */
    private FloatControl volume;
    /** float representing current volume level */
    private float volLevel = -37;
    /** Reference to PlaybackQueueController object */
    private PlaybackQueueController pqc;
    /** boolean used to track song change during playback */
    private boolean changeData = false;
    /** int storing current playback index */
    private int currentIndex = 0;
    /** boolean representing completion of current AudioFile playback */
    private boolean isDone = false;
    /** List of observer objects */
    private List<Observer> observers = new ArrayList<>();
    /** Stores samples from each extracted packet during audio playback */
    private IAudioSamples samples;
    /** int holding value of offset in extracted packet's data */
    private int offset;
    /** int holding number of bytes decoded during audio playback */
    private int bytesDecoded;
    /** boolean representing initial playback start status */
    private volatile boolean startUp = true;
    /** boolean representing if pre-play seek has occurred */
    private boolean prePlaySeek;
    /** int holding the value of desired pre-play seek time */
    private int prePlaySeekTime;

    /**
     * Default Constructor
     */
    public MediaPlayback() {
        
    }

    /**
     * Method used to playback a series of AudioFiles stored in the PlaybackList object.
     * This operation occurs on a separate thread, which needs to be referenced to perform
     * relevant operations throughout the process of audio playback.
     */
    public synchronized void playAudio() {
        // Initialize paused and start up status
        isPaused = false;
        startUp = true;

        // Create new Thread
        Thread t = new Thread() {
            @Override
            public void run() {
                do {
                    // Initialize song change, completion and stopped status
                    changeData = false;
                    isDone = false;
                    isStopped = false;

                    // Set queued AudioFile file system location required for playback
                    String filename = pqc.getPlaybackList().getFileAt(currentIndex).getLocation();

                    // Create a Xuggler container object
                    IContainer container = IContainer.make();
                    refContainer = container;

                    // Open up the container
                    if (container.open(filename, IContainer.Type.READ, null) < 0) {
                        throw new IllegalArgumentException("could not open file: " + filename);
                    }

                    // Query how many streams the call to open found
                    int numStreams = container.getNumStreams();

                    // Iterate through the streams to find the first audio stream
                    int audioStreamId = -1;
                    IStreamCoder audioCoder = null;

                    for (int i = 0; i < numStreams; i++) {
                        // Find the stream object
                        IStream stream = container.getStream(i);

                        // Get the pre-configured decoder that can decode this stream;
                        IStreamCoder coder = stream.getStreamCoder();

                        if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_AUDIO) {
                            // Set audio stream and stream id values
                            audioStreamId = i;
                            refStreamId = audioStreamId;
                            audioCoder = coder;
                            break;
                        }
                    }

                    // No relevant audio stream found
                    if (audioStreamId == -1) {
                        throw new RuntimeException("could not find audio stream in container: " + filename);
                    }

                    //  Open audio decoder
                    if (audioCoder.open() < 0) {
                        throw new RuntimeException("could not open audio decoder for container: " + filename);
                    }

                    // Ready Java Sound System
                    SourceDataLine mLine = openJavaSound(audioCoder);

                    // Iterate through the container looking at each packet.
                    IPacket packet = IPacket.make();

                    // While packets remain and playback has not been stopped
                    while (!this.isInterrupted() && !isDone) {

                        // Put thread to sleep if playback paused
                        if (isPaused && !isStopped) {
                            try {
                                Thread.sleep(1);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        // If no more packets remain, toggle completion boolean
                        if (!isPaused && container.readNextPacket(packet) < 0) {
                            isDone = true;
                        }

                        // If playback has not been paused
                        if (!isPaused) {

                            if (packet.getStreamIndex() == audioStreamId) {
                                /*
                                 * A set of samples with the same number of channels as the
                                 * coder tells us is in this buffer is allocated.
                                 *
                                 * We also pass in a buffer size (1024)
                                 */
                                samples = IAudioSamples.make(1024, audioCoder.getChannels());

                                // Set packet offset
                                offset = 0;

                                // While data remains to be processed
                                while (!this.isInterrupted() && offset < packet.getSize()) {

                                    // Set bytes decoded
                                    bytesDecoded = audioCoder.decodeAudio(samples, packet, offset);
                                    if (bytesDecoded < 0) {
                                        break;
                                    }
                                    // Accumulate offset value
                                    offset += bytesDecoded;

                                    // Ensure complete set of samples is retrieved from decoder
                                    if (samples.isComplete() && !this.isInterrupted()) {
                                        try {
                                            // Play collected set of samples
                                            playJavaSound(samples, mLine);
                                        } catch (Exception e) {}
                                    }
                                }
                            } else {
                                // This packet isn't part of our audio stream, so we just silently drop it.
                                do {} while (false);
                            }
                        }
                    }

                    // Clean up after playback has been stopped
                    closeJavaSound(mLine);

                    if (audioCoder != null) {
                        audioCoder.close();
                        audioCoder = null;
                    }
                    if (container != null) {
                        container.close();
                        container = null;
                    }

                    // If playback has not been stopped
                    if (!this.isInterrupted()) {
                        // Increment current playback index
                        ++currentIndex;
                        // Inform GUI song is changing / update AudioFile information
                        changeSong();
                    }
                    curLine = null;
                    refContainer = null;
                    samples = null;
                    prePlaySeek = false;

                } while (isPlaying && currentIndex < pqc.getPlaybackList().getSize() && !this.isInterrupted());

                // Final clean up after playback stopped
                isPlaying = false;
                curLine = null;
                refContainer = null;
                samples = null;
                refThread = null;
            }
        };
        // Start and assign reference thread
        t.start();
        refThread = t;
    }

    /**
     * Method used to pause the playback of the currently active AudioFile.
     */
    public void pauseAudio() {
        // If playback active
        if (isPlaying) {
            // Toggle paused status
            isPaused = true;
            if (curLine != null) {
                // Stop processing SourceDataLine
                curLine.stop();
            }
        }
    }

    /**
     * Method used to resume audio playback after the pause operation has been called.
     */
    public void resumeAudio() {
        // If playback paused
        if (isPaused) {
            // Continue processing SourceDataLine
            curLine.start();
            // Toggle paused status
            isPaused = false;
        }
    }

    /**
     * Method used to stop active audio playback.
     */
    public void stopAudio() {
        // If SourceDataLine exists
        if (curLine != null) {
            // Toggle stopped status
            isStopped = true;
            isPlaying = false;

            // Close SourceDataLine
            curLine.close();
            // Interrupt playback thread
            refThread.interrupt();

            isPaused = false;
            curLine = null;
            refContainer = null;
        }
    }

    /**
     * Method used to play the next sequential AudioFile stored in the PlaybackList object.
     */
    public void playNextTrack() {
        // If valid index
        if (currentIndex < pqc.getPlaybackList().getSize() - 1) {

            // Close SourceDataLine and interrupt playback thread
            curLine.close();
            refThread.interrupt();

            // Nullify IContainer and toggle playing boolean
            refContainer = null;
            isPlaying = false;

            // Increment current playback index
            ++currentIndex;
        }
    }

    /**
     * Method used to play the previous AudioFile stored in the PlaybackList object.
     */
    public void playPreviousTrack() {
        // If valid index
        if (currentIndex > 0) {

            // Close SourceDataLine and interrupt playback thread
            curLine.close();
            refThread.interrupt();

            // Nullify IContainer and toggle playing boolean
            refContainer = null;
            isPlaying = false;

            // Decrement current playback index
            --currentIndex;
        }
    }

    /**
     * Method used to seek to a particular section of an AudioFile. This method takes input from the seekbar
     * located on the application's main GUI.
     *
     * @param seekVal - desired playback start time in seconds
     */
    public synchronized void seek(int seekVal) {
        // If playback is active
        if (isPlaying) {

            curLine.flush();

            // Calculate time base needed to seek to desired frame
            IRational timeBase = refContainer.getStream(refStreamId).getTimeBase();

            // Seek to desired position in AudioFile
            refContainer.seekKeyFrame(refStreamId, (long)(seekVal / timeBase.getDouble()), IURLProtocolHandler.SEEK_CUR);
        }
        // If pre-play seek operation
        else {
            // Toggle pre-play seek status
            prePlaySeek = true;
            // Assign desired pre-play seek time
            prePlaySeekTime = seekVal;
        }
    }

    /**
     * Method used to create a SourceDataLine containing audio file information collected by an
     * IStreamCoder object.
     *
     * @param aAudioCoder - IStreamCoder containing relevant data
     * @return SourceDataLine - data line required for audio playback
     */
    private synchronized SourceDataLine openJavaSound(IStreamCoder aAudioCoder) {
        // Extract audio format information
        AudioFormat audioFormat = new AudioFormat(aAudioCoder.getSampleRate(),
                (int) IAudioSamples.findSampleBitDepth(aAudioCoder.getSampleFormat()),
                aAudioCoder.getChannels(),
                true,
                false);

        // Create data line from format data
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
        try {
            // Create and assign reference to SourceDataLine
            SourceDataLine mLine = (SourceDataLine) AudioSystem.getLine(info);
            curLine = mLine;

            // Open data line
            mLine.open(audioFormat);
            // Start data line
            mLine.start();

            return mLine;

        } catch (LineUnavailableException e) {
            throw new RuntimeException("could not open audio line");
        }
    }

    /**
     * Method used to play a set of audio samples collect from the playback thread.
     *
     * @param aSamples - set of collected audio samples
     * @param mLine - current SourceDataLine
     */
    private synchronized void playJavaSound(IAudioSamples aSamples, SourceDataLine mLine) {
        // Dump collected samples to data line
        mLine.write(aSamples.getData().getByteArray(0, aSamples.getSize()), 0, aSamples.getSize());

        // If this is the beginning of initial playback sequence
        if (startUp) {

            // Toggle playing boolean
            isPlaying = true;

            if (prePlaySeek) {
                seek(prePlaySeekTime);
            }

            // Notify GUI on main thread to update relevant AudioFile information
            notifyObserver();

            // Toggle initial startup boolean
            startUp = false;
        }
        // Set volume level / check for adjustments
        setVolume(volLevel);
    }

    /**
     * Method used for the safe destruction of the currently active SourceDataLine object.
     *
     * @param mLine - active SourceDataLine
     */
    private void closeJavaSound(SourceDataLine mLine) {
        // If data line exists
        if (mLine != null) {
            // Wait for data line to clear all samples
            mLine.drain();
            // Close the data line
            mLine.close();
            mLine = null;
        }
    }

    /**
     * Method used to return active playback status.
     *
     * @return boolean - playback status
     */
    public boolean active() {
        return isPlaying;
    }

    /**
     * Method used to set the volume level before or during audio playback.
     *
     * @param level - desired volume level
     */
    public void setVolume(float level) {
        volLevel = level;
        
        if (isPlaying && curLine.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            volume = (FloatControl) curLine.getControl(FloatControl.Type.MASTER_GAIN);
            // Set volume level
            volume.setValue(level);
        }
    }

    /**
     * Method responsible for assigning a reference to the PlaybackQueueController.
     *
     * @param pqc - PlaybackQueueController
     */
    public void setPlaybackQueueController(PlaybackQueueController pqc) {
        this.pqc = pqc;
    }

    /**
     * Method used to set the current playback index.
     *
     * @param index - desired playback index
     */
    public void setCurrentIndex(int index) {
        currentIndex = index;
    }

    /**
     * Method used to set the song changed status.
     *
     * @param val - song changed status
     */
    public void setChangeData(boolean val) {
        changeData = val;
    }

    /**
     * Accessor method that return the value of the boolean
     * representing song changed status.
     *
     * @return boolean - status of song changed
     */
    public boolean getChangeData() {
        return changeData;
    }

    /**
     * Accessor method used to return the current playback index.
     *
     * @return int - current playback index
     */
    public int getCurrentIndex() {
        return currentIndex;
    }

    /**
     * Accessor method used to return playback paused status.
     *
     * @return boolean - playback paused status
     */
    public boolean getIsPaused() {
        return isPaused;
    }

    /**
     * Accoessor method used to return the playback stopped status.
     *
     * @return boolean - playback stopped status
     */
    public boolean getIsStopped() {
        return isStopped;
    }

    /**
     * Accessor method used to return the current volume level.
     *
     * @return float - current volume level
     */
    public float getVolLevel() {
        return volLevel;
    }

    /**
     * Observable Interface method implemented to make an addition to the list of observers.
     *
     * @param o - Observer to be added
     */
    @Override
    public void addObserver(Observer o) {
        observers.add(o);
    }

    /**
     * Observable Interface method implemented to remove an observer from the list of observers.
     *
     * @param o - Observer to be removed
     */
    @Override
    public void removeObserver(Observer o) {
        observers.remove(o);
    }

    /**
     * Observable Interface method used to notify all observers of a song change in the PlaybackList
     */
    @Override
    public void notifyObserver() {
        for (Observer obs : observers) {
            obs.update();
        }
    }

    /**
     * Method used to notify the main GUI that the currently playing song has been changed.
     * GUI components will be updated accordingly.
     */
    private void changeSong() {
        changeData = true;
        notifyObserver();
    }

    /**
     * Accessor method used to retrieve pre-play seek status
     *
     * @return boolean - pre-play seek status
     */
    public boolean isPrePlaySeek() {
        return prePlaySeek;
    }
}
