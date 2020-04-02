package com.boboo.confing;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class ZookeeperClient {
    private ZooKeeper zk = null;
    private Config conf = new Config();
    private HandleWatcher watcher;

    private ZookeeperClient() {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        try {
            zk = new ZooKeeper("localhost:2181", 300, new DefaultWatcher(countDownLatch));
            countDownLatch.await();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        watcher = new HandleWatcher();
        watcher.setZk(zk);
        watcher.setConf(conf);

    }

    private static final ZookeeperClient INSTANCE = new ZookeeperClient();

    public static ZookeeperClient getZookeeperClient() {
        return INSTANCE;
    }

    public void close() {
        try {
            zk.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void aWait(){
        watcher.aWait();
    }

    public String getConfig() {
        return conf.getConf();
    }

    public static void await(){
        try {
            INSTANCE.zk.exists("/AppConfig", INSTANCE.watcher);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
