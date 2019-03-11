package com.leyou.common.advice;

import com.leyou.common.exceptions.LyException;
import com.leyou.common.vo.ExceptionResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @author bushifeng
 * @version v1.0
 * @date 2019/3/3 16:26
 * @description TODO
 **/
@ControllerAdvice
public class CommonExceptionAdvice {
    @ExceptionHandler(LyException.class)
    public ResponseEntity<ExceptionResult> handleException(LyException e){
        return ResponseEntity.status(e.getStatus()).body(new ExceptionResult(e));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ExceptionResult> handleException(RuntimeException e){
        return ResponseEntity.status(500).body(new ExceptionResult(500,e.getMessage()));
    }
}
