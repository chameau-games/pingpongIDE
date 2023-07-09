package fr.epita.assistants.myide.domain.entity;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public enum MavenAspect implements Aspect{
    INSTANCE;

    @Override
    public Type getType() {
        return Mandatory.Aspects.MAVEN;}

    @Override
    public List<Feature> getFeatureList() {
        return Arrays.asList(MavenAspect.MavenFeature.values());
        //return Aspect.super.getFeatureList();
    }

    public static Feature.ExecutionReport maven(Project project, String cmd, Object... params){
        try {
            //try compile
            List<String> args = Arrays.asList("mvn", cmd);
            for (Object param : params)
                args.add(param.toString());
            ProcessBuilder pb = new ProcessBuilder(args);
            pb.directory(project.getRootNode().getPath().toFile());
            Process p = pb.start();
            int exitcode = p.waitFor();

            if (exitcode == 0)
                return () -> true;
            else
                return () -> false;
        } catch (IOException | InterruptedException e) {
            return () -> false;
        }
    }

    public enum MavenFeature implements Feature{
        /**
         * mvn compile
         */
        COMPILE {
            @Override
            public ExecutionReport execute(Project project, Object... params) {
                return maven(project, "compile", params);
            }

            @Override
            public Type type() {
                return Mandatory.Features.Maven.COMPILE;
            }
        },

        /**
         * mvn clean
         */
        CLEAN {
            @Override
            public ExecutionReport execute(Project project, Object... params) {
                return maven(project, "clean", params);
            }

            @Override
            public Type type() {
                return Mandatory.Features.Maven.CLEAN;
            }
        },

        /**
         * mvn test
         */
        TEST {
            @Override
            public ExecutionReport execute(Project project, Object... params) {
                return maven(project, "test", params);

            }

            @Override
            public Type type() {
                return Mandatory.Features.Maven.TEST;
            }
        },

        /**
         * mvn package
         */
        PACKAGE{
            @Override
            public ExecutionReport execute(Project project, Object... params) {
                return maven(project, "package", params);

            }

            @Override
            public Type type() {
                return Mandatory.Features.Maven.PACKAGE;
            }
        },

        /**
         * mvn install
         */
        INSTALL {
            @Override
            public ExecutionReport execute(Project project, Object... params) {
                return maven(project, "install", params);

            }

            @Override
            public Type type() {
                return Mandatory.Features.Maven.INSTALL;
            }
        },

        /**
         * mvn exec:java
         */
        EXEC {
            @Override
            public ExecutionReport execute(Project project, Object... params) {
                return maven(project, "exec:java", params);

            }

            @Override
            public Type type() {
                return Mandatory.Features.Maven.EXEC;
            }
        },

        /**
         * mvn dependency:tree
         */
        TREE {
            @Override
            public ExecutionReport execute(Project project, Object... params) {
                return maven(project, "dependency:tree", params);
            }

            @Override
            public Type type() {
                return Mandatory.Features.Maven.TREE;
            }
        };
    }
}
