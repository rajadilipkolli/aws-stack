package com.learning.awslambda.model.query;

public record FindActorsQuery(int pageNo, int pageSize, String sortBy, String sortDir) {}
