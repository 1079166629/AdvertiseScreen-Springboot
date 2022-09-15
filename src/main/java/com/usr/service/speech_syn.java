package com.usr.service;

import com.iflytek.cloud.speech.*;
import com.usr.tools.PcmToMp3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.UUID;

public class speech_syn {
    private static Logger LOGGER = LoggerFactory.getLogger(speech_syn.class);

    private Object lock = new Object();

    // 语音合成对象
    private SpeechSynthesizer mTts;

    private String ttsPcmDir;


    public speech_syn(String appId, String ttsPcmDir) {
        LOGGER.info("------Speech Utility init tts------");
        this.ttsPcmDir = ttsPcmDir;
        SpeechUtility.createUtility(SpeechConstant.APPID + "=" + appId);
        // 初始化合成对象
        mTts = SpeechSynthesizer.createSynthesizer();
        if (mTts != null) {
            // 设置发音人
            mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");
            mTts.setParameter(SpeechConstant.SPEED, "150");//设置语速
            mTts.setParameter(SpeechConstant.VOLUME, "80");//设置音量，范围0~100
        } else {
            LOGGER.error("tts handler init fail");
        }
    }

    public String textToVoice(String text,String fileName) {
        try {
            String pcmPath, mp3Path = null;
            if (null != fileName){
                pcmPath = ttsPcmDir + File.separator + fileName + ".pcm";
                mp3Path = ttsPcmDir + File.separator + fileName + ".mp3";
                //System.out.println(pcmPath);
            }else{
                pcmPath = ttsPcmDir + File.separator + UUID.randomUUID() + ".pcm";
            }

            // 设置合成音频保存位置（可自定义保存位置），默认不保存
            mTts.synthesizeToUri(text, pcmPath, mSynListener);
            synchronized (lock) {
                lock.wait();
            }
            PcmToMp3.convertAudioFiles(pcmPath, mp3Path);
            return pcmPath;
        } catch (Exception e) {
            LOGGER.error("textToVoice get exception:" + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 合成监听器
     */
    SynthesizeToUriListener mSynListener = new SynthesizeToUriListener() {

        public void onBufferProgress(int progress) {
            LOGGER.info("*************合成进度*************" + progress);

        }

        public void onSynthesizeCompleted(String uri, SpeechError error) {
            if (error == null) {
                LOGGER.info("*************合成成功*************");
                LOGGER.info("合成音频生成路径：" + uri);
            } else {
                LOGGER.info("******合成失败*******" + error.getErrorCode()
                        + "*************");
            }
            synchronized (lock) {
                LOGGER.info("通知合成成功");
                lock.notify();
            }

        }


        @Override
        public void onEvent(int eventType, int arg1, int arg2, int arg3, Object obj1, Object obj2) {}

    };
}
