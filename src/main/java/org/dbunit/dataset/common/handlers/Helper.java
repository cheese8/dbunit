/*
 *
 * The DbUnit Database Testing Framework
 * Copyright (C)2002-2004, DbUnit.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package org.dbunit.dataset.common.handlers;

import lombok.extern.slf4j.Slf4j;

/**
 * @author fede
 * @author Last changed by: $Author$
 * @version $Revision$ $Date$
 * @since 2.2 (Sep 12, 2004)
 */
@Slf4j
public abstract class Helper {
    private PipelineComponent handler;

    abstract void helpWith(char c) throws PipelineException;

    public boolean allowForNoMoreInput() throws IllegalStateException {
        return true;
    }

    PipelineComponent getHandler() {
        return handler;
    }

    void setHandler(PipelineComponent handler) {
        log.debug("setHandler(handler={}) - start", handler);
        this.handler = handler;
    }
}