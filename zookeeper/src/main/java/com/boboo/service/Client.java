package com.boboo.service;

import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.apache.zookeeper.Watcher.Event.KeeperState.SyncConnected;

public class Client {

    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        ZooKeeper zk = new ZooKeeper("127.0.0.1:2181/BigData", 1000, new Watcher() {
            public void process(WatchedEvent event) {
                Event.KeeperState state = event.getState();
                if (state == SyncConnected) {
                    countDownLatch.countDown();
                }
            }
        });
        System.out.println("connecting");
        countDownLatch.await();
        System.out.println("connected");

        ClientWatch watch = new ClientWatch();
        watch.setZk(zk);
        watch.beginWatch();

        while (true) {
            System.out.println(watch.getServices());
            Thread.sleep(2000);
        }
    }
}
