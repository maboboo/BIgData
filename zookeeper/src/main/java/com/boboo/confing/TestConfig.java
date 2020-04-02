package com.boboo.confing;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.client.ZooKeeperSaslClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 *  实现一个获取配置的客户端，要求 zookeeper 中配置变更后，客户端也可以立即获取到变更后的配置信息。
 *
 *  1. 初始化 zookeeper 连接客户端
 *  2. 监听 zookeeper 中配置项目录的 watch 时间。
 *  3.
 */
public class TestConfig {

    private ZookeeperClient zk;

    @Before
    public void init(){
        System.out.println("before");
        zk = ZookeeperClient.getZookeeperClient();

    }

    @After
    public void close(){
            zk.close();
    }

    @Test
    public void testConfig() throws InterruptedException {
        System.out.println("test");
        zk.aWait();
        while (true){
            if (zk.getConfig().equals("")){
                System.out.println("lost config");
                zk.aWait();
            }else {
                System.out.println(zk.getConfig());
            }

            Thread.sleep(2000);
        }
    }
}

