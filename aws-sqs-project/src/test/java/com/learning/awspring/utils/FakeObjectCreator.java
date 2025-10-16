package com.learning.awspring.utils;

import com.learning.awspring.model.SQSMessagePayload;
import org.apache.commons.lang3.RandomStringUtils;

public final class FakeObjectCreator {

    private FakeObjectCreator() {
        // private constructor to prevent instantiation
    }

    public static SQSMessagePayload createMessage() {
        return new SQSMessagePayload(
                RandomStringUtils.secure().nextNumeric(3),
                RandomStringUtils.secure().nextAlphanumeric(100));
    }
}
