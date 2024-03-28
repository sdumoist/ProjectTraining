package com.jxdinfo.constant;

public interface QuestionConstant {

    // 问答日志记录 类型，1问题，2回答，3评论回复，4追问，5追答
    public static final String QALOG_TYPE_QUE = "1";
    public static final String QALOG_TYPE_ANS = "2";
    public static final String QALOG_TYPE_COMMENT = "3";
    public static final String QALOG_TYPE_ADD_QUE = "4";
    public static final String QALOG_TYPE_ADD_ANS = "5";

    // 问答日志记录 操作，1新增，2删除，3查看，4点赞，5关注，6取消关注，
    // 7分享，8取消分享，9补充，10修改，11结束，12设为最佳，13附件下载, 14取消点赞
    public static final String QALOG_OPERATION_ADD = "1";
    public static final String QALOG_OPERATION_DEL = "2";
    public static final String QALOG_OPERATION_CHECK = "3";
    public static final String QALOG_OPERATION_AGREE = "4";
    public static final String QALOG_OPERATION_CANCEL_AGREE = "14";
    public static final String QALOG_OPERATION_FOLLOW = "5";
    public static final String QALOG_OPERATION_CANCEL_FOLLOW = "5";
    public static final String QALOG_OPERATION_SUPPLEMENT = "9";
    public static final String QALOG_OPERATION_EDIT = "10";
    public static final String QALOG_OPERATION_END = "11";
    public static final String QALOG_OPERATION_BEST = "12";

    // 是否有效 0 正常 1 删除
    public static final String VALID_FLAG_NORMAL = "0";
    public static final String VALID_FLAG_DELETE = "1";

    // 是否点赞 1 已赞 0：未点赞
    public static final String QA_AGREE = "1";
    public static final String QA_CANCEL_AGREE = "0";
}
