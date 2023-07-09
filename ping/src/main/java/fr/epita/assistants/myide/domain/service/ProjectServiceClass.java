package fr.epita.assistants.myide.domain.service;

import fr.epita.assistants.myide.domain.entity.Feature;
import fr.epita.assistants.myide.domain.entity.NodeClass;
import fr.epita.assistants.myide.domain.entity.Project;
import fr.epita.assistants.myide.domain.entity.ProjectClass;

import javax.validation.constraints.NotNull;
import java.nio.file.Path;
import java.util.Optional;

public enum ProjectServiceClass implements ProjectService {
    INSTANCE;

    /**
     * Load a {@link Project} from a path.
     *
     * @param root Path of the root of the project to load.
     * @return New project.
     */
    @Override
    public Project load(Path root) {
        return new ProjectClass(new NodeClass(root));
    }

    /**
     * Execute the given feature on the given project.
     *
     * @param project     Project for which the features is executed.
     * @param featureType Type of the feature to execute.
     * @param params      Parameters given to the features.
     * @return Execution report of the feature.
     */
    @Override
    public Feature.ExecutionReport execute(Project project, Feature.Type featureType, Object... params) {
        @NotNull Optional<Feature> feature = project.getFeature(featureType);
        if (feature.isPresent())
        {
            return  feature.get().execute(project, params);
        }
        return () -> false;
    }

    /**
     * @return The {@link NodeService} associated with your {@link ProjectService}
     */
    @Override
    public NodeService getNodeService() {
        return NodeServiceClass.INSTANCE; // tqt ca arrive
    }
}

