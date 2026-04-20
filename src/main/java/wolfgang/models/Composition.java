package wolfgang.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Composition {
    private int id;

    private String title;

    private String description;

    private int tempo;

    private String accessType;

    private User owner;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private List<Track> tracks = new ArrayList<>();

    private Map<User, String> members = new HashMap<>();

    public Composition(){}

    public Composition(String title, int tempo, String accessType, User owner) {
        this.title = title;
        this.tempo = tempo;
        this.accessType = accessType;
        this.owner = owner;
    }

    public Composition(int id, String title, String description, int tempo, String accessType, User owner, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.tempo = tempo;
        this.accessType = accessType;
        this.owner = owner;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getTempo() {
        return tempo;
    }

    public void setTempo(int tempo) {
        this.tempo = tempo;
    }

    public String getAccessType() {
        return accessType;
    }

    public void setAccessType(String accessType) {
        this.accessType = accessType;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<Track> getTracks() {
        return tracks;
    }

    public void addTrack(Track track) {
        if (!tracks.contains(track)) {
            tracks.add(track);
        }
    }

    public void removeTrack(Track track) {
        tracks.remove(track);
    }

    public Map<User, String> getMembers() {
        return members;
    }

    public void addMember(User user, String role) {
        members.put(user, role);
    }

    public void removeMember(User user) {
        members.remove(user);
    }

    public String getRoleOf(User user) {
        return members.get(user);
    }
}
