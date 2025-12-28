package com.m.freemovie.mvp.ClassBean;

public class ServerSearchEvent {
    boolean isServer;

    public ServerSearchEvent(boolean isServer) {
        this.isServer = isServer;
    }

    public boolean isServer() {
        return isServer;
    }

    public void setServer(boolean server) {
        isServer = server;
    }





}
