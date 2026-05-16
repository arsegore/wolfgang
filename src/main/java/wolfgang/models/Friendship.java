package wolfgang.models;

import java.time.LocalDateTime;

public class Friendship {

    private User friend;
    private LocalDateTime friendsSince; // date demande si pending, date d'acceptation si demande acceptée

    public Friendship(User friend, LocalDateTime friendsSince) {
        this.friend = friend;
        this.friendsSince = friendsSince;
    }

    public User getFriend() {
        return friend;
    }

    public LocalDateTime getFriendsSince() {
        return friendsSince;
    }
}