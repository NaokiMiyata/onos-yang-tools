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

package org.onosproject.yang.compiler.plugin.buck;

import org.onosproject.yang.compiler.api.YangCompilationParam;
import org.onosproject.yang.compiler.api.YangCompiledOutput;
import org.onosproject.yang.compiler.api.YangCompilerException;
import org.onosproject.yang.compiler.api.YangCompilerService;
import org.onosproject.yang.compiler.tool.DefaultYangCompilationParam;
import org.onosproject.yang.compiler.tool.YangCompilerManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import static org.onosproject.yang.compiler.datamodel.utils.DataModelUtils.parseDepSchemaPath;
import static org.onosproject.yang.compiler.utils.UtilConstants.SLASH;
import static org.onosproject.yang.compiler.utils.UtilConstants.YANG_RESOURCES;

/**
 * Generates Java sources from a Yang model.
 */
public class YangGenerator {

    private final List<File> models;
    private final List<String> depJar;
    private String outputDirectory;
    private YangCompiledOutput output;

    /**
     * Creates an instance of YANG generator.
     *
     * @param models          YANG models
     * @param outputDirectory output directory
     * @param depJar          dependent jar paths
     */
    YangGenerator(List<File> models, String outputDirectory, List<String> depJar) {
        this.models = models;
        this.depJar = depJar;
        this.outputDirectory = outputDirectory + SLASH;
    }

    /**
     * Executes YANG library code generation step.
     *
     * @throws YangParsingException when fails to parse yang files
     */
    public void execute() throws YangParsingException {
        synchronized (YangGenerator.class) {
            //Yang compiler service.
            YangCompilerService compiler = new YangCompilerManager();

            //Create compiler param.
            YangCompilationParam param = new DefaultYangCompilationParam();

            //Need to get dependent schema paths to give inter jar dependencies.
            for (String jar : depJar) {
                try {
                    File path = parseDepSchemaPath(jar, outputDirectory);
                    if (path != null) {
                        param.addDependentSchema(Paths.get(path.getAbsolutePath()));
                    }
                } catch (IOException e) {
                    throw new YangCompilerException(
                            "Failed to parse dependent schema path");
                }
            }
            param.setCodeGenDir(Paths.get(outputDirectory));
            param.setMetadataGenDir(Paths.get(outputDirectory + SLASH +
                                                      YANG_RESOURCES + SLASH));

            for (File file : models) {
                param.addYangFile(Paths.get(file.getAbsolutePath()));
            }

            //Compile yang files and generate java code.
            try {
                output = compiler.compileYangFiles(param);
            } catch (IOException e) {
                throw new YangParsingException(e);
            }
        }
    }

    /**
     * Returns YANG compiled output.
     *
     * @return YANG compiled output
     */
    public YangCompiledOutput output() {
        return output;
    }
}
