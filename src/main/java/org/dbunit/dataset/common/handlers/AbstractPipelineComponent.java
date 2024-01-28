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
public abstract class AbstractPipelineComponent implements PipelineComponent {
    private PipelineComponent successor;
    private Pipeline pipeline;

    private Helper helper;

    protected PipelineComponent getSuccessor() {
        return successor;
    }

    public Pipeline getPipeline() {
        return pipeline;
    }

    public void setPipeline(Pipeline pipeline) {
        log.debug("setPipeline(pipeline={}) - start", pipeline);
        this.pipeline = pipeline;
    }

    protected PipelineConfig getPipelineConfig() {
        if (getPipeline() != null) {
            return this.getPipeline().getPipelineConfig();
        }
        throw new IllegalStateException("The pipeline is not set for this component. Cannot proceed");
    }

    public void setSuccessor(PipelineComponent successor) {
        log.debug("setSuccessor(successor={}) - start", successor);
        this.successor = successor;
    }

    private StringBuffer getThePiece() {
        return getPipeline().getCurrentProduct();
    }

    public void handle(char c) throws IllegalInputCharacterException, PipelineException {
        log.debug("handle(c={}) - start", c);
        if (!canHandle(c)) {
            getSuccessor().handle(c);
        } else {
            getHelper().helpWith(c);
        }
    }

    public void noMoreInput() {
        log.debug("noMoreInput() - start");
        if (allowForNoMoreInput()) {
            if (getSuccessor() != null) {
                getSuccessor().noMoreInput();
            }
        }
    }

    public boolean allowForNoMoreInput() {
        log.debug("allowForNoMoreInput() - start");
        return getHelper().allowForNoMoreInput();
    }

    protected static PipelineComponent createPipelineComponent(AbstractPipelineComponent handler, Helper helper) {
        log.debug("createPipelineComponent(handler={}, helper={}) - start", handler, helper);
        helper.setHandler(handler);
        handler.setHelper(helper);
        return handler;
    }

    /**
     * Method invoked when the character should be accepted
     */
    public void accept(char c) {
        getThePiece().append(c);
    }

    protected Helper getHelper() {
        return helper;
    }

    private void setHelper(Helper helper) {
        log.debug("setHelper(helper={}) - start", helper);
        this.helper = helper;
    }

    static protected class IGNORE extends Helper {
        public void helpWith(char c) {
            // IGNORE
        }
    }

    static protected class ACCEPT extends Helper {
        public void helpWith(char c) {
            log.debug("helpWith(c={}) - start", c);
            getHandler().accept(c);
        }
    }
}