package cn.dengbin97.util;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;

import java.util.HashSet;
import java.util.Set;

/**
 * @program: redislock
 * @description: redis工具类
 * @author: dengbin
 * @create: 2018-11-14 10:23
 **/

public class RedisUtil {

    /**
    * @description: 锁的key前缀， 加上商品id
    * @author: dengbin
    * @date: 2019/2/14
    */
    private static final String LOCK_KEY = "DB_REDIS_LOCK_";

    /**
     * @description: 锁重入，计数
     * @author: dengbin
     * @date: 2019/2/14
     */
    public static final String LOCK_TIMES = "DB_REDIS_LOCKTIMES";

    /**
    * @description: 锁的值前缀，加上请求id
    * @author: dengbin
    * @date: 2019/2/14
    */
    private static final String REQUEST_KEY = "DB_REDIS_REQUEST_";

    /**
    * @description: KEY存活时间
    * @author: dengbin
    * @date: 2019/2/14
    */
    public static final Long SURVIVAL_TIME = 10L;

    /**
    * @description: 成功删除返回值
    * @author: dengbin
    * @date: 2019/2/14
    */
    public static final Long RELEASE_SUCCESS = 1L;

    /**
    * @description: 成功加锁返回值
    * @author: dengbin
    * @date: 2019/2/14
    */
    public static final String LOCK_SUCCESS = "OK";

    /**
    * @description: 如果不存在则加锁
    * @author: dengbin
    * @date: 2019/2/14
    */
    public static final String SET_IF_NOT_EXIST = "NX";

    /**
    * @description: 设置存活时间
    * @author: dengbin
    * @date: 2019/2/14
    */
    public static final String SET_WITH_EXPIRE_TIME = "PX";

    /**
    * @description: Redis服务器IP
    * @author: dengbin
    * @date: 2019/2/14
    */
    private static String ADDR = "127.0.0.1";

    /**
    * @description: Redis的端口号
    * @author: dengbin
    * @date: 2019/2/14
    */
    private static int PORT = 6379;

    /**
    * @description: 访问密码
    * @author: dengbin
    * @date: 2019/2/14
    */
    private static String AUTH = "";

    /**
    * @description:
    * @author: dengbin
    * @date: 2019/2/14
    * 可用连接实例的最大数目，默认值为8；
    * 如果赋值为-1，则表示不限制；如果pool已经分配了maxTotal个jedis实例，则此时pool的状态为exhausted(耗尽)。
    */
    private static int MAX_TOTAL = -1;

    /**
    * @description: 控制一个pool最多有多少个状态为idle(空闲的)的jedis实例，默认值也是8。
    * @author: dengbin
    * @date: 2019/2/14
    */
    private static int MAX_IDLE = 200;

    /**
    * @description:
    * @author: dengbin
    * @date: 2019/2/14
    * 等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException；
    */
    private static int MAX_WAIT_MILLIS = 10000;

    private static int TIMEOUT = 0;

    /**
    * @description: 在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
    * @author: dengbin
    * @date: 2019/2/14
    */
    private static boolean TEST_ON_BORROW = true;

    private static JedisPool jedisPool = null;

    /**
     * 初始化Redis连接池
     */
    static {
        try {
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(MAX_TOTAL);
            config.setMaxIdle(MAX_IDLE);
            config.setMaxWaitMillis(MAX_WAIT_MILLIS);
            config.setTestOnBorrow(TEST_ON_BORROW);
//            String master = "mymaster";
//            //sentinel客户端提供了master自动发现功能
//            Set<String> sentinels = new HashSet<>();
//            sentinels.add("127.0.0.1:26379");
//            sentinels.add("127.0.0.1:26380");

//            jedisPool = new JedisSentinelPool(master, sentinels, config);
              jedisPool = new JedisPool(config, ADDR, PORT, TIMEOUT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取Jedis实例
     * @return
     */
    public synchronized static Jedis getJedis() {
        try {
            if (jedisPool != null) {
                Jedis resource = jedisPool.getResource();
                return resource;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 释放jedis资源
     * @param jedis
     */
    public static void returnResource(final Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }

    /**
    * @description: 生成key
    * @author: dengbin
    * @date: 2018/11/14 下午2:47
    */
    public static String generateLockKey(String productId){
        return LOCK_KEY + productId;
    }

    /**
    * @description: 生成value
    * @author: dengbin
    * @date: 2018/11/14 下午2:48
    */
    public static String generateLockValue(String requestId){
        return REQUEST_KEY + requestId;
    }

}
