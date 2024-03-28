package com.jxdinfo.doc.common.util;

import org.apache.commons.io.FileUtils;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ffmpegUtil {

    static final public org.slf4j.Logger LOGGER = LoggerFactory.getLogger(ffmpegUtil.class);
    private  static String ffmpegEXE;


    public ffmpegUtil(String ffmpegEXE) {
        super();
        this.ffmpegEXE = ffmpegEXE;
    }
    public static void getThumb(String videoFilename, String thumbFilename, int width,
                                int height, int hour, int min, float sec) throws IOException,
            InterruptedException
    {
        ProcessBuilder processBuilder = new ProcessBuilder(ffmpegEXE, "-y",
                "-i", videoFilename, "-vframes", "1", "-ss", hour + ":" + min
                + ":" + sec, "-f", "mjpeg", "-s", width + "*" + height,
                "-an", thumbFilename);

        Process process = processBuilder.start();

        InputStream stderr = process.getErrorStream();
        InputStreamReader isr = new InputStreamReader(stderr);
        BufferedReader br = new BufferedReader(isr);
        String line;
        while ((line = br.readLine()) != null)
            ;
        process.waitFor();

        if(br != null)
            br.close();
        if(isr != null)
            isr.close();
        if(stderr != null)
            stderr.close();
    }
    public void convertor(String videoInputPath, String mp3InputPath, double seconds, String videoOutputPath) throws Exception {
        List<String> command = new ArrayList<>();
        command.add(ffmpegEXE);
        command.add("-i");
        command.add(videoInputPath);
        command.add("-i");
        command.add(mp3InputPath);
        command.add("-t");
        command.add(String.valueOf(seconds));
        command.add("-y");
        command.add(videoOutputPath);
        InputStream errorStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader br = null;
        try {
            ProcessBuilder builder = new ProcessBuilder(command);
          Process process  =builder.start();
            errorStream = process.getErrorStream();
            inputStreamReader = new InputStreamReader(errorStream);
            br = new BufferedReader(inputStreamReader);
            while (br.readLine() != null) {
            }
        } finally {
            if (br != null) {
                br.close();
            }
            if (inputStreamReader != null) {
                inputStreamReader.close();
            }
            if (errorStream != null) {
                errorStream.close();
            }
        }
    }
    public   synchronized  static void convetor(String videoInputPath, String videoOutPath) throws Exception {

        LOGGER.info("******************视频转换开始******************");
        List<String> command = new ArrayList<String>();
        command.add(ffmpegEXE);
        command.add("-i");
        command.add(videoInputPath);
        command.add("-vcodec");
        command.add("libx264");
        command.add("-max_muxing_queue_size");
        command.add("9999");
        //  --preset的参数主要调节编码速度和质量的平衡，有ultrafast、superfast、veryfast、faster、fast、medium、slow、slower、veryslow、placebo这10个选项，从快到慢。
        command.add("-preset");
        command.add("ultrafast");
        command.add(videoOutPath);
        ProcessBuilder builder = new ProcessBuilder(command);
        Process process = null;
        try {
            process = builder.start();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // 使用这种方式会在瞬间大量消耗CPU和内存等系统资源，所以这里我们需要对流进行处理
        InputStream errorStream = process.getErrorStream();
        InputStreamReader inputStreamReader = new InputStreamReader(errorStream);
        BufferedReader br = new BufferedReader(inputStreamReader);

        String line = "";
        while ((line = br.readLine()) != null) {
        }
        if (br != null) {
            br.close();
        }
        if (inputStreamReader != null) {
            inputStreamReader.close();
        }
        if (errorStream != null) {
            errorStream.close();
        }
        LOGGER.info("******************视频转换结束******************");
    }
    public   synchronized  static void convetorAvi(String videoInputPath, String videoOutPath) throws Exception {
        String pdfFilePathAvi = videoInputPath.substring(0, videoInputPath.lastIndexOf(".")) + "_new.avi";
        convetor(videoInputPath,  pdfFilePathAvi);
        File fileAvi = new File(pdfFilePathAvi);
        File fileOut = new File(videoOutPath);
        FileUtils.copyFile(fileAvi,fileOut);

    }

    public static void main(String[] args) {
        ffmpegUtil ffmpeg = new ffmpegUtil("D:\\install\\ffmpeg-4.3-win64-static\\bin\\ffmpeg.exe");
        try {
            ffmpegUtil.getThumb("C:\\knowledge\\downloadFile\\707080b54c7c4f828bfc093aee8106d1.mp4", "C:\\Users\\Administrator\\Desktop\\2.png", 800, 800, 0, 0, 1);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("over");


    }
}
