package com.usr.service;

import com.baidu.aip.face.AipFace;
import java.io.*;
import org.json.JSONObject;

import org.springframework.util.StringUtils;

import javax.imageio.stream.FileImageOutputStream;
import java.util.Base64;
import java.util.HashMap;


public class picture_rec {
    public static void Base64ToImage(String imgStr, String path) { // 对字节数组字符串进行Base64解码并生成图片

        String c = System.getProperty("user.dir");
        if (StringUtils.isEmpty(imgStr)) // 图像数据为空
            System.out.println("空文件");

        //Decoder decoder = new Decoder();
        //String imgFilePath = c + "\\src\\main\\java\\com\\ysp\\upload\\test.png";
        path += "/test1.png";
        System.out.println(path);
        try {
            // Base64解码
            byte[] base64decodedBytes = Base64.getMimeDecoder().decode(imgStr);
            FileImageOutputStream imageOutput = new FileImageOutputStream(new File(path));
            imageOutput.write(base64decodedBytes);
            imageOutput.close();
        } catch (Exception e) {
            System.out.println("Exception: " + e);
            e.printStackTrace();
        }

    }

    public static String GenderJudge(String str) {
        //设置APPID/AK/SK
        final String APP_ID = "26988694";
        final String API_KEY = "fv2yHHTOvtIp622f092cCmvl";
        final String SECRET_KEY = "XekQEBjBoERsQFPhuWi2Ghk3k3UWnZoF";
        try {
            // 初始化一个AipFace
            AipFace client = new AipFace(APP_ID, API_KEY, SECRET_KEY);

            // 可选：设置网络连接参数
            client.setConnectionTimeoutInMillis(2000);
            client.setSocketTimeoutInMillis(60000);

            // 可选：设置代理服务器地址, http和socket二选一，或者均不设置
            //int proxy_port;
            //client.setHttpProxy("proxy_host", proxy_port);  // 设置http代理
            //client.setSocketProxy("proxy_host", proxy_port);  // 设置socket代理

            // 调用接口
            //byte[] bytes = FileUtil.readFileByBytes("D:\\test\\slw.jpg");
            //String img = Base64Util.encode(bytes);
            String image = str;
            String imageType = "BASE64";

            HashMap<String, String> options = new HashMap<String, String>();
            options.put("face_field", "gender");
            options.put("max_face_num", "2");
            options.put("face_type", "LIVE");
            options.put("liveness_control", "LOW");


            // 人脸检测
            JSONObject res = client.detect(image, imageType, options);
            //System.out.println("解析内容" + res.toString());

            try {
                JSONObject person = new JSONObject(res.getJSONObject("result").getJSONArray("face_list").get(0).toString());
                JSONObject Gender = new JSONObject(person.get("gender").toString());
                System.out.println("解析内容" + Gender.get("type"));
                return Gender.get("type").toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "No detect,Please try again.";
    }
}
