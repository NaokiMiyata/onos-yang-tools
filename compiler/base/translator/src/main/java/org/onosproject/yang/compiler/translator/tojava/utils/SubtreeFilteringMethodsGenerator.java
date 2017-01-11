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

package org.onosproject.yang.compiler.translator.tojava.utils;

import org.onosproject.yang.compiler.datamodel.RpcNotificationContainer;
import org.onosproject.yang.compiler.datamodel.YangAugment;
import org.onosproject.yang.compiler.datamodel.YangCase;
import org.onosproject.yang.compiler.datamodel.YangChoice;
import org.onosproject.yang.compiler.datamodel.YangDataStructure;
import org.onosproject.yang.compiler.datamodel.YangLeafRef;
import org.onosproject.yang.compiler.datamodel.YangLeavesHolder;
import org.onosproject.yang.compiler.datamodel.YangNode;
import org.onosproject.yang.compiler.datamodel.YangType;
import org.onosproject.yang.compiler.translator.exception.TranslatorException;
import org.onosproject.yang.compiler.translator.tojava.JavaAttributeInfo;
import org.onosproject.yang.compiler.translator.tojava.JavaCodeGeneratorInfo;
import org.onosproject.yang.compiler.translator.tojava.JavaFileInfoContainer;
import org.onosproject.yang.compiler.translator.tojava.JavaFileInfoTranslator;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes.IDENTITYREF;
import static org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes.LEAFREF;
import static org.onosproject.yang.compiler.translator.tojava.GeneratedTempFileType.FILTER_CONTENT_MATCH_FOR_LEAF_LIST_MASK;
import static org.onosproject.yang.compiler.translator.tojava.GeneratedTempFileType.FILTER_CONTENT_MATCH_FOR_LEAF_MASK;
import static org.onosproject.yang.compiler.translator.tojava.GeneratedTempFileType.FILTER_CONTENT_MATCH_FOR_NODES_MASK;
import static org.onosproject.yang.compiler.translator.tojava.utils.IndentationType.EIGHT_SPACE;
import static org.onosproject.yang.compiler.translator.tojava.utils.IndentationType.FOUR_SPACE;
import static org.onosproject.yang.compiler.translator.tojava.utils.IndentationType.SIXTEEN_SPACE;
import static org.onosproject.yang.compiler.translator.tojava.utils.IndentationType.TWELVE_SPACE;
import static org.onosproject.yang.compiler.translator.tojava.utils.IndentationType.TWENTY_EIGHT_SPACE;
import static org.onosproject.yang.compiler.translator.tojava.utils.IndentationType.TWENTY_FOUR_SPACE;
import static org.onosproject.yang.compiler.translator.tojava.utils.IndentationType.TWENTY_SPACE;
import static org.onosproject.yang.compiler.translator.tojava.utils.JavaFileGeneratorUtils.getDataFromTempFileHandle;
import static org.onosproject.yang.compiler.translator.tojava.utils.MethodClassTypes.CLASS_TYPE;
import static org.onosproject.yang.compiler.translator.tojava.utils.MethodsGenerator.getYangDataStructure;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.getAppInstanceAttrString;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.getElseIfConditionBegin;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.getEqualEqualString;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.getForLoopString;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.getIfConditionBegin;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.getLeafFlagSetString;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.getNewInstance;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.getOpenCloseParaWithValue;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.getReturnString;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.getTwoParaEqualsString;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.methodClose;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.multiAttrMethodSignature;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.signatureClose;
import static org.onosproject.yang.compiler.translator.tojava.utils.TranslatorUtils.getBeanFiles;
import static org.onosproject.yang.compiler.utils.UtilConstants.ADD_STRING;
import static org.onosproject.yang.compiler.utils.UtilConstants.AND_OPERATION;
import static org.onosproject.yang.compiler.utils.UtilConstants.APP_INSTANCE;
import static org.onosproject.yang.compiler.utils.UtilConstants.BIT_SET;
import static org.onosproject.yang.compiler.utils.UtilConstants.BOOLEAN_DATA_TYPE;
import static org.onosproject.yang.compiler.utils.UtilConstants.BREAK;
import static org.onosproject.yang.compiler.utils.UtilConstants.BUILDER;
import static org.onosproject.yang.compiler.utils.UtilConstants.BUILDER_LOWER_CASE;
import static org.onosproject.yang.compiler.utils.UtilConstants.BUILD_FOR_FILTER;
import static org.onosproject.yang.compiler.utils.UtilConstants.CLASS_STRING;
import static org.onosproject.yang.compiler.utils.UtilConstants.CLOSE_CURLY_BRACKET;
import static org.onosproject.yang.compiler.utils.UtilConstants.CLOSE_PARENTHESIS;
import static org.onosproject.yang.compiler.utils.UtilConstants.COMMA;
import static org.onosproject.yang.compiler.utils.UtilConstants.DEFAULT;
import static org.onosproject.yang.compiler.utils.UtilConstants.DEFAULT_CAPS;
import static org.onosproject.yang.compiler.utils.UtilConstants.DIAMOND_CLOSE_BRACKET;
import static org.onosproject.yang.compiler.utils.UtilConstants.DIAMOND_OPEN_BRACKET;
import static org.onosproject.yang.compiler.utils.UtilConstants.EIGHT_SPACE_INDENTATION;
import static org.onosproject.yang.compiler.utils.UtilConstants.ELSE;
import static org.onosproject.yang.compiler.utils.UtilConstants.EMPTY_STRING;
import static org.onosproject.yang.compiler.utils.UtilConstants.ENTRY;
import static org.onosproject.yang.compiler.utils.UtilConstants.ENTRY_SET;
import static org.onosproject.yang.compiler.utils.UtilConstants.EQUAL;
import static org.onosproject.yang.compiler.utils.UtilConstants.EQUALS_STRING;
import static org.onosproject.yang.compiler.utils.UtilConstants.EXTEND;
import static org.onosproject.yang.compiler.utils.UtilConstants.FALSE;
import static org.onosproject.yang.compiler.utils.UtilConstants.FLAG;
import static org.onosproject.yang.compiler.utils.UtilConstants.GET;
import static org.onosproject.yang.compiler.utils.UtilConstants.GET_KEY;
import static org.onosproject.yang.compiler.utils.UtilConstants.GET_VALUE;
import static org.onosproject.yang.compiler.utils.UtilConstants.INSTANCE;
import static org.onosproject.yang.compiler.utils.UtilConstants.IS_ANY_SELECT_OR_CONTAINMENT_NODE_FLAG;
import static org.onosproject.yang.compiler.utils.UtilConstants.IS_EMPTY;
import static org.onosproject.yang.compiler.utils.UtilConstants.IS_SELECT_ALL_SCHEMA_CHILD_FLAG;
import static org.onosproject.yang.compiler.utils.UtilConstants.KEYS;
import static org.onosproject.yang.compiler.utils.UtilConstants.LEAF_IDENTIFIER;
import static org.onosproject.yang.compiler.utils.UtilConstants.MAP;
import static org.onosproject.yang.compiler.utils.UtilConstants.NEW_LINE;
import static org.onosproject.yang.compiler.utils.UtilConstants.NOT;
import static org.onosproject.yang.compiler.utils.UtilConstants.NULL;
import static org.onosproject.yang.compiler.utils.UtilConstants.OPEN_CLOSE_BRACKET_STRING;
import static org.onosproject.yang.compiler.utils.UtilConstants.OPEN_CURLY_BRACKET;
import static org.onosproject.yang.compiler.utils.UtilConstants.OPEN_PARENTHESIS;
import static org.onosproject.yang.compiler.utils.UtilConstants.OP_PARAM;
import static org.onosproject.yang.compiler.utils.UtilConstants.OR_OPERATION;
import static org.onosproject.yang.compiler.utils.UtilConstants.PERIOD;
import static org.onosproject.yang.compiler.utils.UtilConstants.PRIVATE;
import static org.onosproject.yang.compiler.utils.UtilConstants.PROCESS_CHILD_NODE_STF_PARAM;
import static org.onosproject.yang.compiler.utils.UtilConstants.PROCESS_LEAF_LIST_STF_PARAM;
import static org.onosproject.yang.compiler.utils.UtilConstants.PROCESS_LEAF_STF_PARAM;
import static org.onosproject.yang.compiler.utils.UtilConstants.PROCESS_SUBTREE_FILTERING;
import static org.onosproject.yang.compiler.utils.UtilConstants.PUBLIC;
import static org.onosproject.yang.compiler.utils.UtilConstants.QUESTION_MARK;
import static org.onosproject.yang.compiler.utils.UtilConstants.RESULT;
import static org.onosproject.yang.compiler.utils.UtilConstants.SELECT_ALL_CHILD;
import static org.onosproject.yang.compiler.utils.UtilConstants.SELECT_ALL_CHILD_SCHEMA_PARAM;
import static org.onosproject.yang.compiler.utils.UtilConstants.SELECT_LEAF;
import static org.onosproject.yang.compiler.utils.UtilConstants.SELECT_OR_CONTAINMENT_NODE_PARAM;
import static org.onosproject.yang.compiler.utils.UtilConstants.SEMI_COLON;
import static org.onosproject.yang.compiler.utils.UtilConstants.SET_METHOD_PREFIX;
import static org.onosproject.yang.compiler.utils.UtilConstants.SIXTEEN_SPACE_INDENTATION;
import static org.onosproject.yang.compiler.utils.UtilConstants.SPACE;
import static org.onosproject.yang.compiler.utils.UtilConstants.STF_BUILDER_PARAM;
import static org.onosproject.yang.compiler.utils.UtilConstants.SUBTREE_FILTERED;
import static org.onosproject.yang.compiler.utils.UtilConstants.SUBTREE_FILTERING_RESULT_BUILDER;
import static org.onosproject.yang.compiler.utils.UtilConstants.THIRTY_TWO_SPACE_INDENTATION;
import static org.onosproject.yang.compiler.utils.UtilConstants.TO;
import static org.onosproject.yang.compiler.utils.UtilConstants.TRUE;
import static org.onosproject.yang.compiler.utils.UtilConstants.TWELVE_SPACE_INDENTATION;
import static org.onosproject.yang.compiler.utils.UtilConstants.TWENTY_EIGHT_SPACE_INDENTATION;
import static org.onosproject.yang.compiler.utils.UtilConstants.TWENTY_FOUR_SPACE_INDENTATION;
import static org.onosproject.yang.compiler.utils.UtilConstants.TWENTY_SPACE_INDENTATION;
import static org.onosproject.yang.compiler.utils.UtilConstants.TWO;
import static org.onosproject.yang.compiler.utils.UtilConstants.VALUE_LEAF;
import static org.onosproject.yang.compiler.utils.UtilConstants.VALUE_LEAF_SET;
import static org.onosproject.yang.compiler.utils.UtilConstants.ZERO;
import static org.onosproject.yang.compiler.utils.io.impl.YangIoUtils.getCamelCase;
import static org.onosproject.yang.compiler.utils.io.impl.YangIoUtils.getCapitalCase;

/**
 * Represents generator for subtree filtering methods of generated files
 * based on the file type.
 */
public final class SubtreeFilteringMethodsGenerator {

    /**
     * private to make it a util.
     */
    private SubtreeFilteringMethodsGenerator() {
    }

    /**
     * Returns process sub tree for choice interface class.
     *
     * @param node choice node
     * @return process sub tree for choice interface class
     */
    static String getProcessSubTreeForChoiceInterface(YangNode node) {
        if (!(node instanceof YangChoice)) {
            throw new TranslatorException("process sub tree for interface is " +
                                                  "only allowed in choice " +
                                                  "node.");
        }
        JavaFileInfoTranslator info = ((JavaCodeGeneratorInfo) node)
                .getJavaFileInfo();

        String name = info.getJavaName();
        String returnType = getCapitalCase(name);

        String javadoc = "\n   /**\n" +
                "     * Applications need not to implement process subtree " +
                "of " + name + "\n     * it will be overridden by " +
                "corresponding case class.\n" +
                "     *\n" +
                "     * @param " + APP_INSTANCE + SPACE +
                APP_INSTANCE + SPACE + "being passed to check" +
                " for" +
                " content match\n" +
                "     * @param isSelectAllSchemaChild is select all schema child\n" +
                "     * @return match result\n" +
                "     */\n";
        StringBuilder builder = new StringBuilder(javadoc);
        Map<String, String> param = new LinkedHashMap<>();
        param.put(APP_INSTANCE, returnType);
        param.put(SELECT_ALL_CHILD_SCHEMA_PARAM, BOOLEAN_DATA_TYPE);
        builder.append(multiAttrMethodSignature(PROCESS_SUBTREE_FILTERING, null,
                                                DEFAULT, returnType, param,
                                                CLASS_TYPE))
                .append(getReturnString(NULL, EIGHT_SPACE_INDENTATION))
                .append(signatureClose()).append(methodClose(FOUR_SPACE));
        return builder.toString();
    }

    /**
     * Returns is filter content match for leaf.
     *
     * @param javaAttributeInfo java attribute
     * @param type              data type
     * @return is filter content match for leaf
     */
    public static String getSubtreeFilteringForLeaf(
            JavaAttributeInfo javaAttributeInfo, YangType<?> type) {
        String attrQualifiedType;
        String attributeName = javaAttributeInfo.getAttributeName();
        attrQualifiedType = getIfFilterContentMatchMethodImpl(attributeName,
                                                              type);
        /* if (valueLeafFlags.get(LeafIdentifier.LEAF.getLeafIndex())) {
         * if (appInstance.leaf() != leaf()) {
         * return false;
         * } else {
         * subTreeFilteringResultBuilder.leaf(appInstance.leaf());
         * }
         * } else if (selectLeafFlags.get(LeafIdentifier.LEAF.getLeafIndex()) ||
         * isSelectAllSchemaChild) {
         * isAnySelectOrContainmentNode = true;
         * subTreeFilteringResultBuilder.leaf(appInstance.leaf());
         * }*/

/*        if (isSubTreeFiltered && !appInstance.isLeafValueSet(
                LeafIdentifier.LEAF2)) {
            subTreeFilteringResultBuilder.leaf2(leaf2());
        } else {
            return false;
        }*/
        String condition = SUBTREE_FILTERED + SPACE + AND_OPERATION + SPACE + NOT +
                APP_INSTANCE + PERIOD + VALUE_LEAF_SET +
                getOpenCloseParaWithValue(
                        LEAF_IDENTIFIER + PERIOD + attributeName.toUpperCase());
        return getIfConditionBegin(EIGHT_SPACE_INDENTATION, getLeafFlagSetString(
                attributeName, VALUE_LEAF, EMPTY_STRING, GET)) +
                getIfConditionBegin(TWELVE_SPACE_INDENTATION, attrQualifiedType) +
                getIfConditionBegin(SIXTEEN_SPACE_INDENTATION, condition) +
                TWENTY_SPACE_INDENTATION + SUBTREE_FILTERING_RESULT_BUILDER +
                PERIOD + attributeName + getOpenCloseParaWithValue(
                attributeName + OPEN_CLOSE_BRACKET_STRING) + signatureClose() +
                SIXTEEN_SPACE_INDENTATION + CLOSE_CURLY_BRACKET + ELSE +
                OPEN_CURLY_BRACKET + NEW_LINE + getReturnString(
                FALSE, TWENTY_SPACE_INDENTATION) + signatureClose() +
                SIXTEEN_SPACE_INDENTATION + CLOSE_CURLY_BRACKET + NEW_LINE +
                TWELVE_SPACE_INDENTATION + CLOSE_CURLY_BRACKET + ELSE +
                OPEN_CURLY_BRACKET + NEW_LINE +
                getSubTreeBuilderCallString(SIXTEEN_SPACE_INDENTATION, attributeName,
                                            TWELVE_SPACE) +
                getElseIfConditionBegin(EIGHT_SPACE_INDENTATION, getLeafFlagSetString(
                        attributeName, SELECT_LEAF, EMPTY_STRING, GET) + SPACE +
                        OR_OPERATION + SPACE + IS_SELECT_ALL_SCHEMA_CHILD_FLAG) +
                getSelectOrContainmentAssignString() +
                getSubTreeBuilderCallString(TWELVE_SPACE_INDENTATION, attributeName,
                                            EIGHT_SPACE);
    }

    private static String getSubTreeBuilderCallString(String indent, String
            name, IndentationType type) {
        return indent + SUBTREE_FILTERING_RESULT_BUILDER + PERIOD + name +
                getOpenCloseParaWithValue(APP_INSTANCE + PERIOD + name +
                                                  OPEN_CLOSE_BRACKET_STRING) +
                signatureClose() + methodClose(type);
    }

    private static String getAttrTypeForFcmWhenPrimitiveDataType(
            String attributeName) {
        return/* TODO: Need to check if we can expose the value leaf flag in
         interface.
                NOT + APP_INSTANCE + PERIOD + GET_VALUE_LEAF_FLAGS +
                OPEN_PARENTHESIS + CLOSE_PARENTHESIS +
                GET_METHOD_PREFIX + OPEN_PARENTHESIS + LEAF_IDENTIFIER +
                PERIOD + attributeName.toUpperCase() + PERIOD
                + GET_LEAF_INDEX + OR_OPERATION +*/
                APP_INSTANCE + PERIOD + attributeName + OPEN_PARENTHESIS +
                        CLOSE_PARENTHESIS + SPACE + NOT + EQUAL + SPACE +
                        attributeName + OPEN_PARENTHESIS +
                        CLOSE_PARENTHESIS;
    }

    private static String attrTypeForFcmWhenNonPrimitiveDataTypes(String name) {
        /*
         * appInstance.name() == null || name().equals(appInstance.name())
         */
        return getEqualEqualString(getAppInstanceAttrString(name), NULL) +
                SPACE + OR_OPERATION + SPACE + NOT + OPEN_PARENTHESIS +
                getTwoParaEqualsString(name + OPEN_CLOSE_BRACKET_STRING,
                                       getAppInstanceAttrString(name))
                + CLOSE_PARENTHESIS;
    }

    private static String getIfFilterContentMatchMethodImpl(
            String name, YangType dataType) {
        String attrQualifiedType;
        if (dataType.getDataType().isPrimitiveDataType()) {
            attrQualifiedType = getAttrTypeForFcmWhenPrimitiveDataType(name);
        } else if (dataType.getDataType() == LEAFREF) {

            // When leafref in grouping.
            if (((YangLeafRef) dataType.getDataTypeExtendedInfo())
                    .isInGrouping()) {
                attrQualifiedType = attrTypeForFcmWhenNonPrimitiveDataTypes(name);
            } else {
                YangType type = ((YangLeafRef) dataType.getDataTypeExtendedInfo())
                        .getEffectiveDataType();
                if (type.getDataType().isPrimitiveDataType()) {
                    attrQualifiedType = getAttrTypeForFcmWhenPrimitiveDataType(name);
                } else {
                    attrQualifiedType = attrTypeForFcmWhenNonPrimitiveDataTypes(
                            name);
                }
            }
        } else {
            attrQualifiedType = attrTypeForFcmWhenNonPrimitiveDataTypes(name);
        }
        return attrQualifiedType;
    }

    /**
     * Returns is filter content match for leaf.
     *
     * @param javaAttributeInfo java attribute
     * @return is filter content match for leaf
     */
    public static String getSubtreeFilteringForLeafList(
            JavaAttributeInfo javaAttributeInfo) {
        return getSubtreeFilteringForList(javaAttributeInfo, true, null);
    }

    /**
     * Returns is filter content match for child node.
     *
     * @param curNode current node
     * @param path    path of temp file
     * @return is filter content match for child node
     */
    static String getProcessChildNodeSubtreeFiltering(YangNode curNode, String path)
            throws IOException {
       /* Generate code will look like this.
        private boolean processChildNodesSubTreeFiltering(Link
 appInstance, LinkBuilder subTreeFilteringResultBuilder,
                Boolean isAnySelectOrContainmentNode, boolean isSelectAllSchemaChild) {
            if (isSelectAllSchemaChild) {
                for (Areas areas : appInstance.areas()) {
                    subTreeFilteringResultBuilder.addToAreas(areas);
                }
            } else if (areas() != null) {
                isAnySelectOrContainmentNode = true;
                if (!areas().isEmpty()) {
                    if (appInstance.areas() != null && !appInstance.areas().isEmpty()) {
                        for (Areas areas : areas()) {
                            for (Areas areas2 : appInstance.areas()) {
                                Areas result = areas.processSubtreeFiltering(areas2, false);
                                if (result != null) {
                                    subTreeFilteringResultBuilder.addToAreas(result);
                                }
                            }
                        }
                    }
                } else {
                    if (appInstance.areas() != null && !appInstance.areas().isEmpty()) {
                        for (Areas areas : appInstance.areas()) {
                            subTreeFilteringResultBuilder.addToAreas(areas);
                        }
                    }
                }
            }

            return true;
        }*/
        return getProcessStfMethods(PROCESS_CHILD_NODE_STF_PARAM, curNode,
                                    path, FILTER_CONTENT_MATCH_FOR_NODES_MASK);
    }

    /**
     * Returns is filter content match for leaf list.
     *
     * @param curNode current node
     * @param path    path of temp file
     * @return is filter content match for leaf list
     */
    static String getProcessLeafListSubtreeFiltering(YangNode curNode, String path)
            throws IOException {
       /* Generate code will look like this.
        private boolean processLeafListSubTreeFiltering(Link appInstance, LinkBuilder subTreeFilteringResultBuilder,
                Boolean isAnySelectOrContainmentNode, boolean isSelectAllSchemaChild) {
            if (isSelectAllSchemaChild) {
                for (String portId : appInstance.portId()) {
                    subTreeFilteringResultBuilder.addToPortId(portId);
                }
            } else if (portId() != null) {
                if (!portId().isEmpty()) {
                    if (appInstance.portId() == null || appInstance.portId().isEmpty()) {
                        return false;
                    }
                    for (String portId : portId()) {
                        boolean flag = false;
                        for (String portId2 : appInstance.portId()) {
                            if (portId.equals(portId2)) {
                                flag = true;
                                subTreeFilteringResultBuilder.addToPortId(portId2);
                                break;
                            }
                        }
                        if (!flag) {
                            return false;
                        }
                    }
                } else {
                    isAnySelectOrContainmentNode = true;
                    if (appInstance.portId() != null && !appInstance.portId().isEmpty()) {
                        for (String portId : appInstance.portId()) {
                            subTreeFilteringResultBuilder.addToPortId(portId);
                        }
                    }
                }
            }

            return true;
        }*/
        return getProcessStfMethods(PROCESS_LEAF_LIST_STF_PARAM, curNode, path,
                                    FILTER_CONTENT_MATCH_FOR_LEAF_LIST_MASK);
    }

    /**
     * Returns is filter content match for leaf.
     *
     * @param curNode current node
     * @param path    path of temp file
     * @return is filter content match for leaf
     */
    static String getProcessLeafSubtreeFiltering(YangNode curNode, String path)
            throws IOException {
     /* Generate code will look like this.
      private boolean processLeafSubtreeFiltering(Link appInstance, LinkBuilder subTreeFilteringResultBuilder,
                Boolean isAnySelectOrContainmentNode, boolean isSelectAllSchemaChild) {
            if (valueLeafFlags.get(LeafIdentifier.PORT.getLeafIndex())) {
                if (appInstance.port() != port()) {
                    return false;
                } else {
                    subTreeFilteringResultBuilder.port(appInstance.port());
                }
            } else if (selectLeafFlags.get(LeafIdentifier.PORT.getLeafIndex()) || isSelectAllSchemaChild) {
                isAnySelectOrContainmentNode = true;
                subTreeFilteringResultBuilder.port(appInstance.port());
            }

            return true;
        }*/
        return getProcessStfMethods(PROCESS_LEAF_STF_PARAM, curNode, path,
                                    FILTER_CONTENT_MATCH_FOR_LEAF_MASK);
    }

    /**
     * Returns is filter content match for leaf.
     *
     * @param curNode current node
     * @return is filter content match for leaf
     */
    static String getProcessSubtreeFilteringStart(YangNode curNode) {

       /* Generate code will look like this.
       public Link processSubtreeFiltering(Link appInstance, boolean isSelectAllSchemaChild) {
            LinkBuilder subTreeFilteringResultBuilder = new LinkBuilder();
            Boolean isAnySelectOrContainmentNode = false;
        */
        StringBuilder builder = new StringBuilder();
        JavaFileInfoTranslator javaFileInfo =
                ((JavaFileInfoContainer) curNode).getJavaFileInfo();
        String instance = APP_INSTANCE;
        String name = getCapitalCase(javaFileInfo.getJavaName());
        String builderNamePrefix = getCapitalCase(javaFileInfo.getJavaName());
        String caseName = getNameOfClassForIfCase(curNode);
        String returnType;
        if (curNode instanceof RpcNotificationContainer) {
            returnType = name + OP_PARAM;
        } else {
            returnType = DEFAULT_CAPS + name;
        }
        if (caseName != null) {
            instance = INSTANCE;
            name = caseName;
        }

        String javadoc = "\n    /**\n" +
                "     * Checks if the passed " + name + " maps the content " +
                "match query condition.\n" +
                "     *\n" +
                "     * @param " + instance + SPACE + instance + SPACE +
                "being passed to check for content match\n" +
                "     * @param isSelectAllSchemaChild is select all schema " +
                "child\n" +
                "     * @return match result\n" +
                "     */\n";
        Map<String, String> param = new LinkedHashMap<>();
        param.put(instance, name);
        param.put(SELECT_ALL_CHILD_SCHEMA_PARAM, BOOLEAN_DATA_TYPE);
        builder.append(javadoc)
                .append(multiAttrMethodSignature(PROCESS_SUBTREE_FILTERING, null,
                                                 PUBLIC, returnType, param,
                                                 CLASS_TYPE));

        builder.append(getNewInstance(builderNamePrefix + BUILDER,
                                      SUBTREE_FILTERING_RESULT_BUILDER,
                                      EIGHT_SPACE_INDENTATION, EMPTY_STRING));
        builder.append(getNewInstance(BIT_SET,
                                      IS_ANY_SELECT_OR_CONTAINMENT_NODE_FLAG,
                                      EIGHT_SPACE_INDENTATION, EMPTY_STRING));
        if (caseName != null) {
            builder.append(getCaseCastString(javaFileInfo, instance, curNode));
        }

        return builder.toString();
    }

    /**
     * Returns all process sub tree method implementations.
     *
     * @param methodName method name
     * @param curNode    current node
     * @param path       path for temporary file
     * @param file       temp file
     * @return method implementations
     * @throws IOException when fails to fetch data from temp files
     */
    private static String getProcessStfMethods(
            String methodName, YangNode curNode, String path, int file)
            throws IOException {
        StringBuilder builder = new StringBuilder(EMPTY_STRING);
        JavaFileInfoTranslator javaFileInfo =
                ((JavaFileInfoContainer) curNode).getJavaFileInfo();
        String instance = APP_INSTANCE;
        String name = getCapitalCase(javaFileInfo.getJavaName());
        String builderNamePrefix = getCapitalCase(javaFileInfo.getJavaName());
        String caseName = getNameOfClassForIfCase(curNode);
        if (caseName != null) {
            instance = INSTANCE;
            name = caseName;
        }

        Map<String, String> param = new LinkedHashMap<>();
        param.put(instance, name);
        param.put(STF_BUILDER_PARAM, builderNamePrefix + BUILDER);
        param.put(SELECT_OR_CONTAINMENT_NODE_PARAM, BIT_SET);
        param.put(SELECT_ALL_CHILD_SCHEMA_PARAM, BOOLEAN_DATA_TYPE);

        builder.append(multiAttrMethodSignature(methodName, null,
                                                PRIVATE, BOOLEAN_DATA_TYPE, param, CLASS_TYPE));

        if (caseName != null) {
            builder.append(getCaseCastString(javaFileInfo, instance, curNode));
        }
        builder.append(getDataFromTempFileHandle(file,
                                                 getBeanFiles(curNode), path))
                .append(getReturnString(TRUE, EIGHT_SPACE_INDENTATION)).append(
                signatureClose()).append(methodClose(FOUR_SPACE))
                .append(NEW_LINE);
        return builder.toString();
    }

    /**
     * Returns the body for process subtree filtering.
     *
     * @param curNode node for which the code is being generated
     * @return body of subtree filtering
     */
    static String getProcessSubtreeFunctionBody(YangNode curNode) {
        StringBuilder builder = new StringBuilder();

       /* if (!processLeafSubtreeFiltering(appInstance, subTreeFilteringResultBuilder,
                                         isAnySelectOrContainmentNode, isSelectAllSchemaChild)) {
            return null;
        }
        if (!processLeafListSubTreeFiltering(appInstance, subTreeFilteringResultBuilder,
                                             isAnySelectOrContainmentNode, isSelectAllSchemaChild)) {
            return null;
        }
        if (!processChildNodesSubTreeFiltering(appInstance, subTreeFilteringResultBuilder,
                                               isAnySelectOrContainmentNode, isSelectAllSchemaChild)) {
            return null;
        }
      */
        if (curNode instanceof YangLeavesHolder) {
            YangLeavesHolder holder = (YangLeavesHolder) curNode;
            if (!holder.getListOfLeaf().isEmpty()) {
                builder.append(getInnerStfMethodClass(PROCESS_LEAF_STF_PARAM));
            }
            if (!holder.getListOfLeafList().isEmpty()) {
                builder.append(getInnerStfMethodClass(PROCESS_LEAF_LIST_STF_PARAM));
            }
        }
        if (curNode.getChild() != null) {
            builder.append(getInnerStfMethodClass(PROCESS_CHILD_NODE_STF_PARAM));
        }

        return builder.toString();
    }

    //Method calls for process subtree filtering method.
    private static String getInnerStfMethodClass(String name) {
        StringBuilder builder = new StringBuilder()
                .append(getIfConditionBegin(EIGHT_SPACE_INDENTATION,
                                            getMethodCallsConditionsForStfMethods(name)))
                .append(getReturnString(NULL, TWELVE_SPACE_INDENTATION)).append(
                        signatureClose()).append(methodClose(EIGHT_SPACE));
        return builder.toString();
    }

    // Condition for if check in process sub tree method.
    private static String getMethodCallsConditionsForStfMethods(String name) {
        return NOT + name + getOpenCloseParaWithValue(getConditionString());
    }

    //variable call for conditional method call
    private static String getConditionString() {
        return APP_INSTANCE + COMMA + SPACE + SUBTREE_FILTERING_RESULT_BUILDER +
                COMMA + SPACE + SELECT_OR_CONTAINMENT_NODE_PARAM + COMMA + SPACE +
                SELECT_ALL_CHILD;
    }

    /**
     * Returns is filter content match for node.
     *
     * @param attr attribute info
     * @param node YANG node
     * @return is filter content match for node
     */
    public static String getSubtreeFilteringForNode(JavaAttributeInfo attr, YangNode node) {
        boolean isList = attr.isListAttr();
        if (isList) {
            return getSubtreeFilteringForList(attr, false, node);
        } else {
            return getSubtreeFilteringForChildNode(attr, node);
        }
    }

    /**
     * Returns is filter content match close.
     *
     * @param name name of class
     * @return is filter content match close
     */
    static String getProcessSubTreeFilteringEnd(String name) {
        /* generate code will look like this.
        if (!isSelectAllSchemaChild && !isAnySelectOrContainmentNode) {
            return processSubtreeFiltering(appInstance, true);
        }
        return subTreeFilteringResultBuilder.build();
        */

        StringBuilder builder = new StringBuilder();
        String cond1 = NOT + IS_SELECT_ALL_SCHEMA_CHILD_FLAG + SPACE + AND_OPERATION +
                SPACE + NOT + IS_ANY_SELECT_OR_CONTAINMENT_NODE_FLAG +
                PERIOD + GET + getOpenCloseParaWithValue(ZERO);
        String call = PROCESS_SUBTREE_FILTERING + getOpenCloseParaWithValue(
                APP_INSTANCE + COMMA + SPACE + TRUE);
        builder.append(getIfConditionBegin(EIGHT_SPACE_INDENTATION, cond1))
                .append(getReturnString(call, TWELVE_SPACE_INDENTATION))
                .append(signatureClose()).append(methodClose(EIGHT_SPACE));

        call = getOpenCloseParaWithValue(name) + SPACE +
                SUBTREE_FILTERING_RESULT_BUILDER + PERIOD + BUILD_FOR_FILTER +
                OPEN_CLOSE_BRACKET_STRING;
        builder.append(getReturnString(call, EIGHT_SPACE_INDENTATION))
                .append(signatureClose()).append(methodClose(FOUR_SPACE))
                .append(NEW_LINE);
        return builder.toString();
    }

    /**
     * Returns filter content match for child nodes.
     *
     * @param attr attribute to be added
     * @param node YANG node
     * @return filter content match for child nodes
     */
    private static String getSubtreeFilteringForChildNode(JavaAttributeInfo attr,
                                                          YangNode node) {
        StringBuilder builder = new StringBuilder();
        String name = attr.getAttributeName();
        String clsInfo = attr.getImportInfo()
                .getClassInfo();
        String type = DEFAULT_CAPS + attr.getImportInfo()
                .getClassInfo();
        if (attr.isQualifiedName()) {
            type = attr.getImportInfo().getPkgInfo() + PERIOD +
                    type;
            clsInfo = attr.getImportInfo().getPkgInfo() + PERIOD +
                    clsInfo;
        }
        String classCast = getOpenCloseParaWithValue(type) + SPACE;
        String cast = getOpenCloseParaWithValue(classCast + name);
        if (node != null && node instanceof YangChoice) {
            cast = name;
        }

        String resultString = cast + NEW_LINE + TWENTY_EIGHT_SPACE_INDENTATION +
                PERIOD + PROCESS_SUBTREE_FILTERING + OPEN_PARENTHESIS
                + APP_INSTANCE + PERIOD + name + OPEN_CLOSE_BRACKET_STRING
                + COMMA + SPACE + FALSE + CLOSE_PARENTHESIS + SEMI_COLON +
                NEW_LINE;

        String cond1 = name + OPEN_CLOSE_BRACKET_STRING + SPACE + NOT + EQUAL +
                SPACE + NULL + SPACE + OR_OPERATION + SPACE + SELECT_ALL_CHILD;
        builder.append(getIfConditionBegin(EIGHT_SPACE_INDENTATION, cond1))
                .append(getSelectOrContainmentAssignString());

        builder.append(getIfConditionBegin(TWELVE_SPACE_INDENTATION,
                                           getAppInstanceCondition(name, NOT)));

        String assignment = SIXTEEN_SPACE_INDENTATION + clsInfo + SPACE + RESULT +
                SPACE + EQUAL + SPACE + NULL + signatureClose();

        builder.append(assignment)
                .append(getIfConditionBegin(SIXTEEN_SPACE_INDENTATION,
                                            SELECT_ALL_CHILD));
/*
        result = ((DefaultInterfaces)(DefaultInterfaces.builder()
                .build())).processSubtreeFiltering(appInstance.interfaces(),
                                                   true);*/

        assignment = getDummyObjectCreation(node, name, clsInfo, type,
                                            classCast, false, false);
        builder.append(assignment).append(SIXTEEN_SPACE_INDENTATION).append(
                CLOSE_CURLY_BRACKET).append(ELSE).append(OPEN_CURLY_BRACKET)
                .append(NEW_LINE);

        assignment = TWENTY_SPACE_INDENTATION + RESULT + SPACE + EQUAL + SPACE
                + resultString;
        cond1 = RESULT + SPACE + NOT + EQUAL + SPACE + NULL;

        builder.append(assignment).append(methodClose(SIXTEEN_SPACE))
                .append(getIfConditionBegin(SIXTEEN_SPACE_INDENTATION, cond1));

        assignment = TWENTY_SPACE_INDENTATION + SUBTREE_FILTERING_RESULT_BUILDER +
                PERIOD + name + getOpenCloseParaWithValue(RESULT) +
                signatureClose();
        builder.append(assignment).append(methodClose(SIXTEEN_SPACE)).append(
                TWELVE_SPACE_INDENTATION).append(CLOSE_CURLY_BRACKET)
                .append(getSubTreeFilteredCondition(name)).append(methodClose(EIGHT_SPACE));
        return builder.toString();
    }

    private static String getAppInstanceCondition(String name, String condition) {
        return APP_INSTANCE + PERIOD + name + OPEN_CLOSE_BRACKET_STRING + SPACE +
                condition + EQUAL + SPACE + NULL;
    }

    private static String getSelectOrContainmentAssignString() {
        return TWELVE_SPACE_INDENTATION + IS_ANY_SELECT_OR_CONTAINMENT_NODE_FLAG +
                PERIOD + SET_METHOD_PREFIX + getOpenCloseParaWithValue(ZERO) +
                signatureClose();
    }

    /**
     * Returns filter content match for list types.
     *
     * @param javaAttributeInfo attribute information
     * @param isLeafList        if for leaf list
     * @param node              YANG node
     * @return filter content match for list types
     */
    private static String getSubtreeFilteringForList(
            JavaAttributeInfo javaAttributeInfo, boolean isLeafList,
            YangNode node) {
        StringBuilder builder = new StringBuilder();
        String caps = getCapitalCase(javaAttributeInfo.getAttributeName());
        String name = javaAttributeInfo.getAttributeName();
        String type = javaAttributeInfo.getImportInfo().getClassInfo();
        if (javaAttributeInfo.getAttributeType() != null && javaAttributeInfo
                .getAttributeType().getDataType() == IDENTITYREF) {
            type = CLASS_STRING + DIAMOND_OPEN_BRACKET +
                    QUESTION_MARK + SPACE + EXTEND + SPACE +
                    type + DIAMOND_CLOSE_BRACKET;
        }

        YangDataStructure struct =
                getYangDataStructure(javaAttributeInfo.getCompilerAnnotation());

        boolean isMap = false;
        if (struct != null && struct == YangDataStructure.MAP) {
            isMap = true;
        }

        String clsInfo = DEFAULT_CAPS + type;
        if (javaAttributeInfo.isQualifiedName()) {
            if (javaAttributeInfo.getAttributeType() != null && javaAttributeInfo
                    .getAttributeType().getDataType() == IDENTITYREF) {
                type = CLASS_STRING + DIAMOND_OPEN_BRACKET +
                        QUESTION_MARK + SPACE + EXTEND + SPACE +
                        javaAttributeInfo.getImportInfo().getPkgInfo() + PERIOD +
                        type + DIAMOND_CLOSE_BRACKET;
            } else {
                type = javaAttributeInfo.getImportInfo().getPkgInfo() + PERIOD +
                        type;
            }
            clsInfo = javaAttributeInfo.getImportInfo().getPkgInfo() + PERIOD +
                    clsInfo;
        }

        String classCast = getOpenCloseParaWithValue(clsInfo) + SPACE;
        String cast = getOpenCloseParaWithValue(classCast + name);

        if (node != null && node instanceof YangChoice) {
            cast = name;
        }
        String validPass;
        //If map is in DS then need to pass name.getValue() to processSubTree.
        if (isMap) {
            validPass = name + TWO + PERIOD + GET_VALUE;
        } else {
            validPass = name + TWO;
        }
        String resultString = cast + NEW_LINE + TWENTY_EIGHT_SPACE_INDENTATION +
                PERIOD + PROCESS_SUBTREE_FILTERING + OPEN_PARENTHESIS +
                validPass + COMMA + SPACE + FALSE + CLOSE_PARENTHESIS + SEMI_COLON +
                NEW_LINE;
        /*
         * If select all schema child
         */
        String forCondition;
        String mapEntry;
        String para;
        //If map is there in DS the for loop should run for map entry.
        if (!isMap) {
            forCondition = getForLoopString(SIXTEEN_SPACE_INDENTATION, type, name,
                                            getAppInstanceAttrString(name));
        } else {
            mapEntry = MAP + PERIOD + ENTRY + DIAMOND_OPEN_BRACKET + type +
                    KEYS + COMMA + SPACE + type + DIAMOND_CLOSE_BRACKET + SPACE;
            para = getAppInstanceAttrString(name) + PERIOD + ENTRY_SET;
            forCondition = getForLoopString(SIXTEEN_SPACE_INDENTATION,
                                            mapEntry, name, para);
        }
        builder.append(getIfConditionBegin(EIGHT_SPACE_INDENTATION,
                                           IS_SELECT_ALL_SCHEMA_CHILD_FLAG))
                .append(getIfConditionBegin(TWELVE_SPACE_INDENTATION,
                                            getAppInstanceCondition(name, NOT)))
                .append(forCondition);
        String assignment;
        String result;
        //If map is added then while creating dummy object need to used name
        // .getValue() and to add the result in subTreeBuilder need to pass
        // using key and value, key will be name.getKey() and value will be
        // result.
        if (isMap) {
            result = getOpenCloseParaWithValue(name + PERIOD + GET_KEY + COMMA +
                                                       SPACE + RESULT);
        } else {
            result = getOpenCloseParaWithValue(RESULT);
        }
        if (!isLeafList) {
            builder.append(TWENTY_SPACE_INDENTATION).append(type).append(SPACE)
                    .append(RESULT).append(signatureClose());
            assignment = getDummyObjectCreation(node, name, type, clsInfo,
                                                classCast, true, isMap);
            builder.append(assignment);
            assignment = TWENTY_SPACE_INDENTATION +
                    SUBTREE_FILTERING_RESULT_BUILDER + PERIOD + ADD_STRING +
                    getCapitalCase(TO) + caps + result +
                    signatureClose();
            builder.append(assignment);
        } else {
            assignment = TWENTY_SPACE_INDENTATION +
                    SUBTREE_FILTERING_RESULT_BUILDER + PERIOD + ADD_STRING +
                    getCapitalCase(TO) + caps + getOpenCloseParaWithValue(name) +
                    signatureClose();
            builder.append(assignment);
        }
        builder.append(methodClose(SIXTEEN_SPACE))
                .append(TWELVE_SPACE_INDENTATION).append(CLOSE_CURLY_BRACKET);

/*    } else {
        if (isSubTreeFiltered && leafList2() != null) {
            subTreeFilteringResultBuilder.addToLeafList2(leafList2());
        }
    }*/
        builder.append(getSubTreeFilteredCondition(name));

        String cond = name + OPEN_CLOSE_BRACKET_STRING + SPACE + NOT + EQUAL +
                SPACE + NULL;

        //If need to explicitly participate in query
        builder.append(getElseIfConditionBegin(EIGHT_SPACE_INDENTATION, cond));

        if (!isLeafList) {
            builder.append(getSelectOrContainmentAssignString());
        }

        //If there is any parameter in the query condition
        cond = NOT + name + OPEN_CLOSE_BRACKET_STRING + PERIOD + IS_EMPTY;
        builder.append(getIfConditionBegin(TWELVE_SPACE_INDENTATION, cond));

        if (isLeafList) {
            cond = getAppInstanceCondition(name, EQUAL) + SPACE + OR_OPERATION +
                    SPACE + APP_INSTANCE + PERIOD + name +
                    OPEN_CLOSE_BRACKET_STRING + PERIOD + IS_EMPTY;
            /*
             * If there is no app instance to perform content match
             */
            builder.append(getIfConditionBegin(SIXTEEN_SPACE_INDENTATION, cond))
                    .append(getReturnString(FALSE, TWENTY_SPACE_INDENTATION))
                    .append(signatureClose())
                    .append(methodClose(SIXTEEN_SPACE))
                    // for instance iterator
                    .append(getForLoopString(SIXTEEN_SPACE_INDENTATION, type, name,
                                             name + OPEN_CLOSE_BRACKET_STRING));

            assignment = TWENTY_SPACE_INDENTATION + BOOLEAN_DATA_TYPE + SPACE +
                    FLAG + SPACE + EQUAL + SPACE + FALSE + signatureClose();
            builder.append(assignment)
                    // for app instance iterator
                    .append(getForLoopString(TWENTY_SPACE_INDENTATION, type,
                                             name + TWO,
                                             getAppInstanceAttrString(name)));

            cond = name + PERIOD + EQUALS_STRING
                    + OPEN_PARENTHESIS + name + TWO + CLOSE_PARENTHESIS;
            //the content match leaf list attribute value matches
            builder.append(getIfConditionBegin(TWENTY_FOUR_SPACE_INDENTATION,
                                               cond));

            assignment = TWENTY_EIGHT_SPACE_INDENTATION + FLAG + SPACE + EQUAL +
                    SPACE + TRUE + SEMI_COLON + NEW_LINE;
            builder.append(assignment);
            assignment = TWENTY_EIGHT_SPACE_INDENTATION +
                    SUBTREE_FILTERING_RESULT_BUILDER + PERIOD + ADD_STRING +
                    getCapitalCase(TO) + caps + getOpenCloseParaWithValue(name + TWO) +
                    signatureClose();
            builder.append(assignment).append(TWENTY_EIGHT_SPACE_INDENTATION)
                    .append(BREAK).append(signatureClose())
                    //the content match leaf list attribute value matches
                    .append(methodClose(TWENTY_FOUR_SPACE))
                    // for app instance iterator
                    .append(methodClose(TWENTY_SPACE))
                    //if the content match failed
                    .append(getIfConditionBegin(TWENTY_SPACE_INDENTATION, NOT +
                            FLAG))
                    .append(getReturnString(FALSE, TWENTY_FOUR_SPACE_INDENTATION))
                    .append(signatureClose()).append(methodClose(TWENTY_SPACE))// if flag == false
                    .append(methodClose(SIXTEEN_SPACE)); // for instance iterator
        } else {
            cond = getAppInstanceCondition(name, NOT) + SPACE + AND_OPERATION +
                    SPACE + NOT + getAppInstanceAttrString(name) +
                    PERIOD + IS_EMPTY;
            //Same here for loop for map entry.
            if (!isMap) {
                forCondition = getForLoopString(TWENTY_SPACE_INDENTATION, type,
                                                name + TWO,
                                                getAppInstanceAttrString(name));
            } else {
                mapEntry = MAP + PERIOD + ENTRY + DIAMOND_OPEN_BRACKET + type +
                        KEYS + COMMA + SPACE + type + DIAMOND_CLOSE_BRACKET + SPACE;
                para = getAppInstanceAttrString(name) + PERIOD + ENTRY_SET;
                forCondition = getForLoopString(TWENTY_SPACE_INDENTATION,
                                                mapEntry, name + TWO, para);
            }
            String forCondition2;
            //Same here for loop for map entry.
            if (!isMap) {
                forCondition2 = getForLoopString(SIXTEEN_SPACE_INDENTATION, type, name,
                                                 name + OPEN_CLOSE_BRACKET_STRING);
            } else {
                mapEntry = MAP + PERIOD + ENTRY + DIAMOND_OPEN_BRACKET + type +
                        KEYS + COMMA + SPACE + type + DIAMOND_CLOSE_BRACKET + SPACE;
                para = name + OPEN_CLOSE_BRACKET_STRING + PERIOD + ENTRY_SET;
                forCondition2 = getForLoopString(SIXTEEN_SPACE_INDENTATION,
                                                 mapEntry, name, para);
            }
            /*if there is any app instance entry*/
            builder.append(getIfConditionBegin(SIXTEEN_SPACE_INDENTATION,
                                               cond))
                    //loop all the app instance(s)
                    .append(forCondition2)
                    .append(forCondition);

            assignment = TWENTY_EIGHT_SPACE_INDENTATION + type + SPACE +
                    RESULT + SPACE + EQUAL + SPACE + resultString;
            builder.append(assignment);
            cond = RESULT + SPACE + NOT + EQUAL + SPACE + NULL;
            builder.append(getIfConditionBegin(TWENTY_EIGHT_SPACE_INDENTATION, cond));

            //If map is added then while creating dummy object need to used name
            // .getValue() and to add the result in subTreeBuilder need to pass
            // using key and value, key will be name.getKey() and value will be
            // result.
            if (isMap) {
                result = getOpenCloseParaWithValue(name + TWO + PERIOD + GET_KEY +
                                                           COMMA +
                                                           SPACE + RESULT);
            } else {
                result = getOpenCloseParaWithValue(RESULT);
            }
            assignment = THIRTY_TWO_SPACE_INDENTATION +
                    SUBTREE_FILTERING_RESULT_BUILDER + PERIOD + ADD_STRING +
                    getCapitalCase(TO) + caps + result + signatureClose();
            builder.append(assignment).append(methodClose(TWENTY_EIGHT_SPACE))
                    //loop all the app instance(s)
                    .append(methodClose(TWENTY_FOUR_SPACE))
                    //loop all the query condition instance(s)
                    .append(methodClose(TWENTY_SPACE))
                    .append(SIXTEEN_SPACE_INDENTATION).append(CLOSE_CURLY_BRACKET)
                    .append(getSubTreeFilteredCondition(name));
            //if there is any app instance entry
        }

        cond = TWELVE_SPACE_INDENTATION + CLOSE_CURLY_BRACKET
                + ELSE + OPEN_CURLY_BRACKET + NEW_LINE;
        builder.append(cond);

        if (isLeafList) {
            builder.append(getSelectOrContainmentAssignString());
        }

        //need to pass name.getKey() and name.getValue() while adding to
        // subtree builder.
        if (!isMap) {
            forCondition = getForLoopString(SIXTEEN_SPACE_INDENTATION, type,
                                            name, getAppInstanceAttrString(name));
        } else {
            mapEntry = MAP + PERIOD + ENTRY + DIAMOND_OPEN_BRACKET + type +
                    KEYS + COMMA + SPACE + type + DIAMOND_CLOSE_BRACKET + SPACE;
            para = getAppInstanceAttrString(name) + PERIOD + ENTRY_SET;
            forCondition = getForLoopString(SIXTEEN_SPACE_INDENTATION,
                                            mapEntry, name, para);
        }
        cond = getAppInstanceCondition(name, NOT) + SPACE + AND_OPERATION +
                SPACE + NOT + getAppInstanceAttrString(name) + PERIOD + IS_EMPTY;
        builder.append(getIfConditionBegin(SIXTEEN_SPACE_INDENTATION, cond))
                .append(forCondition);
        if (isMap) {
            result = getOpenCloseParaWithValue(name + PERIOD + GET_KEY + COMMA +
                                                       SPACE + name + PERIOD +
                                                       GET_VALUE);
        } else {
            result = getOpenCloseParaWithValue(name);
        }
        assignment = TWENTY_FOUR_SPACE_INDENTATION +
                SUBTREE_FILTERING_RESULT_BUILDER + PERIOD + ADD_STRING +
                getCapitalCase(TO) + caps + result + signatureClose();
        builder.append(assignment).append(methodClose(TWENTY_SPACE))// Close collection Iteration loop
                // close  if condition
                .append(methodClose(SIXTEEN_SPACE))
                .append(methodClose(TWELVE_SPACE))// close  else condition
                .append(methodClose(EIGHT_SPACE));// close else if condition

        return builder.toString();
    }

    public static String getAugmentableSubTreeFiltering() {
        return "        if (yangAugmentedInfoMap.isEmpty()) {\n" +
                "            java.util.Set<Map.Entry<Class<?>, Object>> " +
                "augment =" +
                " appInstance.yangAugmentedInfoMap().entrySet();\n" +
                "            if (augment != null && !augment.isEmpty()) {\n" +
                "                java.util.Iterator<Map.Entry<Class<?>, " +
                "Object>> augItr =" +
                " augment.iterator();\n" +
                "                while (augItr.hasNext()) {\n" +
                "                    Map.Entry<Class<?>, Object> aug =" +
                " augItr.next();\n" +
                "                    Class<?> augClass = aug.getKey();\n" +
                "                    String augClassName = augClass.getName();\n" +
                "                    int index = augClassName.lastIndexOf('.');\n" +
                "                    String classPackage = augClassName.substring(0, index) +\n" +
                "                            \".\" + \"Default\" + augClass.getSimpleName() + \"$\"\n" +
                "                            + augClass.getSimpleName() + \"Builder\";\n" +
                "                    ClassLoader classLoader = augClass.getClassLoader();\n" +
                "                    try {\n" +
                "                        Class<?> builderClass;\n" +
                "                        builderClass = classLoader.loadClass(classPackage);\n" +
                "                        Object builderObj = builderClass.newInstance();\n" +
                "                        java.lang.reflect.Method method = " +
                "builderClass.getMethod(\"build\");\n" +
                "                        Object defaultObj = method.invoke(builderObj);\n" +
                "                        Class<?> defaultClass = defaultObj.getClass();\n" +
                "                        method = defaultClass.getMethod(" +
                "\"processSubtreeFiltering\", augClass, boolean.class);\n" +
                "                        Object result = method.invoke(" +
                "defaultObj, aug.getValue(), true);\n" +
                "                        subTreeFilteringResultBuilder." +
                "addYangAugmentedInfo(result, augClass);\n" +
                "                    } catch (ClassNotFoundException | InstantiationException\n" +
                "                            | NoSuchMethodException |\n" +
                "                            InvocationTargetException | IllegalAccessException e) {\n" +
                "                    }\n" +
                "                }\n" +
                "            }\n" +
                "        } else {\n" +
                "            java.util.Set<Map.Entry<Class<?>, Object>> augment = yangAugmentedInfoMap\n" +
                "                    .entrySet();\n" +
                "            java.util.Iterator<Map.Entry<Class<?>, Object>> augItr = augment.iterator();\n" +
                "            while (augItr.hasNext()) {\n" +
                "                Map.Entry<Class<?>, Object> aug = augItr.next();\n" +
                "                Class<?> augClass = aug.getKey();\n" +
                "                Object appInstanceInfo = appInstance." +
                "yangAugmentedInfo(augClass);\n" +
                "                if (appInstanceInfo == null) {\n" +
                "                    subTreeFilteringResultBuilder." +
                "addYangAugmentedInfo(aug.getValue(), aug.getKey());\n" +
                "                } else {\n" +
                "                    Object processSubtreeFiltering;\n" +
                "                    try {\n" +
                "                        processSubtreeFiltering = " +
                "aug.getValue().getClass()\n" +
                "                                .getMethod(\"processSubtreeFiltering\"," +
                " aug.getKey(), boolean.class)\n" +
                "                                .invoke(aug.getValue(), appInstanceInfo, true);\n" +
                "                        if (processSubtreeFiltering != null) {\n" +
                "                            subTreeFilteringResultBuilder\n" +
                "                                    .addYangAugmentedInfo(" +
                "processSubtreeFiltering, aug.getKey());\n" +
                "                        }\n" +
                "                    } catch (NoSuchMethodException | InvocationTargetException |\n" +
                "                            IllegalAccessException e) {\n" +
                "                    }\n" +
                "                }\n" +
                "            }\n" +
                "        }\n";
    }

    private static String getSubTreeFilteredCondition(String name) {
        StringBuilder builder = new StringBuilder();
        String cond = SUBTREE_FILTERED + SPACE + AND_OPERATION + SPACE + name +
                OPEN_CLOSE_BRACKET_STRING + SPACE + NOT + EQUAL + SPACE + NULL;

        builder.append(ELSE).append(OPEN_CURLY_BRACKET).append(NEW_LINE)
                .append(getIfConditionBegin(SIXTEEN_SPACE_INDENTATION, cond))
                .append(TWENTY_SPACE_INDENTATION)
                .append(SUBTREE_FILTERING_RESULT_BUILDER).append(PERIOD)
                .append(name).append(getOpenCloseParaWithValue(name)).append(
                signatureClose()).append(SIXTEEN_SPACE_INDENTATION).append(
                CLOSE_CURLY_BRACKET).append(NEW_LINE).append(TWELVE_SPACE_INDENTATION)
                .append(CLOSE_CURLY_BRACKET).append(NEW_LINE);
        return builder.toString();
    }

    private static String getNameOfClassForIfCase(YangNode curNode) {
        String name = null;
        JavaFileInfoTranslator parentInfo;
        if (curNode instanceof YangCase) {
            YangNode parent = curNode.getParent();
            if (parent instanceof YangChoice) {
                parentInfo = ((JavaFileInfoContainer) parent).getJavaFileInfo();
                name = getCapitalCase(parentInfo.getJavaName());
            } else if (parent instanceof YangAugment) {
                parentInfo = ((JavaFileInfoContainer) ((YangAugment) parent)
                        .getAugmentedNode()).getJavaFileInfo();
                if (parentInfo != null) {
                    name = getCapitalCase(parentInfo.getJavaName());
                } else {
                    name = getCapitalCase(getCamelCase(
                            ((YangAugment) parent).getAugmentedNode().getName(),
                            null));
                }
            }
        }
        return name;
    }

    private static String getCaseCastString(
            JavaFileInfoTranslator javaFileInfo, String instance, YangNode curNode) {
        if (curNode instanceof YangCase) {
            String caseName = DEFAULT_CAPS + getCapitalCase(
                    javaFileInfo.getJavaName());
            return EIGHT_SPACE_INDENTATION + caseName + SPACE + APP_INSTANCE + SPACE +
                    EQUAL + SPACE + OPEN_PARENTHESIS + caseName +
                    CLOSE_PARENTHESIS + SPACE + instance + signatureClose();
        }
        return null;
    }

    private static String getDummyObjectCreation(YangNode node, String name,
                                                 String clsInfo, String type,
                                                 String classCast, boolean isList,
                                                 boolean isMap) {
        String para = getAppInstanceAttrString(name);
        if (isList) {
            if (isMap) {
                para = name + PERIOD + GET_VALUE;
            } else {
                para = name;
            }
        }
        if (node != null && node instanceof YangChoice) {
            return getChoiceReflectionResult(name, clsInfo);
        }
        return TWENTY_SPACE_INDENTATION + RESULT + SPACE + EQUAL + SPACE +
                getOpenCloseParaWithValue(
                        classCast + type + PERIOD + BUILDER_LOWER_CASE +
                                OPEN_CLOSE_BRACKET_STRING + NEW_LINE +
                                TWENTY_EIGHT_SPACE_INDENTATION + PERIOD +
                                BUILD_FOR_FILTER + OPEN_CLOSE_BRACKET_STRING) +
                PERIOD + PROCESS_SUBTREE_FILTERING + getOpenCloseParaWithValue(
                para + COMMA + SPACE + TRUE) + signatureClose();
    }

    private static String getChoiceReflectionResult(String name, String returnType) {
        String call = "appInstance." + name + "()";
        return "                    Class<?>[] classArray = " + call + "" +
                ".getClass()" +
                ".getInterfaces();\n" +
                "                    Class<?> caseIntf = classArray[0];\n" +
                "                    try {\n" +
                "                        ClassLoader classLoader = this" +
                ".getClass().getClassLoader();\n" +
                "                        String className = \"Default\" + " +
                "caseIntf.getSimpleName();\n" +
                "                        className = caseIntf.getPackage()" +
                ".getName() + \".\" + className;\n" +
                "                        Class<?> caseClass = classLoader" +
                ".loadClass(className);\n" +
                "                        java.lang.reflect.Constructor<?> " +
                "constructor = caseClass.getDeclaredConstructor();\n" +
                "                        constructor.setAccessible(true);\n" +
                "                        Object obj1 = constructor" +
                ".newInstance();\n" +
                "                        java.lang.reflect.Method method =" +
                " caseClass.getMethod(\"builder\");\n" +
                "                        Object obj = method.invoke(obj1);\n" +
                "                        Class<?> builderClass = obj.getClass();\n" +
                "                        method = builderClass.getMethod(\"build\");\n" +
                "                        Object obj2 = method.invoke(obj);\n" +
                "                        Class<?> input = this.getClass()" +
                ".getMethod(" + "\"" + name + "\").getReturnType();\n" +
                "                        method = caseClass.getMethod" +
                "(\"processSubtreeFiltering\", input,\n" +
                "                                     boolean.class);\n" +
                "                        result = (" + returnType + ") method.invoke" +
                "(obj2, " + call + ", true);\n" +
                "                    } catch (NoSuchMethodException | InstantiationException |\n" +
                "                            IllegalAccessException | " +
                "InvocationTargetException |\n" +
                "                            ClassNotFoundException e) {\n" +
                "                        e.printStackTrace();\n" +
                "                    }\n";
    }
}
