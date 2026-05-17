package wolfgang.models;

public class Instrument {
    private int id;

    private String name;

    private String waveType;

    public Instrument(){}

    public Instrument(String name) {
        this.name = name;
    }

    public Instrument(int id, String name) {
        this.id = id;
        this.name = name;
        this.waveType = "sine";
    }

    public Instrument(int id, String name, String waveType) {
        this.id = id;
        this.name = name;
        this.waveType = waveType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWaveType() {
        return waveType != null ? waveType : "sine";
    }

    public void setWaveType(String waveType) {
        this.waveType = waveType;
    }
}
