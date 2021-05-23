package io.daff.uacs.core.enums;

/**
 * 文件上传的渠道
 *
 * @author daffupman
 * @since 2020/9/5
 */
public enum FileUploadChannelEnum {

    /**
     * 文件上传到tomcat
     */
    TOMCAT,

    /**
     * 文件上传到fastdfs服务器
     */
    FAST_DFS,

    /**
     * 文件上传到oss
     */
    OSS;

    /**
     * 构建FileUploadChannel
     */
    public static FileUploadChannelEnum buildChannel(String channel) {
        for (FileUploadChannelEnum value : values()) {
            if (value.name().equalsIgnoreCase(channel)) {
                return value;
            }
        }
        throw new IllegalArgumentException("channel is not supported");
    }
}
