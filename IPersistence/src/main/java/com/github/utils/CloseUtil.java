package com.github.utils;

/**
 * <p>
 * 关闭
 * </p>
 *
 * @author isaac 2020/8/21 3:05
 * @since 1.0.0
 */
public final class CloseUtil {

    private CloseUtil() {
        throw new IllegalStateException("Utils");
    }

    public static void close(AutoCloseable... closeables) {
        for (AutoCloseable closeable : closeables) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (Exception e) {
                    // ignore
                }
            }
        }
    }

}
