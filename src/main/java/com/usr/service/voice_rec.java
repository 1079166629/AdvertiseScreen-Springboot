package com.usr.service;

import com.iflytek.cloud.speech.SpeechRecognizer;
import com.iflytek.cloud.speech.SpeechConstant;
import com.iflytek.cloud.speech.SpeechUtility;
import com.iflytek.cloud.speech.RecognizerListener;
import com.iflytek.cloud.speech.RecognizerResult;
import com.iflytek.cloud.speech.SpeechError;

//import org.json.JSONArray;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
//import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.io.File;


public class voice_rec {

    private static Logger LOGGER = LoggerFactory.getLogger(voice_rec.class);

    private StringBuilder curRet;

    private SpeechRecognizer recognizer;

    private Object lock = new Object();

    public voice_rec(String appId) {
        LOGGER.info("------Speech Utility init iat------");
        SpeechUtility.createUtility(SpeechConstant.APPID + "=" + appId);
    }

    public String RecognizePcmfileByte(MultipartFile audioFile, String base_path) {
        FileInputStream fileInputStream;
        curRet = new StringBuilder();

        try {
            if (recognizer == null) {
                //以下设置转换参数
                recognizer = SpeechRecognizer.createRecognizer();
                recognizer.setParameter(com.iflytek.cloud.speech.SpeechConstant.AUDIO_SOURCE, "-1");
                recognizer.setParameter(SpeechConstant.RESULT_TYPE, "plain");
                recognizer.setParameter(SpeechConstant.VAD_BOS, "2000");//前端点超时，
                recognizer.setParameter(SpeechConstant.VAD_EOS, "10000");//后端点超时要与运行SDK时配置的一样
            }

            recognizer.startListening(recListener);

            base_path += "/test_wav.pcm";
            System.out.println(base_path);
            File file = new File(new File(base_path).getAbsolutePath());
            byte[] buffer = audioFile.getBytes();
            audioFile.transferTo(file);
            //fileInputStream = new FileInputStream(base_path);



            //byte[] pcmbyte = Arrays.copyOfRange(buffer, 44, buffer.length);

            if (buffer == null || buffer.length == 0) {
                LOGGER.error("no audio avaible!");
                recognizer.cancel();
            } else {
                int lenRead = buffer.length;
                System.out.println("文件长度" + buffer.length);
                //ystem.out.println("你好");
                recognizer.writeAudio(buffer, 0, lenRead);
                recognizer.stopListening();
                synchronized (lock) {
                    lock.wait();//主线程等待
                }

                //System.out.println(str);
                //System.out.println(str);
                //System.out.println(curRet.toString());
                //return curRet.toString();
                return curRet.toString();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static byte[] InputStreamToByte(FileInputStream fis) throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        long size = fis.getChannel().size();
        byte[] buffer = null;
        if (size <= Integer.MAX_VALUE) {
            buffer = new byte[(int) size];
        } else {
            buffer = new byte[8];
            for (int ix = 0; ix < 8; ++ix) {
                int offset = 64 - (ix + 1) * 8;
                buffer[ix] = (byte) ((size >> offset) & 0xff);
            }
        }
        int len;
        while ((len = fis.read(buffer)) != -1) {
            byteStream.write(buffer, 0, len);
        }
        byte[] data = byteStream.toByteArray();
        IOUtils.closeQuietly(byteStream);
        return data;
    }
    public static String getEncoding(String str) {
        String encode = "GB2312";
        try {
            if (isEncoding(str, encode)) { // 判断是不是GB2312
                return encode;
            }
        } catch (Exception exception) {
        }
        encode = "ISO-8859-1";
        try {
            if (isEncoding(str, encode)) { // 判断是不是ISO-8859-1
                return encode;
            }
        } catch (Exception exception1) {
        }
        encode = "UTF-8";
        try {
            if (isEncoding(str, encode)) { // 判断是不是UTF-8
                return encode;
            }
        } catch (Exception exception2) {
        }
        encode = "GBK";
        try {
            if (isEncoding(str, encode)) { // 判断是不是GBK
                return encode;
            }
        } catch (Exception exception3) {
        }
        return ""; // 如果都不是，说明输入的内容不属于常见的编码格式。
    }

    public static boolean isEncoding(String str, String encode) {
        try {
            if (str.equals(new String(str.getBytes(), encode))) {
                return true;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return false;
    }
    private RecognizerListener recListener = new RecognizerListener() {
        @Override
        public void onBeginOfSpeech() {
            LOGGER.info("onBeginOfSpeech enter");
        }

        @Override
        public void onEndOfSpeech() {
            LOGGER.info("onEndOfSpeech enter");
        }

        /**
         * 获取听写结果
         */
        @Override
        public void onResult(RecognizerResult results, boolean islast) {
            LOGGER.info("onResult enter");
            //可以用json解析器解析为json格式，，参考AsrSpeechView中inResult函数
            String text = results.getResultString();
            curRet.append(text);
            System.out.println("解析结果"+curRet.toString());
            if (islast) {
                synchronized (lock) {
                    lock.notify();//子线程唤醒
                }
            }
        }

        @Override
        public void onVolumeChanged(int volume) {
            LOGGER.info("onVolumeChanged volume=" + volume);
        }

        @Override
        public void onError(SpeechError error) {
            LOGGER.error("onError enter");
            if (null != error) {
                LOGGER.error("onError Code：" + error.getErrorCode() + "," + error.getErrorDescription(true));
            }
        }

        @Override
        public void onEvent(int eventType, int arg1, int agr2, String msg) {
            LOGGER.info("onEvent enter");
            //以下代码用于调试，如果出现问题可以将sid提供给讯飞开发者，用于问题定位排查
			/*if(eventType == SpeechEvent.EVENT_SESSION_ID) {
				DebugLog.Log("sid=="+msg);
			}*/
        }
    };

}
