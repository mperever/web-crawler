package com.github.mperever.common.dto;

import java.io.Serializable;

/**
 * Represents response for {@link SaveTaskResultRequest}.
 *
 * @author mperever
 */
public class SaveTaskResultResponse implements Serializable, ErrorKeeper
{
    private static final long serialVersionUID = 1L;

    private Exception error;

    public SaveTaskResultResponse()
    {
    }

    public SaveTaskResultResponse( Exception error )
    {
        this.error = error;
    }

    public boolean hasError()
    {
        return error != null;
    }

    public Exception getError()
    {
        return error;
    }
}