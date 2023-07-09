package fr.epita.assistants.myide.domain.entity;

import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeCommand;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.RemoteRefUpdate;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public enum GitAspect implements Aspect{
    INSTANCE;

    @Override
    public Type getType() {
        return Mandatory.Aspects.GIT;
    }

    @Override
    public List<Feature> getFeatureList() {
        return Arrays.asList(GitAspect.GitFeature.values());
        //return Aspect.super.getFeatureList();
    }

    public enum GitFeature implements Feature{
        /**
         * Git pull, fast-forward if possible.
         */
        PULL {
            @Override
            public ExecutionReport execute(Project project, Object... params) {
                try {
                    Repository repository = FileRepositoryBuilder.create(project.getRootNode().getPath().resolve(".git").toFile());
                    Git git = new Git(repository);
                    git.pull().setFastForward(MergeCommand.FastForwardMode.FF_ONLY).call();
                    git.close();
                    return () -> true;
                }
                catch (Exception e) {
                    return () -> false;
                }
            }

            @Override
            public Type type() {
                return Mandatory.Features.Git.PULL;
            }
        },
        /**
         * Git add.
         */
        ADD {
            @Override
            public ExecutionReport execute(Project project, Object... params) {
                try {
                    Repository repository = FileRepositoryBuilder.create(project.getRootNode().getPath().resolve(".git").toFile());
                    Git git = new Git(repository);
                    AddCommand add = git.add();
                    for (Object o : params)
                        if (Files.exists(Paths.get(o.toString())))
                            add.addFilepattern(o.toString());
                        else
                            return () -> false;
                    add.call();
                    git.close();
                    return () -> true;
                }
                catch (Exception e) {
                    return () -> false;
                }
            }

            @Override
            public Type type() {
                return Mandatory.Features.Git.ADD;
            }
        },

        /**
         * Git commit.
         */
        COMMIT {
            @Override
            public ExecutionReport execute(Project project, Object... params) {
                try {
                    Repository repository = FileRepositoryBuilder.create(project.getRootNode().getPath().resolve(".git").toFile());
                    Git git = new Git(repository);
                    CommitCommand commit = git.commit();
                    if (params.length > 0)
                        commit.setMessage(params[0].toString());
                    else
                        return () -> false;
                    commit.call();
                    git.close();
                    return () -> true;
                }
                catch (Exception e) {
                    return () -> false;
                }
            }

            @Override
            public Type type() {
                return Mandatory.Features.Git.COMMIT;
            }
        },

        /**
         * Git push (no force).
         */
        PUSH {
            @Override
            public ExecutionReport execute(Project project, Object... params) {
                try {
                    Repository repository = FileRepositoryBuilder.create(project.getRootNode().getPath().resolve(".git").toFile());
                    Git git = new Git(repository);
                    Iterable<PushResult> results = git.push().setRemote("origin").call();
                    for (PushResult result : results)
                        if (result.getRemoteUpdate("origin").getStatus() == RemoteRefUpdate.Status.UP_TO_DATE)
                            return () -> false;
                    git.close();
                    return () -> true;
                }
                catch (Exception e) {
                    return () -> false;
                }
            }
            @Override
            public Type type() {
                return Mandatory.Features.Git.PUSH;
            }
        }
    }
}
