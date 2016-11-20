package com.codemine.talk2me;

import java.io.*;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.*;

public class Ip implements Runnable{
    public HashMap ping; //ping 后的结果集
    static ArrayList<String> result;
    private static final String NETWORK_CARD = "eth0";//单网卡名称
    private static final String NETWORK_CARD_BAND = "bond0";//绑定网卡名称

    public HashMap getPing(){ //用来得到ping后的结果集
        return ping;
    }

    //当前线程的数量, 防止过多线程摧毁电脑
    static int threadCount = 0;

    public Ip() {
        ping = new HashMap();
        result = new ArrayList<>();
    }

    public void Ping(String ip) throws Exception{
//最多30个线程
        while(threadCount>30)
            Thread.sleep(50);
        threadCount +=1;
        PingIp p = new PingIp(ip);
        p.start();
    }

    public Ip PingAll() throws Exception{
        //首先得到本机的IP，得到网段
//        InetAddress host = InetAddress.getLocalHost();
        String hostAddress = InetAddress.getLocalHost().getHostAddress();
//        getLocalIP();
//        int k=0;
//        String hostAddress = getLocalIP();
        System.err.println(hostAddress);
        int k = hostAddress.lastIndexOf(".");
        String ss = hostAddress.substring(0,k+1);
        for(int i=1;i <=255;i++){ //对所有局域网Ip
            String iip=ss+i;
            Ping(iip);
        }
        //等着所有Ping结束
        while(threadCount>0) {
            Thread.sleep(50);
        }
        return this;
    }

    public static String getLocalIP() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> e1 = NetworkInterface.getNetworkInterfaces();
            while (e1.hasMoreElements()) {
                NetworkInterface ni = e1.nextElement();

                //单网卡或者绑定双网卡
                if ((NETWORK_CARD.equals(ni.getName()))
                        || (NETWORK_CARD_BAND.equals(ni.getName()))) {
                    Enumeration<InetAddress> e2 = ni.getInetAddresses();
                    while (e2.hasMoreElements()) {
                        InetAddress ia = e2.nextElement();
                        if (ia instanceof Inet6Address) {
                            continue;
                        }
                        ip = ia.getHostAddress();
                    }
                    break;
                }
                else {
                    continue;
                }
            }
        }
        catch (SocketException e) {
            System.out.println("IpGetter.getLocalIP出现异常！异常信息：" + e.getMessage());
        }
        return ip;
    }

    @Override
    public void run() {
        try {
            Ip ip = new Ip();
            ip.PingAll();
//            java.util.Set entries = ping.entrySet();
//            Iterator iter = entries.iterator();

//            String k;
//            while (iter.hasNext()) {
//                Map.Entry entry = (Map.Entry) iter.next();
//                String key = (String) entry.getKey();
//                String value = (String) entry.getValue();
//
//                if (value.equals("true")) {
//                    result.add(key);
//                    System.out.println(key + "-->" + value);
//                }
//            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    class PingIp extends Thread{
        public String ip; // IP
        public PingIp(String ip){
            this.ip=ip;
        }

        public void run(){
            Process p= null;
            try {
                p = Runtime.getRuntime().exec ("ping "+ip);

            InputStreamReader ir = new InputStreamReader(p.getInputStream());
                LineNumberReader input = new LineNumberReader (ir);
//读取结果行
                for (int i=1 ; i <7; i++)
                    input.readLine();
                String line= input.readLine();
                if(line == null)
                {
                    System.out.println(ip);
                    return;
                }
                String reg=".*timeout.*";
                if (line.matches(reg))
                {
                    ping.put(ip,"false");
                }
                else{
                    ping.put(ip,"true");
                    result.add(ip);
                    System.out.println("inresult: " + result.size());
                }
//线程结束
                threadCount -= 1;
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}