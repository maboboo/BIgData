package com.boboo.service;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.ArrayList;
import java.util.List;

public class ClientWatch implements Watcher, AsyncCallback.ChildrenCallback, AsyncCallback.StatCallback {
    public ZooKeeper getZk() {
        return zk;
    }

    public void setZk(ZooKeeper zk) {
        this.zk = zk;
    }

    ZooKeeper zk;
    ArrayList<String> services = new ArrayList<String>();


    public void process(WatchedEvent event) {
        System.out.println(event);
        Event.EventType type = event.getType();
        switch (type) {
            case None:
                break;
            case NodeCreated:
                break;
            case NodeDeleted:
                break;
            case NodeDataChanged:
                break;
            case NodeChildrenChanged:
                zk.getChildren("/services", this, this, "abc");
                break;
        }
    }

    public void processResult(int rc, String path, Object ctx, List<String> children) {
        services.clear();
        for (String child: children){
            try {
                byte[] data = zk.getData("/services/" + child, false, null);
                services.add(new String(data));
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("children: " + children.toString());
    }

    List getServices() {

        return services;
    }
    void beginWatch(){
        zk.exists("/services", this, this, "abc");
    }
    public void processResult(int rc, String path, Object ctx, Stat stat) {
        System.out.println("--- exists --- " + path);
        zk.getChildren("/services", this, this, "abc");
    }
}
