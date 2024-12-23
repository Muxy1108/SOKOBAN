package SOKOBAN.view;

import java.io.File;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;

class Sound {
    String path = new String("musics\\");
    String file = new String("nor.mid");
    Sequence seq;
    Sequencer midi;
    boolean sign;

    Sound() {
    }

    void loadSound() {
        try {
            this.seq = MidiSystem.getSequence(new File(this.path + this.file));
            this.midi = MidiSystem.getSequencer();
            this.midi.open();
            this.midi.setSequence(this.seq);
            this.midi.start();
            this.midi.setLoopCount(-1);
        } catch (Exception var2) {
            Exception ex = var2;
            ex.printStackTrace();
        }

        this.sign = true;
    }

    void mystop() {
        this.midi.stop();
        this.midi.close();
        this.sign = false;
    }

    boolean isplay() {
        return this.sign;
    }

    void setMusic(String e) {
        this.file = e;
    }
}
