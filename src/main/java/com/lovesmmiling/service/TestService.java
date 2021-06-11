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
    public void getFile() throws IOException {
        String url = "http://api.hclyz.com:81/mf/json.txt";
        ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(null, null), String.class);
        String body = exchange.getBody();
        JSONObject jsonObject = JSONObject.parseObject(body);
        JSONArray pingtai = jsonObject.getJSONArray("pingtai");
        List<Tv> tvs = JSONArray.parseArray(pingtai.toJSONString(), Tv.class);

        String getUrl = "";
        long startTime = System.currentTimeMillis();
        OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream("tv.m3u"), "UTF-8");
        String s = "#EXTM3U\n";
        out.write(s, 0, s.length());
        for (Tv tv : tvs) {
            getUrl = "http://api.hclyz.com:81/mf/" + tv.getAddress();
            ResponseEntity<String> ex = restTemplate.exchange(getUrl, HttpMethod.GET, new HttpEntity<>(null, null), String.class);
            String bodys = ex.getBody();
            JSONObject jsonObjects = JSONObject.parseObject(bodys);
            JSONArray zhubo = jsonObjects.getJSONArray("zhubo");
            List<Zhubo> zhubos = JSONArray.parseArray(zhubo.toJSONString(), Zhubo.class);
            StringBuilder stringBuilder = new StringBuilder();
            try {
                for (Zhubo zhubo1 : zhubos) {
                    stringBuilder.append("#EXTINF:-1 ,"+zhubo1.getTitle()+"\n"+zhubo1.getAddress()+"\n");
                }
                out.write(stringBuilder.toString(), 0, stringBuilder.toString().length());
            } catch (IOException e) {

            }
        }
        out.close();
        long endTime = System.currentTimeMillis();
        long l = endTime - startTime;
        System.out.println(l);
        System.out.println("文件创建成功！");
    }
}