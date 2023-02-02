package com.learning.awspring.utils;

import com.learning.awspring.model.SQSMessagePayload;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.RandomStringUtils;

@UtilityClass
public class FakeObjectCreator {

    public static SQSMessagePayload createMessage() {
        return new SQSMessagePayload(
                RandomStringUtils.randomNumeric(3), RandomStringUtils.randomAlphanumeric(100));
    }
}
