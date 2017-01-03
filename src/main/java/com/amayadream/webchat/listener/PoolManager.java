package com.amayadream.webchat.listener;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDriver;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.junit.Test;

/**
 * @author :  Amayadream
 * @date :  2016.04.06 20:17
 */
public class PoolManager {
    private static String DRIVER = null;
    private static String URL = null;
    private static String NAME = null;
    private static String PASSWORD = null;

    private static Class driverClass = null;
    private static ObjectPool connectionPool = null;

    public PoolManager() {

    }

    /**
     * 装配配置文件
     */
    private static void loadProperties(){
        try {
            InputStream stream = PoolManager.class.getClassLoader().getResourceAsStream("jdbc.properties");
            Properties props = new Properties();
            props.load(stream);
            DRIVER = props.getProperty("driver");
            URL = props.getProperty("url");
            NAME = props.getProperty("username");
            PASSWORD = props.getProperty("password");
        } catch (FileNotFoundException e) {
            System.out.println("读取配置文件异常");
        } catch(IOException ie){
            System.out.println("读取配置文件时IO异常");
        }
    }

    /**
     * 初始化数据源
     */
    private static synchronized void initDataSource(){
        if (driverClass == null) {
            try{
                driverClass = Class.forName(DRIVER);
            }catch (ClassNotFoundException e){
                e.printStackTrace();
            }
        }
    }

    /**
     * 连接池启动
     */
    private static void startPool(){
        loadProperties();
        initDataSource();
        if (connectionPool != null) {
            destroyPool();
        }
        try {
            connectionPool = new GenericObjectPool(null);
            ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(URL, NAME, PASSWORD);
            PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory, connectionPool, null, null, false, true);
            Class.forName("org.apache.commons.dbcp.PoolingDriver");
            PoolingDriver driver = (PoolingDriver) DriverManager.getDriver("jdbc:apache:commons:dbcp:");
            driver.registerPool("dbpool", connectionPool);
            System.out.println("装配连接池OK");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭连接池
     */
    private static void destroyPool(){
        try {
            PoolingDriver driver = (PoolingDriver) DriverManager.getDriver("jdbc:apache:commons:dbcp:");
            driver.closePool("dbpool");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 取得连接池中的连接
     * @return
     */
    public static Connection getConnection() {
        Connection conn = null;
        if(connectionPool == null)
            startPool();
        try {
            conn = DriverManager.getConnection("jdbc:apache:commons:dbcp:dbpool");
            System.out.println("取得连接成功");
        } catch (SQLException e) {
            System.out.println("取得连接失败");
            e.printStackTrace();
        }
        return conn;
    }

    /**
     * 获取连接
     * getConnection
     * @param name
     * @return
     */
    public static Connection getConnection(String name){
        return getConnection();
    }

    /**
     * 释放连接
     * freeConnection
     * @param conn
     */
    private static void freeConnection(Connection conn){
        if(conn != null){
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 释放连接
     * freeConnection
     * @param name
     * @param con
     */
    public static void freeConnection (String name,Connection con){
        freeConnection(con);
    }

    @Test
    public void test(){
        try {
            Connection conn = PoolManager.getConnection();
            if(conn != null){
                Statement statement = conn.createStatement();
                ResultSet rs = statement.executeQuery("select * from user");
                int c = rs.getMetaData().getColumnCount();
                while(rs.next()){
                    System.out.println();
                    for(int i=1;i<=c;i++){
                        System.out.print(rs.getObject(i));
                    }
                }
                rs.close();
            }
            PoolManager.freeConnection(conn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}