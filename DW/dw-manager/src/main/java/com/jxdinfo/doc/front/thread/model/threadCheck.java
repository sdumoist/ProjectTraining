package com.jxdinfo.doc.front.thread.model;


import com.baomidou.mybatisplus.annotation.TableName;

@TableName("threadCheck")
public class threadCheck {
    /**
     * 线程名
     */
    private  String threadName;
    /**
     * 线程状态
     */
    private String threadState;

    /**
     * 调用线程方法的类名、方法名、文件名以及调用的行数
     */
    private StackTraceElement stackTraceElement;


    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public StackTraceElement getStackTraceElement() {
        return stackTraceElement;
    }

    public void setStackTraceElement(StackTraceElement stackTraceElement) {
        this.stackTraceElement = stackTraceElement;
    }

    public String getThreadState() {
        return threadState;
    }

    public void setThreadState(String threadState) {
        this.threadState = threadState;
    }
}
