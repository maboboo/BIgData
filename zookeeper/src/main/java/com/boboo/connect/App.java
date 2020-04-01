package com.boboo.connect;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class App {
    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        System.out.println("begin connect zookeeper");

        final CountDownLatch c = new CountDownLatch(1);

        final ZooKeeper zk = new ZooKeeper("localhost:2181", 300,
                new Watcher() {
                    public void process(WatchedEvent event) {
                        System.out.println("watch event: " + event.toString());
                        Event.KeeperState state = event.getState();

                        switch (state) {
                            case Unknown:
                                break;
                            case Disconnected:
                                break;
                            case NoSyncConnected:
                                break;
                            case SyncConnected:
                                System.out.println("connected");
                                c.countDown();
                                break;
                            case AuthFailed:
                                break;
                            case ConnectedReadOnly:
                                break;
                            case SaslAuthenticated:
                                break;
                            case Expired:
                                break;
                        }
                    }
                }
        );
        ZooKeeper.States state = zk.getState();
        System.out.println("get connect status:" + state.toString());

        c.await();
        state = zk.getState();
        System.out.println("get connect status:" + state.toString());

//        zk.exists("/hello", new Watcher() {
//            public void process(WatchedEvent event) {
//                System.out.println("watch event" + event.toString());
//
//            }
//        }, new AsyncCallback.StatCallback() {
//            public void processResult(int rc, String path, Object ctx, Stat stat) {
//                System.out.println(rc);
//                System.out.println(path);
//                System.out.println(ctx);
//                System.out.println(stat);
//                if (stat != null){
//                    zk.getData("/hello", false, new DataCallback() {
//                        public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
//                            System.out.println("get /hello object" + ctx.toString());
//                            System.out.println("get /hello result: " + new String(data));
//                        }
//                    }, "get");
//                }
//
//            }
//        }, "exists");


        String path = zk.create("/hello/world", "hello + world".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);


        // 同步获取数据
        final Stat stat = new Stat();
        System.out.println("========同步获取数据开始========");
        byte[] node = zk.getData("/hello/world", new Watcher() {
            public void process(WatchedEvent event) {
                System.out.println("get data watch: " + event.toString());

                //true   default Watch  被重新注册   new zk的那个watch
                try {
                    zk.getData("/ooxx", this, stat);
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, stat);
        System.out.println("同步获取到数据" + new String(node));
        System.out.println("========同步获取数据结束========");


        // 异步获取数据
        System.out.println("========异步获取数据开始========");
        zk.getData("/hello/world", false, new AsyncCallback.DataCallback() {
            public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
                System.out.println("get /hello/world object" + ctx.toString());
                System.out.println("get /hello/world result: " + new String(data));
            }
        }, "obj");
        System.out.println("========异步获取数据结束========");

        Thread.sleep(10000);

    }
}
