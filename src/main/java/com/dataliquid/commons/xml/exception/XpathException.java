/*
 * Copyright © 2019 dataliquid GmbH | www.dataliquid.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dataliquid.commons.xml.exception;

/**
 * The XpathException class represents an exception that is thrown for errors encountered during XPath processing.
 */
public class XpathException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new XpathException with a specific detail message.
     *
     * @param message the detail message
     */
    public XpathException(String message)
    {
        super(message);
    }

    /**
     * Constructs a new XpathException with a specific detail message and a cause.
     *
     * @param message the detail message
     * @param cause   the cause (which is saved for later retrieval by the {@link #getCause()} method)
     */
    public XpathException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
