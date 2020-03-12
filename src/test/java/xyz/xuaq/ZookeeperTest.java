package xyz.xuaq;


import org.I0Itec.zkclient.ZkClient;
import org.junit.Test;
import xyz.xuaq.lock.Lock;
import xyz.xuaq.lock.ZookeeperLock;

public class ZookeeperTest {

    @Test
    public void zookeeperTest() throws InterruptedException {
        ZookeeperLock zookeeperLock = new ZookeeperLock();
        Lock xu = zookeeperLock.lock("xu", 100000);
        System.out.println("获取了"+xu.getPath());
        Thread.sleep(Long.MAX_VALUE);
    }
    @Test
    public void createnode(){
        ZkClient zkClient=new ZkClient("localhost:2181",30000,20000);
        zkClient.createPersistent("/lock");
    }
}
