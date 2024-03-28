package com.jxdinfo.doc.semanticAnalysis.service;

/**
 * 语义分析-服务类
 */
public interface SemanticAnalysisService {

    /**
     * 传递文件建立语义分析模型
     * @param filePath 文件路径
     * @param label 文件标签
     * @return 是否成功
     */
    boolean uploadToAnalyse(String fileName, String filePath, String label);

    /**
     * 获取文件语义分析后生成的标签
     * @param filePath 文件路径
     * @return 标签
     */
    String getLabelAnalysis(String fileName, String filePath);
}
