package sound;

import javax.sound.sampled.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Manages all in-game sound effects using javax.sound.sampled.
 * All sounds are synthesized programmatically — no audio files needed.
 * Sounds play on a background thread pool to avoid UI lag.
 *
 * Sound effects:
 *  CORRECT    — ascending chime
 *  TOO_HIGH   — descending blip
 *  TOO_LOW    — low buzz
 *  CLICK      — short tick
 *  GAME_OVER  — descending chord
 *  WIN_FANFARE — ascending multi-note fanfare
 */
public class SoundManager {

    // ── Singleton ─────────────────────────────────────────────────────────────

    private static SoundManager instance;
    private boolean soundEnabled = true;
    private final ExecutorService pool = Executors.newCachedThreadPool(r -> {
        Thread t = new Thread(r, "SoundThread");
        t.setDaemon(true);
        return t;
    });

    private SoundManager() {}

    public static SoundManager getInstance() {
        if (instance == null) instance = new SoundManager();
        return instance;
    }

    // ── Sound IDs ─────────────────────────────────────────────────────────────

    public enum Sound {
        CORRECT, TOO_HIGH, TOO_LOW, CLICK, GAME_OVER, WIN_FANFARE
    }

    // ── Public API ────────────────────────────────────────────────────────────

    public void play(Sound sound) {
        if (!soundEnabled) return;
        pool.execute(() -> {
            try {
                byte[] pcm = synthesize(sound);
                AudioFormat format = new AudioFormat(44100, 16, 1, true, false);
                AudioInputStream ais = new AudioInputStream(
                    new java.io.ByteArrayInputStream(pcm),
                    format,
                    pcm.length / 2L);
                Clip clip = AudioSystem.getClip();
                clip.open(ais);
                clip.start();
                // Wait for clip to finish then release
                Thread.sleep(clip.getMicrosecondLength() / 1000 + 50);
                clip.close();
            } catch (Exception e) {
                // Sound failure is non-critical — log but continue
                utils.Logger.getInstance().warn("Sound error: " + e.getMessage());
            }
        });
    }

    public void setSoundEnabled(boolean enabled) { this.soundEnabled = enabled; }
    public boolean isSoundEnabled() { return soundEnabled; }

    // ── PCM Synthesis ─────────────────────────────────────────────────────────

    private byte[] synthesize(Sound sound) {
        return switch (sound) {
            case CORRECT     -> concat(tone(880, 150, 0.5), tone(1047, 200, 0.5));
            case TOO_HIGH    -> descTone(600, 300, 0.35);
            case TOO_LOW     -> tone(200, 220, 0.3);
            case CLICK       -> tone(1200, 50, 0.2);
            case GAME_OVER   -> concat(descTone(400, 600, 0.5), tone(200, 400, 0.4));
            case WIN_FANFARE -> buildFanfare();
        };
    }

    // Concatenate two byte arrays
    private byte[] concat(byte[] a, byte[] b) {
        if (a == null) return b;
        byte[] result = new byte[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

    // Overloaded + operator simulation
    private byte[] tone(double freq, int ms, double amp) {
        return tone(freq, ms, amp, false);
    }

    private byte[] descTone(double startFreq, int ms, double amp) {
        int sampleRate = 44100;
        int numSamples = sampleRate * ms / 1000;
        byte[] buf = new byte[numSamples * 2];
        for (int i = 0; i < numSamples; i++) {
            double progress = (double) i / numSamples;
            double freq = startFreq * (1.0 - 0.5 * progress);
            double val = amp * Math.sin(2 * Math.PI * freq * i / sampleRate);
            double envelope = 1.0 - progress * 0.6; // slight fade
            short sample = (short)(val * envelope * Short.MAX_VALUE);
            buf[i * 2]     = (byte)(sample & 0xFF);
            buf[i * 2 + 1] = (byte)((sample >> 8) & 0xFF);
        }
        return buf;
    }

    private byte[] tone(double freq, int ms, double amp, boolean fadeOut) {
        int sampleRate = 44100;
        int numSamples = sampleRate * ms / 1000;
        byte[] buf = new byte[numSamples * 2];
        for (int i = 0; i < numSamples; i++) {
            double val = amp * Math.sin(2 * Math.PI * freq * i / sampleRate);
            double env = 1.0;
            if (fadeOut) env = 1.0 - (double) i / numSamples;
            short sample = (short)(val * env * Short.MAX_VALUE);
            buf[i * 2]     = (byte)(sample & 0xFF);
            buf[i * 2 + 1] = (byte)((sample >> 8) & 0xFF);
        }
        return buf;
    }

    private byte[] buildFanfare() {
        // C5 E5 G5 C6  ascending major chord
        double[] freqs = {523.25, 659.25, 783.99, 1046.5};
        int[]    durations = {120, 120, 120, 400};
        byte[] result = new byte[0];
        for (int i = 0; i < freqs.length; i++) {
            result = concat(result, tone(freqs[i], durations[i], 0.5, i == freqs.length - 1));
        }
        return result;
    }

    // Helper operator-overload simulation using byte[] returns
    private byte[] operator(byte[] a, byte[] b) { return concat(a, b); }
}
