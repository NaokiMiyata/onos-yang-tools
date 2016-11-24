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
package org.onosproject.yangutils.plugin.buck;

import com.facebook.buck.io.ProjectFilesystem;
import com.facebook.buck.model.UnflavoredBuildTarget;
import com.facebook.buck.rules.BuildRule;
import com.facebook.buck.step.AbstractExecutionStep;
import com.facebook.buck.step.ExecutionContext;
import com.facebook.buck.step.StepExecutionResult;
import com.google.common.collect.ImmutableSortedSet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.onosproject.yangutils.utils.UtilConstants.JAR;
import static org.onosproject.yangutils.utils.UtilConstants.LIB;
import static org.onosproject.yangutils.utils.UtilConstants.LIB_PATH;
import static org.onosproject.yangutils.utils.UtilConstants.OUT;
import static org.onosproject.yangutils.utils.UtilConstants.PERIOD;
import static org.onosproject.yangutils.utils.UtilConstants.SLASH;

/**
 * Buck build step to trigger Yang Java source file generation.
 */
public class YangStep extends AbstractExecutionStep {

    private static final String DESCRIPTION = "yang-compile";
    private final ImmutableSortedSet<BuildRule> deps;
    private final ProjectFilesystem filesystem;
    private final List<Path> srcs;
    private final Path output;

    YangStep(ProjectFilesystem filesystem,
             List<Path> srcs,
             Path genSourcesDirectory, ImmutableSortedSet<BuildRule> deps) {
        super(DESCRIPTION);
        this.filesystem = filesystem;
        this.srcs = srcs;
        this.deps = deps;
        this.output = genSourcesDirectory;
    }

    @Override
    public StepExecutionResult execute(ExecutionContext executionContext)
            throws IOException, InterruptedException {

        List<File> sourceFiles = srcs.stream().map(Path::toFile)
                .collect(Collectors.toList());
        try {
            new YangGenerator(sourceFiles, output.toString(), getJarPaths())
                    .execute();
            return StepExecutionResult.SUCCESS;
        } catch (YangParsingException e) {
            executionContext.getConsole().printErrorText(e.getMessage());
            return StepExecutionResult.ERROR;
        }
    }

    private List<String> getJarPaths() {
        StringBuilder builder;
        List<String> depJarPaths = new ArrayList<>();
        String[] array;
        UnflavoredBuildTarget uBt;
        if (deps != null) {
            for (BuildRule rule : deps) {
                if (!rule.getBuildTarget().getFullyQualifiedName()
                        .contains(LIB_PATH)) {
                    builder = new StringBuilder();
                    //build absolute path for jar file
                    builder.append(filesystem.getRootPath().toString()).append(SLASH)
                            .append(filesystem.getBuckPaths().getGenDir())
                            .append(SLASH);
                    uBt = rule.getBuildTarget().getUnflavoredBuildTarget();
                    array = uBt.getBaseName().split(SLASH);
                    for (int i = 2; i < array.length; i++) {
                        builder.append(array[i]).append(SLASH);
                    }
                    builder.append(LIB).append(uBt.getShortName())
                            .append(OUT).append(SLASH)
                            .append(uBt.getShortName()).append(PERIOD + JAR);
                    depJarPaths.add(builder.toString());
                }
            }
        }
        return depJarPaths;
    }
}
