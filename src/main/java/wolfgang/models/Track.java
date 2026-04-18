package wolfgang.models;

public class Track {
    private int id;

    private Composition composition;

    private String name;

    private Instrument instrument;

    private String color;

    private int position;

    public Track() {}

    public Track(Composition composition, Instrument instrument, int position) {
        this.composition = composition;
        this.instrument = instrument;
        this.position = position;
    }

    public Track(int id, Composition composition, String name, Instrument instrument, String color, int position) {
        this.id = id;
        this.composition = composition;
        this.name = name;
        this.instrument = instrument;
        this.color = color;
        this.position = position;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Composition getComposition() {
        return composition;
    }

    public void setComposition(Composition composition) {
        this.composition = composition;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instrument getInstrument() {
        return instrument;
    }

    public void setInstrument(Instrument instrument) {
        this.instrument = instrument;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
