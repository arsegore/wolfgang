package wolfgang.models;

import java.time.LocalDateTime;

public class Information {
    private int id;

    private String title;

    private LocalDateTime createdAt;

    private String description;

    public Information(String t, String d) {
        title = t;
        description = d;
    }

    public Information(int i, String t, String d, LocalDateTime ca) {
        id = i;
        title = t;
        description = d;
        createdAt = ca;
    }

    public int getId() {
        return id;
    }

    public void setId(int i) {
        id = i;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String t) {
        title = t;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String d) {
        description = d;
    }
}
