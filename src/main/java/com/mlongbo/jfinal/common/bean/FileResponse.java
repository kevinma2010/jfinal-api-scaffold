package com.mlongbo.jfinal.common.bean;

import java.util.List;

/**
 * @author malongbo
 * @date 2015/1/28
 * @package com.pet.project.bean
 */
public class FileResponse extends DatumResponse {
    /*
    * 保存上传失败的文件名
     */
    private List<String> failed;

    public List<String> getFailed() {
        return failed;
    }

    public FileResponse setFailed(List<String> failed) {
        this.failed = failed;
        return this;
    }
}
