package com.lovesmmiling.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lovesmmiling.entity.Tv;
import com.lovesmmiling.entity.Zhubo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.List;

@Service
@Slf4j
public class TestService {

    @Autowired
    private RestTemplate restTemplate;

    @PostConstruct
    public void getFile() {
        String url = "http://api.hclyz.com:81/mf/json.txt";
        ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(null, null), String.class);
        String body = exchange.getBody();
        JSONObject jsonObject = JSONObject.parseObject(body);
        JSONArray pingtai = jsonObject.getJSONArray("pingtai");
        List<Tv> tvs = JSONArray.parseArray(pingtai.toJSONString(), Tv.class);

        String getUrl = "";
        for (Tv tv : tvs) {
            getUrl = "http://api.hclyz.com:81/mf/" + tv.getAddress();
            long st = System.currentTimeMillis();
            ResponseEntity<String> ex = restTemplate.exchange(getUrl, HttpMethod.GET, new HttpEntity<>(null, null), String.class);
            long et = System.currentTimeMillis();
            long l = et - st;
            System.out.println("qingqiu" + l);
            String bodys = ex.getBody();
            JSONObject jsonObjects = JSONObject.parseObject(bodys);
            JSONArray zhubo = jsonObjects.getJSONArray("zhubo");
            List<Zhubo> zhubos = JSONArray.parseArray(zhubo.toJSONString(), Zhubo.class);

            StringBuilder stringBuilder = new StringBuilder();
            int i = 1;
            try {
                long startTime = System.currentTimeMillis();
                String address = tv.getAddress();
                String[] split = address.split("\\.");
                OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(split[0] + ".dpl"), "UTF-8");
                String s = "DAUMPLAYLIST\n" +
                        "playname = \n" +
                        "topindex = 0\n";
                out.write(s, 0, s.length());
                for (Zhubo zhubo1 : zhubos) {
                    stringBuilder.append(i + "*file*" + zhubo1.getAddress() + "\n" + i + "*title*" + zhubo1.getTitle() + "\n" + i + "*played*0"+"\n");
                    i++;
                }
                out.write(stringBuilder.toString(), 0, stringBuilder.toString().length());
                out.close();
                long endTime = System.currentTimeMillis();
                long time = endTime - startTime;
                System.out.println(time);
                System.out.println("?????????????????????");
            } catch (IOException e) {
            }
        }
    }
}