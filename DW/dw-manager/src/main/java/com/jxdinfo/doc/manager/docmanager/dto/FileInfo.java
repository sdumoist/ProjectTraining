package com.jxdinfo.doc.manager.docmanager.dto;

/**
 * @author dushitaoyuan 可以从文件id中获取文件的文件信息
 */
public class FileInfo {

    private Long file_size;// 文件大小

    private Long create_timestamp;// 创建时间戳

    private Long crc32;// crc32

    public Long getFile_size() {
        return file_size;
    }

    public void setFile_size(Long file_size) {
        this.file_size = file_size;
    }

    public Long getCreate_timestamp() {
        return create_timestamp;
    }

    public void setCreate_timestamp(Long create_timestamp) {
        this.create_timestamp = create_timestamp;
    }

    public Long getCrc32() {
        return crc32;
    }

    public void setCrc32(Long crc32) {
        this.crc32 = crc32;
    }

}
