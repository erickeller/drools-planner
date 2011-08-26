/*
 * Copyright 2011 JBoss Inc
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

package org.drools.planner.core.domain.variable;

import java.beans.PropertyDescriptor;
import java.util.Collection;
import java.util.Iterator;

import org.drools.planner.api.domain.variable.ValueRangeFromPlanningEntityProperty;
import org.drools.planner.api.domain.variable.ValueRangeFromSolutionProperty;
import org.drools.planner.core.domain.common.DescriptorUtils;
import org.drools.planner.core.domain.entity.PlanningEntityDescriptor;
import org.drools.planner.core.solution.director.SolutionDirector;

public class PlanningEntityPropertyPlanningValueRangeDescriptor extends AbstractPlanningValueRangeDescriptor {

    private PropertyDescriptor rangePropertyDescriptor;

    public PlanningEntityPropertyPlanningValueRangeDescriptor(PlanningVariableDescriptor variableDescriptor,
            ValueRangeFromPlanningEntityProperty valueRangeFromPlanningEntityPropertyAnnotation) {
        super(variableDescriptor);
        processValueRangeFromPlanningEntityPropertyAnnotation(valueRangeFromPlanningEntityPropertyAnnotation);
    }

    private void processValueRangeFromPlanningEntityPropertyAnnotation(
            ValueRangeFromPlanningEntityProperty valueRangeFromPlanningEntityPropertyAnnotation) {
        String planningEntityProperty = valueRangeFromPlanningEntityPropertyAnnotation.propertyName();
        PlanningEntityDescriptor planningEntityDescriptor = variableDescriptor.getPlanningEntityDescriptor();
        rangePropertyDescriptor = planningEntityDescriptor.getPropertyDescriptor(planningEntityProperty);
        if (rangePropertyDescriptor == null) {
            String exceptionMessage = "The planningEntityClass ("
                    + planningEntityDescriptor.getPlanningEntityClass()
                    + ") has a PlanningVariable annotated property (" + variableDescriptor.getVariablePropertyName()
                    + ") that refers to a planningEntityProperty (" + planningEntityProperty
                    + ") that does not exist.";
            if (planningEntityProperty.length() >= 2 && Character.isUpperCase(planningEntityProperty.charAt(1))) {
                String correctedPlanningEntityProperty = planningEntityProperty.substring(0, 1).toUpperCase()
                        + planningEntityProperty.substring(1);
                exceptionMessage += " But it probably needs to be correctedPlanningEntityProperty ("
                        + correctedPlanningEntityProperty + ") instead because the JavaBeans spec states" +
                        " the first letter should be a upper case if the second is upper case.";
            }
            throw new IllegalArgumentException(exceptionMessage);
        }
        if (!Collection.class.isAssignableFrom(rangePropertyDescriptor.getPropertyType())) {
            throw new IllegalArgumentException("The planningEntityClass ("
                    + planningEntityDescriptor.getPlanningEntityClass()
                    + ") has a PlanningVariable annotated property (" + variableDescriptor.getVariablePropertyName()
                    + ") that refers to a planningEntityProperty (" + planningEntityProperty
                    + ") that does not return a Collection.");
        }
    }

    public Collection<?> extractValues(SolutionDirector solutionDirector, Object planningEntity) {
        return (Collection<?>) DescriptorUtils.executeGetter(rangePropertyDescriptor,
                planningEntity);
    }

}