package com.codemine.talk2me;

import java.io.*;
import java.net.InetAddress;
import java.util.*;

public class Ip implements Runnable{
    public HashMap ping; //ping 后的结果集
    static ArrayList<String> result;
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
        InetAddress host = InetAddress.getLocalHost();
        String hostAddress = host.getHostAddress();
        int k=0;
        k=hostAddress.lastIndexOf(".");
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