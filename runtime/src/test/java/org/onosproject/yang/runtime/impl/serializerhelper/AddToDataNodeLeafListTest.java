/*
 * Copyright 2017-present Open Networking Laboratory
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

package org.onosproject.yang.runtime.impl.serializerhelper;

import org.junit.Test;
import org.onosproject.yang.model.DataNode;
import org.onosproject.yang.model.ResourceId;
import org.onosproject.yang.runtime.HelperContext;
import org.onosproject.yang.runtime.impl.TestYangSerializerContext;

import static org.onosproject.yang.model.DataNode.Type.MULTI_INSTANCE_LEAF_VALUE_NODE;
import static org.onosproject.yang.runtime.SerializerHelper.addDataNode;
import static org.onosproject.yang.runtime.SerializerHelper.getResourceId;
import static org.onosproject.yang.runtime.SerializerHelper.initializeDataNode;
import static org.onosproject.yang.runtime.SerializerHelper.initializeResourceId;
import static org.onosproject.yang.runtime.impl.TestUtils.LNS;
import static org.onosproject.yang.runtime.impl.TestUtils.validateDataNode;
import static org.onosproject.yang.runtime.impl.TestUtils.validateResourceId;
import static org.onosproject.yang.runtime.impl.TestUtils.walkINTree;

/**
 * Tests the serializer helper methods.
 */
public class AddToDataNodeLeafListTest {

    TestYangSerializerContext context = new TestYangSerializerContext();

    /*
     * Reference for data node info.
     */
    HelperContext info;

    /*
     * Reference for data node builder.
     */
    DataNode.Builder dBlr;

    /*
     * Reference for resource id.
     */
    ResourceId id;

    /*
     * Reference for the value.
     */
    String value;

    /*
     * Reference for string array to used for resource id testing.
     */
    String[] nA;
    String[] nsA;
    String[] valA;

    private static final String[] EXPECTED = {
            "Entry Node is leaf1.",
            "Exit Node is leaf1."
    };

    /**
     * Test add to data node after initializing it with resource builder.
     */
    @Test
    public void addToDataLeafListTest() {

        ResourceId.Builder rIdBlr = initializeResourceId(context);
        dBlr = initializeDataNode(rIdBlr);
        value = "0";
        dBlr = addDataNode(dBlr, "leaf1", LNS, value, null);
        info = (HelperContext) dBlr.appInfo();
        id = getResourceId(dBlr);

        //Tree validation
        nA = new String[]{"/", "leaf1"};
        nsA = new String[]{null, LNS};
        valA = new String[]{"0"};
        validateResourceId(nA, nsA, valA, id);

        DataNode node = dBlr.build();
        validateDataNode(node, "leaf1", LNS, MULTI_INSTANCE_LEAF_VALUE_NODE,
                         false, "0");
        walkINTree(node, EXPECTED);
    }
}
