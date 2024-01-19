import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class BGM {
    Clip music;
    File[] bgm = new File[10];

    public BGM(){
        bgm[0] = new File("data/bgm/Wii Sports.wav");
        bgm[1] = new File("data/bgm/Sunshine Seaside.wav");
        bgm[2] = new File("data/bgm/Pokemon Center.wav");
    }

    public void set(int i) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        AudioInputStream musicbox = AudioSystem.getAudioInputStream(bgm[i]);
        music = AudioSystem.getClip();
        music.open(musicbox);
    }

    public void play(){
        music.start();
    }

    public void loop(){
        music.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void stop(){
        music.stop();
    }

    public void changeTrack(int i){
            stop();
        try {
            set(i);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            throw new RuntimeException(e);
        }
        play();
        loop();
    }

}
