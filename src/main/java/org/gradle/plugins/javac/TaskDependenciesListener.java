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

import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.TreeVisitor;
import com.sun.source.util.JavacTask;
import com.sun.source.util.TaskEvent;
import com.sun.source.util.TaskListener;
import com.sun.source.util.TreeScanner;

import javax.lang.model.type.TypeMirror;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class TaskDependenciesListener implements TaskListener {

    private final JavacTask task;
    private final File outputFile;

    public TaskDependenciesListener(final JavacTask javacTask, final File outputFile) {
        this.task = javacTask;
        this.outputFile = outputFile;
    }

    @Override
    public void started(final TaskEvent taskEvent) {

    }

    @Override
    public void finished(final TaskEvent taskEvent) {
        if (taskEvent.getKind() == TaskEvent.Kind.ANALYZE) {
            Set<String> collectedTypes = new HashSet<>();
            TreeVisitor<Void, Set<String>> visitor = new TreeScanner<Void, Set<String>>() {
                @Override
                public Void visitIdentifier(final IdentifierTree identifierTree, final Set<String> strings) {
                    TypeMirror typeMirror = task.getTypeMirror(Collections.singleton(identifierTree));
                    switch (typeMirror.getKind()) {
                        case DECLARED:
                            strings.add(typeMirror.toString());
                    }
                    return super.visitIdentifier(identifierTree, strings);
                }
            };
            taskEvent.getCompilationUnit().accept(visitor, collectedTypes);
            try {
                appendResult(taskEvent.getTypeElement().getQualifiedName().toString(), collectedTypes);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void appendResult(final String s, final Set<String> collectedTypes) throws IOException {
        if (collectedTypes.isEmpty()) {
            return;
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile, true))) {
            writer.write(s);
            writer.write(":");
            boolean comma = false;
            for (String collectedType : collectedTypes) {
                if (comma) {
                    writer.append(",");
                }
                writer.append(collectedType);
                comma = true;
            }
            writer.write("\n");
        }
    }

}
