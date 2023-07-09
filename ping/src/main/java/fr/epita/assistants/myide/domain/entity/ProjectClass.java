package fr.epita.assistants.myide.domain.entity;

import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class ProjectClass implements Project {

    private Node root;
    private Set<Aspect> aspects;

    public ProjectClass(Node root)
    {
        this.root = root ;
        this.aspects = new HashSet<>();
        aspects.add(AnyAspect.INSTANCE);
        if (root.getPath().resolve("pom.xml").toFile().isFile())
            aspects.add(MavenAspect.INSTANCE);
        if (root.getPath().resolve(".git").toFile().isDirectory())
            aspects.add(GitAspect.INSTANCE);
    }

    @Override
    public Node getRootNode() {
        return root;
    }

    @Override
    public Set<Aspect> getAspects() {
        return aspects;
    }

    @Override
    public Optional<Feature> getFeature(Feature.Type featureType) {
        for (Aspect aspect : aspects)
        {
            for (Feature ft : aspect.getFeatureList())
            {
                if (ft.type().equals(featureType))
                {
                    return Optional.of(ft);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<@NotNull Feature> getFeatures() {
        return Project.super.getFeatures();
    }
}
