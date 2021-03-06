/*
 * Copyright 2016-present Open Networking Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.onosproject.yang.compiler.datamodel;

import org.onosproject.yang.compiler.datamodel.exceptions.DataModelException;
import org.onosproject.yang.compiler.datamodel.utils.Parsable;
import org.onosproject.yang.compiler.datamodel.utils.YangConstructType;

import java.io.Serializable;

import static org.onosproject.yang.compiler.datamodel.utils.YangConstructType.MUST_DATA;
import static org.onosproject.yang.compiler.datamodel.utils.YangErrMsgConstants.MUST_VIOLATION_ERROR_APP_TAG;
import static org.onosproject.yang.compiler.datamodel.utils.YangErrMsgConstants.OPERATION_FAILED_ERROR_TAG;

/*-
 * The "must" statement, which is optional, takes as an argument a string that
 * contains an XPath expression. It is used to formally declare a constraint
 * on valid data.
 *
 * When a datastore is validated, all "must" constraints are conceptually
 * evaluated once for each data node in the data tree, and for all leafs with
 * default values in use. If a data node does not exist in the data tree, and
 * it does not have a default value, its "must" statements are not evaluated.
 *
 * All such constraints MUST evaluate to true for the data to be valid.
 *
 *  The must's sub-statements
 *
 *                +---------------+---------+-------------+------------------+
 *                | substatement  | section | cardinality |data model mapping|
 *                +---------------+---------+-------------+------------------+
 *                | description   | 7.19.3  | 0..1        | -string          |
 *                | error-app-tag | 7.5.4.2 | 0..1        | -not supported   |
 *                | error-message | 7.5.4.1 | 0..1        | -not supported   |
 *                | reference     | 7.19.4  | 0..1        | -string          |
 *                +---------------+---------+-------------+------------------+
 */

/**
 * Represents information defined in YANG must.
 */
public class YangMust extends DefaultLocationInfo
        implements YangDesc, YangReference, Parsable, Serializable, YangAppErrorHolder {

    private static final long serialVersionUID = 806201646L;

    /**
     * Constraint info.
     */
    private String constraint;

    /**
     * Description string.
     */
    private String description;

    /**
     * Reference string.
     */
    private String reference;

    /**
     * YANG application error information.
     */
    private YangAppErrorInfo yangAppErrorInfo;

    /**
     * Creates a YANG must restriction.
     */
    public YangMust() {
        yangAppErrorInfo = new YangAppErrorInfo();
        yangAppErrorInfo.setErrorTag(OPERATION_FAILED_ERROR_TAG);
        yangAppErrorInfo.setErrorAppTag(MUST_VIOLATION_ERROR_APP_TAG);
    }

    /**
     * Returns the constraint.
     *
     * @return the constraint
     */
    public String getConstraint() {
        return constraint;
    }

    /**
     * Sets the constraint.
     *
     * @param constraint the constraint to set
     */
    public void setConstraint(String constraint) {
        this.constraint = constraint;
    }

    /**
     * Returns the description.
     *
     * @return the description
     */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description.
     *
     * @param description set the description
     */
    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the textual reference.
     *
     * @return the reference
     */
    @Override
    public String getReference() {
        return reference;
    }

    /**
     * Sets the textual reference.
     *
     * @param reference the reference to set
     */
    @Override
    public void setReference(String reference) {
        this.reference = reference;
    }

    /**
     * Returns the type of the parsed data.
     *
     * @return returns MUST_DATA
     */
    @Override
    public YangConstructType getYangConstructType() {
        return MUST_DATA;
    }

    /**
     * Validates the data on entering the corresponding parse tree node.
     *
     * @throws DataModelException a violation of data model rules
     */
    @Override
    public void validateDataOnEntry() throws DataModelException {
        // TODO auto-generated method stub, to be implemented by parser
    }

    /**
     * Validates the data on exiting the corresponding parse tree node.
     *
     * @throws DataModelException a violation of data model rules
     */
    @Override
    public void validateDataOnExit() throws DataModelException {
        // TODO auto-generated method stub, to be implemented by parser
    }

    @Override
    public void setAppErrorInfo(YangAppErrorInfo yangAppErrorInfo) {
        this.yangAppErrorInfo = yangAppErrorInfo;
    }

    @Override
    public YangAppErrorInfo getAppErrorInfo() {
        return yangAppErrorInfo;
    }
}
