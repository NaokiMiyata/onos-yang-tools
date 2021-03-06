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

package org.onosproject.yang.compiler.plugin.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Test;
import org.onosproject.yang.compiler.datamodel.YangNode;
import org.onosproject.yang.compiler.parser.exceptions.ParserException;
import org.onosproject.yang.compiler.parser.impl.YangUtilsParserManager;
import org.onosproject.yang.compiler.tool.YangCompilerManager;
import org.onosproject.yang.compiler.utils.io.YangPluginConfig;
import org.onosproject.yang.compiler.utils.io.impl.YangIoUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import static org.onosproject.yang.compiler.translator.tojava.JavaCodeGeneratorUtil.generateJavaCode;
import static org.onosproject.yang.compiler.utils.io.impl.YangFileScanner.getYangFiles;

/**
 * Unit tests for choice-case translator.
 */
public final class ChoiceCaseTranslatorTest {
    private static final String DIR = "target/ChoiceCaseTestGenFile/";
    private static final String COMP = System.getProperty("user.dir") + File
            .separator + DIR;
    private final YangCompilerManager utilManager = new YangCompilerManager();
    private final YangUtilsParserManager manager = new YangUtilsParserManager();

    /**
     * Checks choice-case translation should not result in any exception.
     */
    @Test
    public void processChoiceCaseTranslator() throws IOException, ParserException {

        String dir = "target/ChoiceCaseTestGenFile/";
        YangIoUtils.deleteDirectory(dir);
        YangNode node = manager.getDataModel("src/test/resources/ChoiceCaseTranslator.yang");

        YangPluginConfig yangPluginConfig = new YangPluginConfig();
        yangPluginConfig.setCodeGenDir(dir);

        generateJavaCode(node, yangPluginConfig);
        String dir1 = System.getProperty("user.dir") + File.separator + dir;
        YangPluginConfig.compileCode(dir1);
        YangIoUtils.deleteDirectory(dir);
    }

    /**
     * Checks augment translation should not result in any exception.
     *
     * @throws MojoExecutionException
     */
    @Test
    public void processChoiceAllTranslator() throws IOException,
            ParserException, MojoExecutionException {
        YangIoUtils.deleteDirectory(DIR);
        String searchDir = "src/test/resources/choiceTranslator";

        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(searchDir)) {
            paths.add(Paths.get(file));
        }

        utilManager.createYangFileInfoSet(paths);
        utilManager.parseYangFileInfoSet();
        utilManager.createYangNodeSet();
        utilManager.resolveDependenciesUsingLinker();

        YangPluginConfig yangPluginConfig = new YangPluginConfig();
        yangPluginConfig.setCodeGenDir(DIR);
        utilManager.translateToJava(yangPluginConfig);
        YangPluginConfig.compileCode(COMP);
        YangIoUtils.deleteDirectory(DIR);
    }
}
