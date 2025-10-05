public class Main {
    public static void main(String[] args) throws ActivityPubException {
        // Create server
        Server server = new InMemoryServer("localServer");

        // Create actors
        Actor alice = server.createActor("alice", "Alice Wonderland");
        Actor bob = server.createActor("bob", "Bob Builder");

        System.out.println("Actors created:");
        server.listAllActors().forEach(a -> System.out.println(a.getFullId()));

        // Follow relationship
        server.follow("alice", "bob");
        System.out.println("Alice now follows Bob? " + server.isFollowing("alice", "bob"));

        // Create activity (Alice posts something)
        Activity post = server.createActivity("alice", ActivityType.CREATE, "Hello ActivityPub!");
        System.out.println("Activity created: " + post.getContent());

        // Check outbox/inbox
        System.out.println("\nAlice's outbox:");
        server.getOutbox("alice").forEach(a -> System.out.println(a.getContent()));

        System.out.println("\nBob's inbox:");
        server.getInbox("bob").forEach(a -> System.out.println(a.getContent()));
    }
}
