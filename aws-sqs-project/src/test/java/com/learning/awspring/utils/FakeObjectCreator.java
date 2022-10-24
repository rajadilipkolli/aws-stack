package com.learning.awspring.utils;

import com.learning.awspring.model.SNSMessagePayload;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.RandomStringUtils;

@UtilityClass
public class FakeObjectCreator {

    public static SNSMessagePayload createMessage() {
        SNSMessagePayload SNSMessagePayload = new SNSMessagePayload();
        SNSMessagePayload.setId(RandomStringUtils.randomNumeric(3));
        SNSMessagePayload.setMessageBody(RandomStringUtils.randomAlphanumeric(100));
        return SNSMessagePayload;
    }
}
