import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Actor implements Serializable{
    public final String username;
    public final String displayName;
    public final String fullId;

    public Map<String, Actor> followedActors = new HashMap<>();
    public Map<String, Actor> followingActors = new HashMap<>();

    public List<Activity> outbox = new ArrayList<>();
    public List<Activity> inbox = new ArrayList<>();

    Actor(String username, String displayName, String serverName) {
        this.username = username;
        this.displayName = displayName;
        this.fullId = ActivityPubUtilities.fullId(username, serverName);
    }

    public String getFullId() {
        return fullId;
    }

    public String getUsername() {
        return username;
    }

    public String getDisplayName() {
        return displayName;
    }

}
