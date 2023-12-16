package com.frn.findlovebackend;

import com.frn.findlovebackend.common.BaseResponse;
import com.frn.findlovebackend.common.ResultUtils;
import com.frn.findlovebackend.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Service;

import static com.frn.findlovebackend.common.ErrorCode.PARAM_ERROR;

@SpringBootTest
@Slf4j
class FindloveBackendApplicationTests {

    @Test
    void exceptionTest() {
        BusinessException e = new BusinessException(PARAM_ERROR);
        log.error("businessException: " + e.getMessage(), e);
        BaseResponse error = ResultUtils.error(e.getCode(), e.getMessage());

    }

}
