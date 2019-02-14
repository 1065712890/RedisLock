package test;

import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import util.RedisUtil;

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
    public void test(){
        String res = jedis.get("db");
        System.out.println(res);
    }

    @Test
    public void tryLock(){

    }

    @Test
    public void lock(){
        String res = jedis.set(LOCK_KEY, "db_id", "NX", "PX", 100000);
        log.info("{} {}", res, res == null ? "加锁失败" : "加锁成功");
    }

    @Test
    public void unlock(){

    }

    @Test
    public void ttl(){
        Long ttl = jedis.ttl(LOCK_KEY);
        System.out.println(ttl);
    }

    @After
    public void destroy(){
        RedisUtil.returnResource(jedis);
    }
}
