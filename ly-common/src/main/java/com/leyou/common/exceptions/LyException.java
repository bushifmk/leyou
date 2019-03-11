package com.leyou.common.exceptions;

import com.leyou.common.enums.ExceptionEnum;
import lombok.Getter;

/**
 * @author bushifeng
 * @version v1.0
 * @date 2019/3/3 16:38
 * @description TODO
 **/
@Getter
public class LyException extends RuntimeException {
    private int status;

    public LyException(ExceptionEnum em) {
        super(em.msg());
        this.status = em.status();
    }

    public LyException(ExceptionEnum em, Throwable cause) {
        super(em.msg(), cause);
        this.status = em.status();
    }
}
