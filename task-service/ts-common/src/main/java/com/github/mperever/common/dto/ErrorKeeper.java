package com.github.mperever.common.dto;

import java.io.Serializable;

public interface ErrorKeeper extends Serializable
{
    boolean hasError();

    Exception getError();
}