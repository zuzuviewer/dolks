package com.yk.Constants;

public interface Constant {

    // 根据系统得到的临时目录，如linux下的”/tmp“
    String TEMP_PATH = System.getProperty("java.io.tmpdir");
}
