package wolfgang.models;

public class CompositionMember {
    private Composition composition;

    private User user;

    private String role;

    public CompositionMember(){}

    public CompositionMember(Composition composition, User user, String role) {
        this.composition = composition;
        this.user = user;
        this.role = role;
    }

    public Composition getComposition() {
        return composition;
    }

    public void setComposition(Composition composition) {
        this.composition = composition;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
