package com.github.mperever.dal.mysql.internal;

import com.github.mperever.common.dto.UrlTask;
import com.github.mperever.dal.mysql.TaskServiceRepositoryMySql;

/**
 * This class keeps field names for {@link UrlTask} and is used by {@link TaskServiceRepositoryMySql}.
 */
public final class UrlTask_
{
    private UrlTask_()
    {
    }

    /**
     *  Field name for {@link UrlTask#id}
     */
    public static final String id = "id";

    /**
     *  Field name for {@link UrlTask#url}
     */
    public static final String url = "url";

    /**
     *  Field name for {@link UrlTask#endProcessTime}
     */
    public static final String endProcessTime = "endProcessTime";

    /**
     *  Field name for {@link UrlTask#errorCount}
     */
    public static final String errorCount = "errorCount";

    /**
     *  Field name for {@link UrlTask#external}
     */
    public static final String external = "external";

    /**
     *  Field name for {@link UrlTask#depth}
     */
    public static final String depth = "depth";

    /**
     *  Field name for {@link UrlTask#clientId}
     */
    public static final String clientId = "clientId";

    /**
     *  Field name for {@link UrlTask#startProcessTime}
     */
    public static final String startProcessTime = "startProcessTime";
}