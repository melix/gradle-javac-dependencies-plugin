/*
 * Copyright 2003-2012 the original author or authors.
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
package org.gradle.plugins.javac;

import com.sun.source.util.JavacTask;
import com.sun.source.util.Plugin;

import java.io.File;

public class ClassDependenciesPlugin implements Plugin {
    @Override
    public java.lang.String getName() {
        return ClassDependenciesPlugin.class.getSimpleName();
    }

    @Override
    public void init(final JavacTask javacTask, final String... args) {
        File outputFile = args.length == 1 ? new File(args[0]) : new File("analysis.txt");
        if (outputFile.exists()) {
            outputFile.delete();
        }
        javacTask.addTaskListener(new TaskDependenciesListener(javacTask, outputFile));
    }
}
