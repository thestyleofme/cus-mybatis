package com.github.io;

import java.io.InputStream;

/**
 * <p>
 * 加载sqlMapConfig.xml
 * </p>
 * 
 * @author isaac 2020/8/20 23:16
 * @since 1.0.0
 */
public class Resources {

    /**
     * 根据配置文件路径，将配置文件加载成字节输入流，储存在内存中
     * @param path 配置文件路径
     * @return InputStream
     */
    public static InputStream getResourceAsStream(String path){
        return Resources.class.getClassLoader().getResourceAsStream(path);
    }
}
