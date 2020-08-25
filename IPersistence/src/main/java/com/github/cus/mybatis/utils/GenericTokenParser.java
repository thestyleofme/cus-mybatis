package com.github.cus.mybatis.utils;

/**
 * <p>
 * mybatis源码里扣的
 * </p>
 *
 * @author isaac 2020/8/21 2:11
 * @since 1.0.0
 */
public class GenericTokenParser {

    /**
     * 开始标记
     */
    private final String openToken;
    /**
     * 结束标记
     */
    private final String closeToken;
    /**
     * 标记处理器
     */
    private final TokenHandler handler;

    public GenericTokenParser(String openToken, String closeToken, TokenHandler handler) {
        this.openToken = openToken;
        this.closeToken = closeToken;
        this.handler = handler;
    }

    /**
     * 解析${}和#{}
     * 该方法主要实现了配置文件、脚本等片段中占位符的解析、处理工作，并返回最终需要的数据。
     * 其中，解析工作由该方法完成，处理工作是由处理器handler的handleToken()方法来实现
     *
     * @param text text
     * @return String
     */
    public String parse(String text) {
        // 验证参数问题，如果是null，就返回空字符串。
        if (text == null || text.isEmpty()) {
            return "";
        }

        // 下面继续验证是否包含开始标签，如果不包含，默认不是占位符，直接原样返回即可，否则继续执行。
        int start = text.indexOf(openToken);
        if (start == -1) {
            return text;
        }

        // 把text转成字符数组src，并且定义默认偏移量offset=0、存储最终需要返回字符串的变量builder，
        // text变量中占位符对应的变量名expression。判断start是否大于-1(即text中是否存在openToken)，如果存在就执行下面代码
        char[] src = text.toCharArray();
        int offset = 0;
        final StringBuilder builder = new StringBuilder();
        StringBuilder expression = null;
        while (start > -1) {
            // 判断如果开始标记前如果有转义字符，就不作为openToken进行处理，否则继续处理
            if (start > 0 && src[start - 1] == '\\') {
                builder.append(src, offset, start - offset - 1).append(openToken);
                offset = start + openToken.length();
            } else {
                // 重置expression变量，避免空指针或者老数据干扰。
                if (expression == null) {
                    expression = new StringBuilder();
                } else {
                    expression.setLength(0);
                }
                builder.append(src, offset, start - offset);
                offset = start + openToken.length();
                int end = text.indexOf(closeToken, offset);
                while (end > -1) {
                    // 存在结束标记时
                    if (end > offset && src[end - 1] == '\\') {
                        // 如果结束标记前面有转义字符时
                        // this close token is escaped. remove the backslash and continue.
                        expression.append(src, offset, end - offset - 1).append(closeToken);
                        offset = end + closeToken.length();
                        end = text.indexOf(closeToken, offset);
                    } else {
                        // 不存在转义字符，即需要作为参数进行处理
                        expression.append(src, offset, end - offset);
                        break;
                    }
                }
                if (end == -1) {
                    // close token was not found.
                    builder.append(src, start, src.length - start);
                    offset = src.length;
                } else {
                    //首先根据参数的key（即expression）进行参数处理，返回?作为占位符
                    builder.append(handler.handleToken(expression.toString()));
                    offset = end + closeToken.length();
                }
            }
            start = text.indexOf(openToken, offset);
        }
        if (offset < src.length) {
            builder.append(src, offset, src.length - offset);
        }
        return builder.toString();
    }
}
