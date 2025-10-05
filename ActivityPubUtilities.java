public class ActivityPubUtilities {

    private ActivityPubUtilities(){}

    private static NamingService service = null;
    /**
     * Retrieves the NamingService
     * @return the naming service singleton object
     */
    public static NamingService getNamingService(){
        if( service == null ){
            service = new InMemoryNamingService();
        }
        return service;
    }

    /**
     * Creates a new instance of a local server
     * @param name the name of the server
     * @return reference to the Server
     */
    public static Server createServer(String name){
        // TODO: to be implemented
        service = ActivityPubUtilities.getNamingService();
        Server server = new InMemoryServer(name);

        service.registerServer(name, server);
        return server;
    }


    /**
     * converts a local username and the server name into a full actor ID
     * @param username the local user name
     * @param serverName the server name
     * @return the full ID
     */
    public static  String fullId(String username, String serverName){
        return "@%s@%s".formatted(username,serverName);
    }


}
