package org.example.matchinggameserver.model;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.example.matchinggameserver.controller.ServerThread;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class Invitation {
    private User sender;
    private final int existTime = 30;
    private ServerThread serverThread;
    
    public Invitation(User sender, ServerThread serverThread)
    {
        this.sender = sender;
        this.serverThread = serverThread;
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.schedule(() -> {
            serverThread.removeInvitation(sender.getID());
            System.out.println("removed invitation with sender: " + sender.getUsername()
            + " and receiver: " + serverThread.getUser().getUsername());
            executor.shutdown();
        }, existTime, TimeUnit.SECONDS);
    }
    
    public int getSenderID()
    {
    	return sender.getID();
    }

    public String getSenderName()
    {
        return sender.getUsername();
    }

    public int getSenderStars()
    {
        return sender.getStar();
    }

    public int getSenderRank()
    {
        return sender.getRank();
    }
}
