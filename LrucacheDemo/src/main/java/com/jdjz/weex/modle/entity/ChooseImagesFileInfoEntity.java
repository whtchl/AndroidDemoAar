package com.jdjz.weex.modle.entity;

import java.util.List;

public class ChooseImagesFileInfoEntity {
    private List<String> tempFilePaths;
    private List<TempFile> tempFiles;



    public List<String> getTempFilePaths() {
        return tempFilePaths;
    }

    public void setTempFilePaths(List<String> tempFilePaths) {
        this.tempFilePaths = tempFilePaths;
    }

    public List<TempFile> getTempFiles() {
        return tempFiles;
    }

    public void setTempFiles(List<TempFile> tempFiles) {
        this.tempFiles = tempFiles;
    }
}
