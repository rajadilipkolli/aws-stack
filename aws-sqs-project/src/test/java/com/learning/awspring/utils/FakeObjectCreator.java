package com.learning.awspring.utils;

import com.learning.awspring.model.SNSMessagePayload;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.RandomStringUtils;

@UtilityClass
public class FakeObjectCreator {

    public static SNSMessagePayload createMessage() {
        return new SNSMessagePayload(
                RandomStringUtils.randomNumeric(3), RandomStringUtils.randomAlphanumeric(100));
    }
}
