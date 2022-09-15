package com.usr.controller;

import com.usr.service.speech_syn;
import com.usr.service.voice_rec;
import com.usr.common.R;

import static com.usr.service.iflytek_detect.NewGenderJudge;
import static com.usr.service.picture_rec.Base64ToImage;
import static com.usr.service.picture_rec.GenderJudge;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {

    @Value("${locationWin:/tmp/tomcat_upload}")
    private String base_path;

    @CrossOrigin
    @PostMapping("/upload_aud")
    public R<String> upload_aud(MultipartFile file) throws IOException {//此处file与前端form表单中name属性一致
        log.info(file.toString());

        String res = null;

        voice_rec iatTool=new voice_rec("91218b71");
        try {
            //转存文件
            res = iatTool.RecognizePcmfileByte(file, base_path);
            //file.transferTo(new File(path));

        } catch (Exception e) {
            e.printStackTrace();
        }
        //str = IatTool.start(path);
        //System.out.println(str);
        //return R.success("hello");
        return R.success(res);
    }


    @CrossOrigin
    @PostMapping("/baidu_pic")
    public R<String> baidu_upload_pic(String PicStr) throws IOException {//此处file与前端form表单中name属性一致
        log.info("接受图片");
        String Gender = null;
        try {
            //转存文件
            Gender = GenderJudge(PicStr);
            //file.transferTo(new File(path));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return R.success(Gender);
    }

    @CrossOrigin
    @PostMapping("/iflytek_pic")
    public R<String> iflytek_upload_pic(String PicStr) throws IOException {//此处file与前端form表单中name属性一致
        log.info("接受图片");
        String Gender = null;
        try {
            //转存文件
            Gender = NewGenderJudge(PicStr);
            //file.transferTo(new File(path));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return R.success(Gender);
    }
    @CrossOrigin
    @PostMapping("/speech_syn")
    public R<String> speech_syn(String Speech) throws IOException{
        log.info("语音合成");
        String c = System.getProperty("user.dir");
        String imgFilePath = c + "\\src\\main\\java\\com\\usr\\upload";
        SimpleDateFormat df = new SimpleDateFormat("yyy-MM-dd|yHH:mm:ss");//设置日期格式
        String date = String.valueOf(System.currentTimeMillis());// new Date()为获取当前系统时间，也可使用当前时间戳
        speech_syn iatTool = new speech_syn("91218b71", base_path);
        try{
            iatTool.textToVoice(Speech, "语音转写"+date);
        }catch (Exception e){
            e.printStackTrace();
        }
        return R.success("转换成功");
    }

    @GetMapping("/download")
    public String download(String name, HttpServletResponse response){
        // 输入流读取文件
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(base_path+name));
            ServletOutputStream outputStream = response.getOutputStream();

            response.setContentType("image/jpeg");

            int len=0;
            byte[] bytes=new byte[1024];
            while ((len = fileInputStream.read(bytes))!=-1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }
            outputStream.close();
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //
        return "听我说谢谢你";
    }
}
