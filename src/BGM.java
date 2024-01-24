import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class BGM {
    Clip music;
    Clip sound;
    FloatControl gain;
    File[] bgm = new File[10];
    File[] sfx = new File[20];

    public BGM(){
        bgm[0] = new File("data/bgm/Wii Sports.wav");
        bgm[1] = new File("data/bgm/Sunshine Seaside.wav");
        bgm[2] = new File("data/bgm/Pokemon Center.wav");
        bgm[3] = new File("data/bgm/Petalburg Woods.wav");

        sfx[0] = new File("data/bgm/energyAtk.wav");
        sfx[1] = new File("data/bgm/atkContact.wav");
        sfx[2] = new File("data/bgm/bump.wav");
        sfx[3] = new File("data/bgm/step.wav");
        sfx[4] = new File("data/bgm/door.wav");
        sfx[5] = new File("data/bgm/potion.wav");
        sfx[6] = new File("data/bgm/spdUp.wav");
        sfx[7] = new File("data/bgm/gameOver.wav");
        sfx[8] = new File("data/bgm/start.wav");
        sfx[9] = new File("data/bgm/pickup.wav");
        sfx[10] = new File("data/bgm/spawn.wav");
        sfx[11] = new File("data/bgm/next.wav");
        sfx[12] = new File("data/bgm/doorUnlock.wav");
    }

    public void setMusic(int i) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        AudioInputStream musicbox = AudioSystem.getAudioInputStream(bgm[i]);
        music = AudioSystem.getClip();
        music.open(musicbox);
        gain = (FloatControl) music.getControl(FloatControl.Type.MASTER_GAIN);
    }

    public void setSfx(int i) throws LineUnavailableException, UnsupportedAudioFileException, IOException {
        AudioInputStream soundMaker = AudioSystem.getAudioInputStream(sfx[i]);
        sound = AudioSystem.getClip();
        sound.open(soundMaker);
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
            setMusic(i);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            throw new RuntimeException(e);
        }
        play();
        loop();
    }

    public void changeGain(float db){
        gain.setValue(db);
    }

    public void playSFX(int i){
        try {
            setSfx(i);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            throw new RuntimeException(e);
        }
        sound.start();
    }

}
