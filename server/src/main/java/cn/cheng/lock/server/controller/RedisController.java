package cn.cheng.lock.server.controller;

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
        //加锁，确保当前只有一个用户能够进来消费
        synchronized (this) {
            //取出phone的剩余数量
            int number = Integer.parseInt(String.valueOf(redisTemplate.opsForValue().get("phone")));
            //还有剩余
            if (number > 0) {
                //消费一个
                number--;
                //将消费完的phone的数量，重新放到redis中
                redisTemplate.opsForValue().set("phone", String.valueOf(number));
                System.out.println("恭喜抢到啦！"+number);
                return "恭喜抢到啦！";
            } else {
                System.out.println("没抢到...");
                return "没抢到...";
            }
        }
    }
}
