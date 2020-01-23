package main;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.ListPosition;

/**
 * Project RedisTest
 * Created by End on янв., 2020
 */
public class JedisMain {

    private static int usersCount = 20;
    private static String key = "users";

    //адресс сервера
    private static final String redisHost = "localhost";
    private static final Integer redisPort = 6379;

    //Jedis connection
    private static Jedis jedis;


    public static void main(String[] args) throws InterruptedException {
        jedis = new Jedis(redisHost, redisPort);
        addUsers();
        Long userListLength = jedis.llen(key);

        for (; ; ) {
            for (int i = 0; i < userListLength; i++) {
                String user = jedis.lindex(key, i);

                System.out.println("Показываем на экране пользователя " + user);

                if (Math.round(Math.random() * 10) >= 9) {
                    long rnd = randomPay(userListLength, user);
                    if (rnd < i) i--;
                }
                Thread.sleep(1000);
            }
        }
    }

    //Заполнение списка
    public static void addUsers() {
        for (int i = 0; i < usersCount; i++) {
            jedis.rpush(key, Integer.toString(i));
        }
    }

    //Случайный юзер который дал взятку
    public static long randomPay(Long listLength, String user) {
        String richUser = "";
        long randomUser = 0;
        boolean flag = true;

        while (flag) {
            randomUser = Math.round(Math.random() * (listLength - 1));
            richUser = jedis.lindex(key, randomUser);
            flag = richUser.equals(user);
        }

        jedis.lrem(key,0,richUser);
        jedis.linsert(key, ListPosition.AFTER, user,richUser);

        System.out.println("Пользователь " + richUser + " дал взятку !!!");
        return randomUser;
    }
}
