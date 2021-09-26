package Utility;

import Server.RemoteMethods;

import java.util.*;
import java.util.stream.Collectors;

public class ConnectionHandlerSingleton {

    private final List<Map<Integer, RemoteMethods>> connections = new ArrayList<>();
    private static ConnectionHandlerSingleton INSTANCE;

    public static ConnectionHandlerSingleton getInstance(){
        if(INSTANCE == null) {
            INSTANCE = new ConnectionHandlerSingleton();
        }
        return INSTANCE;
    }

    public int roomSize(){
        return this.connections.size();
    }

    public int subscribe(Integer port, RemoteMethods stub){
        Map<Integer, RemoteMethods> map = new HashMap<>();
        map.put(port,stub);
        this.connections.add(map);
        return this.connections.size()-1; // return the id of the created room
    }

    public void join(int idRoom, Integer port, RemoteMethods stub){
        Map<Integer, RemoteMethods> map = new HashMap<>();
        map.put(port,stub);
        this.connections.get(idRoom).putAll(map);
    }

    public Map<Integer,RemoteMethods> getConnections(int roomID){
        return this.connections.get(roomID);
    }

    public List<Integer> getKeyByValue(RemoteMethods remoteMethods, int roomID){
        return this.connections.get(roomID)
                .entrySet()
                .stream()
                .filter(entry -> Objects.equals(entry.getValue(),remoteMethods))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

}
