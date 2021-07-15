package com.yk.utils;

public class FileUtils {

    /**
     * 路径是否以文件夹分隔符结束
     *
     * @param path
     * @return
     */
    public static boolean isEndWithPathSeparator(final String path) {
        if (OSUtils.isLinux() || OSUtils.isMacOS()) {
            return path.endsWith("/");
        }
        if (OSUtils.isWindows()) {
            return path.endsWith("/") || path.endsWith("\\");
        }
        return false;
    }

    /**
     * 确保路径以分隔符结束，已经有则不处理，没有则增加
     *
     * @param path
     * @return
     */
    public static String AddPathSeparator(final String path) {
        if (isEndWithPathSeparator(path)) {
            return path;
        }
        return path + "/";
    }
}
