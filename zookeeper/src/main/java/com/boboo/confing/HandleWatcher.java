package com.boboo.confing;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

public class HandleWatcher implements Watcher, AsyncCallback.DataCallback, AsyncCallback.StatCallback {

    private ZooKeeper zk;
    private Config conf;
    private CountDownLatch cc = new CountDownLatch(1);

    public ZooKeeper getZk() {
        return zk;
    }

    public void setZk(ZooKeeper zk) {
        this.zk = zk;
    }


    public Config getConf() {
        return conf;
    }

    public void setConf(Config conf) {
        this.conf = conf;
    }


    public void process(WatchedEvent event) {
        Event.EventType state = event.getType();
        switch (state) {
            case None:
                break;
            case NodeCreated:
            case NodeDataChanged:
                zk.getData("/AppConfig", this, this, "abc");
                break;
            case NodeDeleted:
                System.out.println("node.delete");
                conf.setConf("");
                cc = new CountDownLatch(1);
                break;
            case NodeChildrenChanged:
                break;
        }
    }

    public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
        if (data != null && !new String(data).equals("")) {
            conf.setConf(new String(data));
            cc.countDown();
        } else {
            cc = new CountDownLatch(1);
        }
    }

    public void aWait() {
        zk.exists("/AppConfig", this, this, "abc");
        try {
            cc.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void processResult(int rc, String path, Object ctx, Stat stat) {
        if (stat != null) {
            zk.getData("/AppConfig", this, this, "abc");
        }
    }
}
