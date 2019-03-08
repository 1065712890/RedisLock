package lock;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import util.RedisUtil;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * @program: redislock
 * @description: redis实现分布式锁
 * @author: dengbin
 * @create: 2018-11-14 14:41
 **/

@Slf4j
public class RedisLock {

    private static Jedis jedis;

    static {
        jedis = RedisUtil.getJedis();
        log.info("获取redis连接, {}", jedis);
    }


    /**
     * @description: 加锁,支持设置加锁时间
     * @author: dengbin
     * @date: 2018/11/14 下午2:43
     */
    public static Boolean lock(String key, String value, TimeUnit timeUnit, Long time) {
        String res = jedis.set(key, value, "NX", "PX", timeUnit.toSeconds(time));
        log.info("{} - {} 加锁结果: {}", key, value, RedisUtil.LOCK_SUCCESS.equals(res) ? "成功" : "失败");
        return RedisUtil.LOCK_SUCCESS.equals(res);
    }


    /**
     * @description: 加锁
     * @author: dengbin
     * @date: 2018/11/14 下午2:43
     */
    public static Boolean lock(String key, String value) {
        String res = jedis.set(key, value, "NX", "PX", RedisUtil.SURVIVAL_TIME);
        log.info("{} - {} 加锁结果: {}", key, value, RedisUtil.LOCK_SUCCESS.equals(res) ? "成功" : "失败");
        return RedisUtil.LOCK_SUCCESS.equals(res);
    }

    /**
     * @description: 尝试加锁
     * @author: dengbin
     * @date: 2018/11/14 下午3:00
     */
    public static Boolean tryLock(String productId, String requestId) throws InterruptedException {
        String key = RedisUtil.generateLockKey(productId);
        while (lock(key, requestId) != true) {
            //获取key剩余时间
            Long time = jedis.ttl(key);
            log.info("产生冲突，剩余时间:{}", time);
            if (time > 0) {
                //休眠
                Thread.sleep(time * 1000);
            }else if(time == 0){
                //此处延时1s，否则会多次尝试加锁并失败
                Thread.sleep(1000);
            }
        }
        return true;
    }

    /**
     * @description: 尝试加锁,可以指定尝试的次数
     * @author: dengbin
     * @date: 2018/11/14 下午3:00
     */
    public static Boolean tryLock(String productId, String requestId, Integer times) throws InterruptedException {
        String key = RedisUtil.generateLockKey(productId);
        int cnt = 0;
        while (lock(key, requestId) != true) {
            ++cnt;
            if(cnt > times){
                return false;
            }
            //获取key剩余时间
            Long time = jedis.ttl(key);
            log.info("产生冲突，剩余时间:{}", time);
            if (time > 0) {
                //休眠
                Thread.sleep(time * 1000);
            }else if(time == 0){
                //此处延时1s，否则会多次尝试加锁并失败
                Thread.sleep(1000);
            }
        }
        return true;
    }

    /**
     * @description: 解锁
     * @author: dengbin
     * @date: 2018/11/14 下午3:21
     */
    public static Boolean unLock(String productId, String requestId) {
        //采用lua脚本，保持操作的原子性
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        String key = RedisUtil.generateLockKey(productId);
        Object result = jedis.eval(script, Collections.singletonList(key), Collections.singletonList(requestId));

        if (RedisUtil.RELEASE_SUCCESS.equals(result)) {
            log.info("{} - {} 解锁成功", key, requestId);
            return true;
        }
        log.info("{} - {} 解锁失败", key, requestId);
        return false;
    }

    public static void main(String[] args) throws InterruptedException {
        String productId = "2018", requestId = "dengbing";
        RedisLock.tryLock(productId, requestId);
        RedisLock.unLock(productId, requestId);
    }
}
