package xyz.xuaq.lock;

import lombok.Data;


public class Lock {

    String lockid;

    String path;

    boolean Activation;

    public Lock(String lockid,String path){
        this.lockid=lockid;
        this.path=path;
    }
    public Lock(String lockid,String path,boolean isActivation){
        this.lockid=lockid;
        this.path=path;
        this.Activation=isActivation;
    }

    public String getLockid() {
        return lockid;
    }

    public void setLockid(String lockid) {
        this.lockid = lockid;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isActivation() {
        return Activation;
    }

    public void setActivation(boolean activation) {
        Activation = activation;
    }
}
