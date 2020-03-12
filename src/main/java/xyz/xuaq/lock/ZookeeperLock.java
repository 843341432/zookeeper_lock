package xyz.xuaq.lock;


import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import java.util.List;
import java.util.stream.Collectors;

public class ZookeeperLock {

    private ZkClient zkClient;
    //获取连接
    public ZookeeperLock(){
        zkClient=new ZkClient("localhost:2181",3000,20000);
    }
    //创建锁
    public Lock lock(String lockid,long timeout){
        Lock lockNode=createLockNode(lockid);
        //激活锁
        lockNode=activationLock(lockNode);
        //没有激活进入等待
        if (!lockNode.isActivation()){
            try {

            synchronized (lockNode) {
                    lockNode.wait(timeout);
            }

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return lockNode;
    }



    //激活锁
    public Lock  activationLock(Lock lockNode){
        //查询所有节点
        List<String> list = zkClient.getChildren("/lock")
                .stream()
                .sorted()
                .map(p -> "/lock/" + p)
                .collect(Collectors.toList());
        //查看是否为第一位

        if (list.get(0).equals(lockNode.path)){
            lockNode.setActivation(true);
        }else {
            //监听前面节点
            String upNodePath = list.get(list.indexOf(lockNode.path) - 1);
            zkClient.subscribeDataChanges(upNodePath, new IZkDataListener() {
                @Override
                public void handleDataChange(String s, Object o) throws Exception {
                }

                @Override
                public void handleDataDeleted(String s) throws Exception {
                    System.out.println("节点删除:"+s);
                    Lock lock = activationLock(lockNode);
                    //前面节点删除，唤醒线程再次去获取lock
                    synchronized (lockNode){
                        if (lock.isActivation()){
                            lockNode.notify();
                        }
                    }
                    zkClient.unsubscribeDataChanges(upNodePath,this);
                }
            });
        }
        return lockNode;

    }

    //删除锁
    public void deleteLock(Lock lock){
        if (lock.isActivation()){
            zkClient.delete(lock.getPath());
        }
    }



    private Lock createLockNode(String lockid) {
        String path = zkClient.createEphemeralSequential("/lock/" + lockid, "w");
        return new Lock(lockid,path,false);
    }
}
