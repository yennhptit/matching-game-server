package org.example.matchinggameserver.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ServerThreadBus {
//    private final List<ServerThread> listServerThreads;
//
//    public ServerThreadBus() {
//        listServerThreads = new ArrayList<>();
//    }
//
//    public List<ServerThread> getListServerThreads() {
//        return listServerThreads;
//    }
//
//    public void add(ServerThread serverThread) {
//        listServerThreads.add(serverThread);
//    }
//
//    public void boardCast(int id, String message) {
//        for (ServerThread serverThread : Server.serverThreadBus.getListServerThreads()) {
//            if (serverThread.getClientNumber() != id) {
//                try {
//                    serverThread.write(message);
//                } catch (IOException ex) {
//                    ex.printStackTrace();
//                }
//            }
//        }
//    }
//
//    public int getLength() {
//        return listServerThreads.size();
//    }
//
//    public void sendMessageToUserID(int id, String message) {
//        for (ServerThread serverThread : Server.serverThreadBus.getListServerThreads()) {
//            if (serverThread.getUser().getID() == id) {
//                try {
//                    serverThread.write(message);
//                    break;
//                } catch (IOException ex) {
//                    ex.printStackTrace();
//                }
//            }
//        }
//    }
//
//    public ServerThread getServerThreadByUserID(int ID) {
//        for (int i = 0; i < Server.serverThreadBus.getLength(); i++) {
//            if (Server.serverThreadBus.getListServerThreads().get(i).getUser().getID() == ID) {
//                return Server.serverThreadBus.listServerThreads.get(i);
//            }
//        }
//        return null;
//    }
//
//    public void remove(int id) {
//        for (int i = 0; i < Server.serverThreadBus.getLength(); i++) {
//            if (Server.serverThreadBus.getListServerThreads().get(i).getClientNumber() == id) {
//                Server.serverThreadBus.listServerThreads.remove(i);
//                break;
//            }
//        }
//    }
private final List<ServerThread> listServerThreads;

    public ServerThreadBus() {
        listServerThreads = new ArrayList<>();
    }

    public List<ServerThread> getListServerThreads() {
        return listServerThreads;
    }

    public void add(ServerThread serverThread) {
        listServerThreads.add(serverThread);
    }

    public void boardCast(int id, String message) {
        for (ServerThread serverThread : Server.serverThreadBus.getListServerThreads()) {
            if (serverThread.getClientNumber() != id) {
                try {
                    serverThread.write(message);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public int getLength() {
        return listServerThreads.size();
    }

    public void sendMessageToUserID(int id, String message) {
        for (ServerThread serverThread : Server.serverThreadBus.getListServerThreads()) {
            if (serverThread.getUser().getID() == id) {
                try {
                    serverThread.write(message);
                    break;
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public ServerThread getServerThreadByUserID(int ID) {
        for (int i = 0; i < Server.serverThreadBus.getLength(); i++) {
            if (Server.serverThreadBus.getListServerThreads().get(i).getUser().getID() == ID) {
                return Server.serverThreadBus.listServerThreads.get(i);
            }
        }
        return null;
    }

    public void remove(int id) {
        for (int i = 0; i < Server.serverThreadBus.getLength(); i++) {
            if (Server.serverThreadBus.getListServerThreads().get(i).getClientNumber() == id) {
                Server.serverThreadBus.listServerThreads.remove(i);
                break;
            }
        }
    }
}
