/**
 * Created by glacier on 14-9-1.
 */
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class Redis {

    private String Key;
    private Jedis Redis;

    /*public static void main(String[] args) {
        Redis obj = new Redis();
        System.out.println(getSecount("URL"));
        obj.ConnectRedis("222.24.63.100", 9004);
        obj.setKey("URL");
        obj.pushValue("www.baidu.com");
        obj.pushValue("www.google.com");
        System.out.println(obj.getLength());
        System.out.println(obj.popValue());
        System.out.println(obj.popValue());
        System.out.println(obj.getLength());
        obj.Redis.flushAll();
    }*/
    static public int getSecount( String username ) {
        return new Jedis("127.0.0.1", 6379).llen(username).intValue();
    }
    public void ConnectRedis(String host, int port) {
        Redis = new Jedis(host, port);
    }
    public void ConnectRedis( String host ) {
        Redis = new Jedis(host, 6379);
    }
    public void ConnectRedis() {
        Redis = new Jedis("127.0.0.1", 6379);
    }
    public void setKey( String Key ) {
        this.Key = Key;
    }
    public String getKey() {
        return Key;
    }
    public void pushValue( String Value ) {
        Redis.lpush(Key, Value);
    }
    public String popValue() {
        return Redis.rpoplpush(Key, "Temp");
    }
    public int getLength() {
        return Redis.llen(Key).intValue();
    }
    public void delKey() {
        Redis.del(Key);
    }
    public void clearRedis() {
        Redis.flushAll();
    }

}