import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;

public class AudInputLine {
    public Mixer mixer;
    public Line.Info lineInfo;
    public String name;

    AudInputLine() {
    }

    public String toString() {
        return this.name;
    }
}
