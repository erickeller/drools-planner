/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.planner.core.heuristic.selector.entity.decorator;

import java.util.ArrayList;
import java.util.List;

import org.drools.planner.core.heuristic.selector.common.SelectionCacheType;
import org.drools.planner.core.heuristic.selector.common.decorator.SelectionFilter;
import org.drools.planner.core.heuristic.selector.entity.EntitySelector;
import org.drools.planner.core.score.director.ScoreDirector;
import org.drools.planner.core.solver.DefaultSolverScope;

public class CachingFilteringEntitySelector extends CachingEntitySelector {

    protected final List<SelectionFilter> entityFilterList;

    public CachingFilteringEntitySelector(EntitySelector childEntitySelector, SelectionCacheType cacheType,
            List<SelectionFilter> entityFilterList) {
        super(childEntitySelector, cacheType);
        this.entityFilterList = entityFilterList;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public void constructCache(DefaultSolverScope solverScope) {
        ScoreDirector scoreDirector = solverScope.getScoreDirector();
        long childSize = childEntitySelector.getSize();
        if (childSize > (long) Integer.MAX_VALUE) {
            throw new IllegalStateException("The moveSelector (" + this + ") has a childEntitySelector ("
                    + childEntitySelector + ") with childSize (" + childSize
                    + ") which is higher than Integer.MAX_VALUE.");
        }
        cachedEntityList = new ArrayList<Object>((int) childSize);
        for (Object entity : childEntitySelector) {
            if (accept(scoreDirector, entity)) {
                cachedEntityList.add(entity);
            }
        }
    }

    private boolean accept(ScoreDirector scoreDirector, Object entity) {
        for (SelectionFilter entityFilter : entityFilterList) {
            if (!entityFilter.accept(scoreDirector, entity)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "Filtering(" + childEntitySelector + ")";
    }

}
