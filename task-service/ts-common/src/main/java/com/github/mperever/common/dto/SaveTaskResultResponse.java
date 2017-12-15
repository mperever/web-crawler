package com.github.mperever.common.dto;

/**
 * Represents response for {@link SaveTaskResultRequest}.
 *
 * @author mperever
 */
public class SaveTaskResultResponse implements ErrorKeeper
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

    @Override
    public boolean hasError()
    {
        return error != null;
    }

    @Override
    public Exception getError()
    {
        return error;
    }
}