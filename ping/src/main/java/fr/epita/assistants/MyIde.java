package fr.epita.assistants;

import fr.epita.assistants.myide.domain.service.ProjectService;
import fr.epita.assistants.myide.domain.service.ProjectServiceClass;
import fr.epita.assistants.utils.Given;

import java.nio.file.Path;

/**
 * Starter class, we will use this class and the init method to get a
 * configured instance of {@link ProjectService}.
 */
@Given(overwritten = false)
public class MyIde {

    /**
     * Init method. It must return a fully functional implementation of {@link ProjectService}.
     *
     * @return An implementation of {@link ProjectService}.
     */
    public static ProjectService init(final Configuration configuration) {
        return ProjectServiceClass.INSTANCE;
        //Set<Aspect> aspects = new HashSet<Aspect>();
        //aspects.add();
        //Node root = new NodeClass(configuration.indexFile());
        //create new projectclass
        //ProjectClass p = new ProjectClass(root);
        //je comprend PAS PTN JE SAIS PAS CE QUE JE FAIS AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAH :)
        // PK IL FAUT RENVOYER UN PROJECTSERVICE CA A AUCUN SENS
    }

    /**
     * Record to specify where the configuration of your IDE
     * must be stored. Might be useful for the search feature.
     */
    public record Configuration(Path indexFile,
                                Path tempFolder) {
    }

    /*public static void main(String[] args) {
        //System.out.println("kk");
        ProjectService projectService = init(new Configuration(null, null));
        Project project = new ProjectClass(new NodeClass(Paths.get("").resolve("test")));
        List<Node> test = project.getRootNode().getChildren();
        System.out.println(project.getFeature(Mandatory.Features.Any.SEARCH).get().execute(project, "sample"));
    }*/
}
