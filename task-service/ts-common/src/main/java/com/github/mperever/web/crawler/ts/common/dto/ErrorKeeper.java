package com.github.mperever.web.crawler.ts.common.dto;

import java.io.Serializable;

public interface ErrorKeeper extends Serializable
{
    boolean hasError();

    Exception getError();
}