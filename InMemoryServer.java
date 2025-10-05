import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class InMemoryServer implements Server {

    private String serverName;

    private Map<String, Actor> actors = new HashMap<>();

    public InMemoryServer(String name) {
        this.serverName = name;
    }


    @Override
    public String getName() {
        return this.serverName;
    }

    @Override
    public Actor createActor(String username, String displayName) throws ActivityPubException {
        if (actors.containsKey(username)) {
            throw new ActivityPubException("Actor with this username already exists on this server!!");
        }
        String fullId = ActivityPubUtilities.fullId(username, this.serverName);
        Actor actor = new Actor(username, displayName, fullId);
        actors.put(username, actor);
        return actor;
    }

    @Override
    public Optional<Actor> getActor(String input) {
        if (input.startsWith("@")) {
            String[] credentials = input.split("@");
            if (credentials.length == 3 && credentials[2].equals(this.serverName)) {
                return Optional.ofNullable(actors.get(credentials[1]));
            }
        } else {
            return Optional.ofNullable(actors.get(input));
        }
        return Optional.empty();
    }

    @Override
    public List<Actor> listAllActors() {
        return new ArrayList<>(actors.values());
    }

    @Override
    public boolean deleteActor(String username) {
        return actors.remove(username) != null;

    }

    @Override
    public boolean follow(String followerId, String targetId) throws ActivityPubException {
        Actor target = actors.get(targetId);
        Actor follower = actors.get(followerId);

        if (follower == null || target == null) 
            throw new ActivityPubException("Follower or target actor not found");
        if (target.followedActors.containsKey(followerId)) return false;

        target.followedActors.put(followerId, follower);
        follower.followingActors.put(targetId, target);

        return true;
    }

    @Override
    public boolean addFollower(String followerId, String targetId) throws ActivityPubException {
        Actor target = actors.get(targetId);
        Actor follower = actors.get(followerId);

        if (follower == null || target == null)
            throw new ActivityPubException("Follower or target actor not found");

        if (target.followedActors.containsKey(followerId)) return false;

        target.followedActors.put(followerId, follower);
        follower.followingActors.put(targetId, target);

        return true;
    }

    @Override
    public boolean unfollow(String followerId, String targetId) throws ActivityPubException {
        Actor target = actors.get(targetId);
        Actor follower = actors.get(followerId);

        if (follower == null || target == null) 
            throw new ActivityPubException("Follower or target actor not found");
        if (!target.followedActors.containsKey(followerId)) return false;

        target.followedActors.remove(followerId);

        if (follower.followingActors.containsKey(targetId))
            follower.followingActors.remove(targetId);

        return true;
    }

    @Override
    public boolean removeFollower(String followerId, String targetId) throws ActivityPubException {
        Actor target = actors.get(targetId);
        Actor follower = actors.get(followerId);

        if (follower == null || target == null)
            throw new ActivityPubException("Follower or target actor not found");

        if (!target.followedActors.containsKey(followerId)) return false;

        target.followedActors.remove(followerId);
        follower.followingActors.remove(targetId);

        return true;
    }
    
    @Override
    public List<Actor> getFollowers(String fullId) {
        Optional<Actor> givenActor = getActor(fullId);

        if (givenActor.isPresent()) {
            Actor actor = givenActor.get();
        
            List<Actor> followers = new ArrayList<>();
            for (Actor a : actors.values()) {
                if (a.followingActors.containsKey(actor.username)) {
                    followers.add(a);
                }
            }
            return followers;
        }

        System.out.println("Actor not found");
        return Collections.emptyList();
    }


    @Override
    public List<Actor> getFollowing(String fullId) {
        Optional<Actor> givenActor = getActor(fullId);
        
        if (givenActor.isPresent()) {
            Actor actor = givenActor.get();
            return new ArrayList<>(actor.followingActors.values());
        }
        
        System.out.println("Actor not found");
        return Collections.emptyList();
    }

    @Override
    public boolean isFollowing(String followerId, String targetId) {
        Actor follower = actors.get(followerId);

        if (follower == null) return false;

        return follower.followingActors.containsKey(targetId);
    }

    @Override
    public Activity createActivity(String actorId, ActivityType type, String content) {
        Actor actor = actors.get(actorId);
        if (actor == null) return null;

        Activity act = new Activity(type, actorId, content);
        actor.outbox.add(act);

        for (Actor follower : getFollowers(actorId)) {
            follower.inbox.add(0, act);
        }

        return act;
    }

    @Override
    public Stream<Activity> getAllActivities() {
        return actors.values().stream().flatMap(a -> a.outbox.stream());
    }

    @Override
    public Stream<Activity> getInbox(String actorId) {
        Actor actor = actors.get(actorId);
        return actor.inbox.stream();
    }

    @Override
    public Stream<Activity> getOutbox(String actorId) {
        Actor actor = actors.get(actorId);
        return actor.outbox.stream();
    }

    @Override
    public void receiveActivity(Activity activity, String... targetId) {
        for (String followerID : targetId) {
            Optional<Actor> opActor = getActor(followerID);
            if(opActor.isPresent()) {
                Actor actor = opActor.get();
                actor.inbox.add(0, activity);
            }
        }
    }
    
}
