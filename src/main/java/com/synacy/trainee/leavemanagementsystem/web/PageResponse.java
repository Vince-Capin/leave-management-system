package com.synacy.trainee.leavemanagementsystem.web;

import java.util.List;

public record PageResponse<T>(int totalCount, int pageNumber, List<T> content) {}
