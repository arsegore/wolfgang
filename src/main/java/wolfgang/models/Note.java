package wolfgang.models;

public class Note {
    private int id;

    private Track track;

    private int pitch;

    private float startBeat;

    private float duration;

    private int velocity;

    public Note() {}

    public Note(Track track, int pitch, float startBeat, float duration) {
        this.track = track;
        this.pitch = pitch;
        this.startBeat = startBeat;
        this.duration = duration;
    }

    public Note(int id, Track track, int pitch, float startBeat, float duration, int velocity) {
        this.id = id;
        this.track = track;
        this.pitch = pitch;
        this.startBeat = startBeat;
        this.duration = duration;
        this.velocity = velocity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }

    public int getPitch() {
        return pitch;
    }

    public void setPitch(int pitch) {
        this.pitch = pitch;
    }

    public float getStartBeat() {
        return startBeat;
    }

    public void setStartBeat(float startBeat) {
        this.startBeat = startBeat;
    }

    public float getDuration() {
        return duration;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

    public int getVelocity() {
        return velocity;
    }

    public void setVelocity(int velocity) {
        this.velocity = velocity;
    }
}
