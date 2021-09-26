import Host.Host;
import Utility.ConnectionHandlerSingleton;

public class Launcher {

    public static void main(String[] args){

        ConnectionHandlerSingleton connectionHandlerSingleton = ConnectionHandlerSingleton.getInstance();

        Host host = new Host(8080);

        Host hostB = new Host(8081);

        //Host hostC = new Host(8082);

        //Host hostD = new Host(8083);
    }
}
