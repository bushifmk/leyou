package com.leyou.common.vo;

import com.leyou.common.exceptions.LyException;
import lombok.Getter;
import org.joda.time.DateTime;

/**
 * @author bushifeng
 * @version v1.0
 * @date 2019/3/3 16:52
 * @description TODO
 **/
@Getter
public class ExceptionResult {
    private int status;
    private String message;
    private String timestamp;
    public ExceptionResult(LyException e){
        this.status=e.getStatus();
        this.message=e.getMessage();
        this.timestamp= DateTime.now().toString("yyyy-MM-dd HH:mm:ss");
    }

    public ExceptionResult(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
