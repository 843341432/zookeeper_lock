package xyz.xuaq.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import xyz.xuaq.lock.Lock;
import xyz.xuaq.lock.ZookeeperLock;

/**
 * @author x
 */
@Service
public class ProductService {

    @Autowired
    RedisTemplate<Object,Object> redisTemplate;
    public String update() {
        ZookeeperLock zookeeperLock=new ZookeeperLock();
        Lock lock = zookeeperLock.lock("xu", 100000);
        if (lock.isActivation()) {
            try {
                int num = Integer.parseInt(redisTemplate.opsForValue().get("num").toString());
                if (num > 0) {
                    num = num - 1;
                    redisTemplate.opsForValue().set("num", num);
                    System.out.println("剩余库存：" + num);
                } else {
                    System.out.println("库存不足！");
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                zookeeperLock.deleteLock(lock);
            }
        }
        return "end";
    }
}
