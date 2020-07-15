package cn.cheng.lock.eureka.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Cheng Mingwei
 * @create 2020-07-15 19:58
 **/
@RestController
public class RedisController {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @GetMapping("/shop")
    public String shopPhone() {
        synchronized (this) {
            int number = Integer.parseInt(String.valueOf(redisTemplate.opsForValue().get("shop")));
            if (number > 0) {
                number--;
                redisTemplate.opsForValue().set("shop", String.valueOf(number));
                System.out.println("恭喜抢到啦！"+number);
                return "恭喜抢到啦！";
            } else {
                System.out.println("没抢到...");
                return "没抢到...";
            }
        }
    }
}
