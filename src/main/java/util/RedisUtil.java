package util;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @program: redislock
 * @description: redis工具类
 * @author: dengbin
 * @create: 2018-11-14 10:23
 **/

public class RedisUtil {

    //锁的key前缀， 加上商品id
    private static final String LOCK_KEY = "DB_REDIS_LOCK_";

    //锁的值前缀，加上请求id
    private static final String REQUEST_KEY = "DB_REDIS_REQUEST_";

    //KEY存活时间
    public static final Integer SURVIVAL_TIME = 30000;

    //成功删除返回值
    public static final Long RELEASE_SUCCESS = 1L;

    //成功加锁返回值
    public static final String LOCK_SUCCESS = "OK";

    //如果不存在则加锁
    public static final String SET_IF_NOT_EXIST = "NX";

    //设置存活时间
    public static final String SET_WITH_EXPIRE_TIME = "PX";

    //Redis服务器IP
    private static String ADDR = "127.0.0.1";

    //Redis的端口号
    private static int PORT = 6379;

    //访问密码
    private static String AUTH = "";

    //可用连接实例的最大数目，默认值为8；
    //如果赋值为-1，则表示不限制；如果pool已经分配了maxTotal个jedis实例，则此时pool的状态为exhausted(耗尽)。
    private static int MAX_TOTAL = 1024;

    //控制一个pool最多有多少个状态为idle(空闲的)的jedis实例，默认值也是8。
    private static int MAX_IDLE = 200;

    //等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException；
    private static int MAX_WAIT_MILLIS = 10000;

    private static int TIMEOUT = 10000;

    //在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
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

    /*
    * @description: 生成key
    * @author: dengbin
    * @date: 2018/11/14 下午2:47
    */
    public static String generateLockKey(String productId){
        return LOCK_KEY + productId;
    }

    /*
    * @description: 生成value
    * @author: dengbin
    * @date: 2018/11/14 下午2:48
    */
    public static String generateLockValue(String requestId){
        return REQUEST_KEY + requestId;
    }

}
