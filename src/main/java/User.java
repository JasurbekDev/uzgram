import java.util.List;

public class User {
    private String nickname;
    private Long chatId;
    private Long points;
    private String level;
    private int friends;
    private String photoId;
    private boolean viewUser = false;

    public User(String nickname, Long chatId, Long points, String level, int friends) {
        this.nickname = nickname;
        this.chatId = chatId;
        this.points = points;
        this.level = level;
        this.friends = friends;
    }

    public String getNickname() {
        return nickname;
    }

    public Long getChatId() {
        return chatId;
    }

    public Long getPoints() {
        return points;
    }

    public String getLevel() {
        return level;
    }

    public int getFriends() {
        return friends;
    }

    public String getPhotoId() {
        return photoId;
    }

    public void setPhotoId(String photoId) {
        this.photoId = photoId;
    }

    public boolean isViewUser() {
        return viewUser;
    }

    public void setViewUser(boolean viewUser) {
        this.viewUser = viewUser;
    }
}
