package com.usr.service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.usr.utils.FileUtil;
import com.usr.utils.HttpUtil;
import org.json.JSONArray;


import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;

public class iflytek_detect {
    public static String strer="";

    public static String NewGenderJudge(String str) throws Exception {
        strer=str;
        iflytek_detect demo = new iflytek_detect();
        ResponseData respData = demo.faceContrast(str);
        //ResponseData respData = demo.faceContrast(Property.imagePath1);
        if (respData!=null && respData.getPayLoad().getFace_detect_result() != null) {
            String textBase64 = respData.getPayLoad().getFace_detect_result().getText();
            String text = new String(Base64.getDecoder().decode(textBase64));
            //System.out.println("人脸检测及属性分析结果(text)base64解码后：");
            //System.out.println(text);
            if(text.contains("gender")) ;

            JSONObject personObject =  JSON.parseObject(text);
            if(text.contains("gender")){
                JSONObject person = (JSONObject) personObject.get("face_1");
                JSONObject property = (JSONObject) person.get("property");
                String gender = property.get("gender").toString();
                if(gender.equals("0")) return "male";
                else if (gender.equals("1")) {
                    return "female";
                }
                else return gender;
            }

        }
        return null;
    }

    class Property {
        public final static  String requestUrl =  "https://api.xf-yun.com/v1/private/s67c9c78c";
        public final static  String appid ="91218b71"; //请填写控制台获取的APPID,
        public final static  String apiSecret="YjAwMjQ5MjVlOTE2ZjMyMTdlNDc4YmI0";  //请填写控制台获取的APISecret;
        public final static  String apiKey = "c977a471afb1b93c0f9be23d0639e017";  //请填写控制台获取的APIKey
        //public final static  String imagePath1="D:\\test\\slw.png";  //请填写要检测的图片路径
        public final static  String serviceId= "s67c9c78c";
    }

    public String getXParam(String imageBase641) {//(2)
        JsonObject jso = new JsonObject();

        /** header **/
        JsonObject header = new JsonObject();
        header.addProperty("app_id", Property.appid);
        header.addProperty("status", 3);

        jso.add("header", header);

        /** parameter **/
        JsonObject parameter = new JsonObject();
        JsonObject service = new JsonObject();
        service.addProperty("service_kind", "face_detect");

        //service.addProperty("detect_points", "1");//检测特征点
        service.addProperty("detect_property", "1");//检测人脸属性

        JsonObject faceCompareResult = new JsonObject();
        faceCompareResult.addProperty("encoding", "utf8");
        faceCompareResult.addProperty("format", "json");
        faceCompareResult.addProperty("compress", "raw");
        service.add("face_detect_result", faceCompareResult);
        parameter.add(Property.serviceId, service);
        jso.add("parameter", parameter);

        /** payload **/
        JsonObject payload = new JsonObject();
        JsonObject inputImage1 = new JsonObject();
        inputImage1.addProperty("encoding", "png");
        inputImage1.addProperty("image", imageBase641);
        payload.add("input1", inputImage1);

        jso.add("payload", payload);
        //System.out.println(jso.toString());
        return jso.toString();
    }


    //读取image
    private byte[] readImage(String imagePath) throws IOException {
        InputStream is = new FileInputStream(imagePath);
        byte[] imageByteArray1 = FileUtil.read(imagePath);
        //return is.readAllBytes();
        return imageByteArray1;
    }

    public ResponseData faceContrast(String str) throws Exception {//(1)

        String url = assembleRequestUrl(Property.requestUrl, Property.apiKey, Property.apiSecret);
        String imageBase641 =str;
        //String imageBase641 = Base64.getEncoder().encodeToString(readImage(imageFirstUrl));//读取地址，可以直接传入base64数据，删掉后面的操作
        //String imageEncoding1 = imageFirstUrl.substring(imageFirstUrl.lastIndexOf(".") + 1);

        //System.out.println("url:"+url);
        return handleFaceContrastRes(url, getXParam(imageBase641));
        //return handleFaceContrastRes(url, getXParam(imageBase641, imageEncoding1));
    }

    public static final Gson json = new Gson();

    public ResponseData handleFaceContrastRes(String url, String bodyParam) {

        Map<String,String> headers = new HashMap<>();
        headers.put("Content-type", "application/json");
        String result = HttpUtil.doPost2(url, headers,bodyParam);
        if (result != null) {
            //System.out.println("人脸检测及属性分析接口调用结果：" + result);
            return json.fromJson(result, ResponseData.class);
        } else {
            return null;
        }
    }


    //构建url
    public static String assembleRequestUrl(String requestUrl, String apiKey, String apiSecret) {
        URL url = null;
        // 替换调schema前缀 ，原因是URL库不支持解析包含ws,wss schema的url
        String  httpRequestUrl = requestUrl.replace("ws://", "http://").replace("wss://","https://" );
        try {
            url = new URL(httpRequestUrl);
            //获取当前日期并格式化
            SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
            format.setTimeZone(TimeZone.getTimeZone("GMT"));
            String date = format.format(new Date());

            String host = url.getHost();
            if (url.getPort()!=80 && url.getPort() !=443){
                host = host +":"+String.valueOf(url.getPort());
            }
            StringBuilder builder = new StringBuilder("host: ").append(host).append("\n").//
                    append("date: ").append(date).append("\n").//
                    append("POST ").append(url.getPath()).append(" HTTP/1.1");
            Charset charset = Charset.forName("UTF-8");
            Mac mac = Mac.getInstance("hmacsha256");
            SecretKeySpec spec = new SecretKeySpec(apiSecret.getBytes(charset), "hmacsha256");
            mac.init(spec);
            byte[] hexDigits = mac.doFinal(builder.toString().getBytes(charset));
            String sha = Base64.getEncoder().encodeToString(hexDigits);

            String authorization = String.format("api_key=\"%s\", algorithm=\"%s\", headers=\"%s\", signature=\"%s\"", apiKey, "hmac-sha256", "host date request-line", sha);
            String authBase = Base64.getEncoder().encodeToString(authorization.getBytes(charset));
            return String.format("%s?authorization=%s&host=%s&date=%s", requestUrl, URLEncoder.encode(authBase), URLEncoder.encode(host), URLEncoder.encode(date));

        } catch (Exception e) {
            throw new RuntimeException("assemble requestUrl error:"+e.getMessage());
        }
    }

    public static class ResponseData {
        private Header header;
        private PayLoad payload;
        public Header getHeader() {
            return header;
        }
        public PayLoad getPayLoad() {
            return payload;
        }
    }
    public static class Header {
        private int code;
        private String message;
        private String sid;
        public int getCode() {
            return code;
        }
        public String getMessage() {
            return message;
        }
        public String getSid() {
            return sid;
        }
    }
    public static class PayLoad {
        private FaceResult face_detect_result;
        public FaceResult getFace_detect_result() {
            return face_detect_result;
        }
    }
    public static class FaceResult {
        private String compress;
        private String encoding;
        private String format;
        private String text;
        public String getCompress() {
            return compress;
        }
        public String getEncoding() {
            return encoding;
        }
        public String getFormat() {
            return format;
        }
        public String getText() {
            return text;
        }
    }
}
