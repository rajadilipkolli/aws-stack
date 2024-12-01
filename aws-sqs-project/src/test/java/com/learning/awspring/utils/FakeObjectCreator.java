package com.learning.awspring.utils;

import com.learning.awspring.model.SQSMessagePayload;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.RandomStringUtils;

@UtilityClass
public class FakeObjectCreator {

    public static SQSMessagePayload createMessage() {
        return new SQSMessagePayload(
                RandomStringUtils.secure().nextNumeric(3),
                RandomStringUtils.secure().nextAlphanumeric(100));
    }
}
