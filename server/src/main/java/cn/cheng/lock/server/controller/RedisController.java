package cn.cheng.lock.server.controller;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @author Cheng Mingwei
 * @create 2020-07-15 19:58
 **/
@RestController
public class RedisController {

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private RedissonClient redissonClient;

    @GetMapping("/shop")
    public String shopPhone() {
        //标识锁用
        String lock = "phoneLock";
        //获取锁
        RLock rLock = redissonClient.getLock(lock);
        if (rLock.isLocked()) {
            System.out.println("活动太火爆了，请稍后再试！");
            return "活动太火爆了，请稍后再试！";
        }
        //获取到了，才需要finally去释放锁
        try {
            //加锁
            rLock.lock(5, TimeUnit.SECONDS);
            //取出phone的剩余数量
            String phone = redisTemplate.opsForValue().get("phone");
            if (phone == null) {
                System.out.println("没抢到...");
                return "没抢到...";
            }
            int number = Integer.parseInt(phone);
            //还有剩余
            if (number > 0) {
                //消费一个
                int newNumber = number - 1;
                //将消费完的phone的数量，重新放到redis中
                redisTemplate.opsForValue().set("phone", String.valueOf(newNumber));
                System.out.println("恭喜抢到啦！" + newNumber);
                return "恭喜抢到啦！";
            } else {
                System.out.println("没抢到...");
                return "没抢到...";
            }
        } finally {
            if (rLock.isLocked()) {
                //释放锁
                rLock.unlock();
            }
        }
    }
}
