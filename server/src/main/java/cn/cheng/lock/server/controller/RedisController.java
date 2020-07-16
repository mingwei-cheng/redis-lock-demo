package cn.cheng.lock.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * demo2 : 演示了redis在分布式的高并发场景下，使用简单的手动锁可能会出现的问题。
 *
 * @author Cheng Mingwei
 * @create 2020-07-15 19:58
 **/
@RestController
public class RedisController {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @GetMapping("/shop")
    public String shopPhone() {
        String lock = "phoneLock";
        /*
         * 尝试获取锁,Redis单线程，所以同时只会有一个线程获得锁
         * 未设置超时时间，在服务不出问题的情况下（如服务挂了，网络断了等），可以实现分布式锁
         */
        Boolean result = redisTemplate.opsForValue().setIfAbsent(lock, "phone");
        assert result != null;
        //假如没获取到锁
        if (!result) {
            System.out.println("活动太火爆了，请稍后再试!");
            return "活动太火爆了，请稍后再试!";
        }
        //获取到了，才需要finally去释放锁
        try {
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
            //释放锁
            redisTemplate.delete(lock);
        }
    }
}
