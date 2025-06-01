package com.learning.awspring.model.response;

import java.net.URL;

public record SignedURLResponse(String url) {

    public SignedURLResponse(URL signedUrl) {
        this(signedUrl.toString()); // Call the primary constructor with the URL's string
        // representation
    }
}
