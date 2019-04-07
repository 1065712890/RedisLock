package test;

import cn.dengbin97.lock.RedisLock;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import cn.dengbin97.util.RedisUtil;

/**
 * @program: redislock
 * @description: redis测试类
 * @author: dengbin
 * @create: 2018-11-14 10:35
 **/

@Slf4j
public class RedisTest {

    private Jedis jedis;

    private static final String LOCK_KEY = "DB_REDIS_LOCK_";
    private static final String REQUEST_KEY = "DB_REDIS_REQUEST_";

    @Before
    public void init(){
        jedis = RedisUtil.getJedis();
    }

    @Test
    public void scheduled() throws InterruptedException {
        log.info("机器1-定时任务开始");
        if ( RedisLock.tryLock("key", "string1", 0)) {
            jedis.incrBy("key", 1);
            log.info("机器1-获取到锁，执行定时任务");
        } else {
            log.info("机器1-获取锁失败");
        }
    }

    @Test
    public void scheduled2() throws InterruptedException {
        log.info("机器2-定时任务开始");
        if ( RedisLock.tryLock("key", "string2", 0)) {
            jedis.incrBy("key", 1);
            log.info("机器2-获取到锁，执行定时任务");
        } else {
            log.info("机器2-获取锁失败");
        }
    }

    @Test
    public void scheduled3() throws InterruptedException {
        log.info("机器3-定时任务开始");
        if ( RedisLock.tryLock("key", "string3", 0)) {
            jedis.incrBy("key", 1);
            log.info("机器3-获取到锁，执行定时任务");
        } else {
            log.info("机器3-获取锁失败");
        }
    }

//    @Test
//    public void test(){
//        String res = jedis.get("db");
//        System.out.println(res);
//    }
//
//    @Test
//    public void tryLock(){
//
//    }
//
//    @Test
//    public void cn.dengbin97.lock(){
//        String res = jedis.set(LOCK_KEY, "db_id", "NX", "PX", 100000);
//        log.info("{} {}", res, res == null ? "加锁失败" : "加锁成功");
//    }
//
//    @Test
//    public void unlock(){
//
//    }
//
//    @Test
//    public void ttl(){
//        Long ttl = jedis.ttl(LOCK_KEY);
//        System.out.println(ttl);
//    }

    @After
    public void destroy(){
        RedisUtil.returnResource(jedis);
    }
}
