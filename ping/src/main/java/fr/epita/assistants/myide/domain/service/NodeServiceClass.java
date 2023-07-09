package fr.epita.assistants.myide.domain.service;

import fr.epita.assistants.myide.domain.entity.Node;
import fr.epita.assistants.myide.domain.entity.NodeClass;
import fr.epita.assistants.utils.Exceptions;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public enum NodeServiceClass implements fr.epita.assistants.myide.domain.service.NodeService {
    INSTANCE;
    /**
     * Update the content in the range [from, to[.
     * The content must be inserted in any case.
     * i.e. : "hello world" -> update(0, 0, "inserted ") -> "inserted hello world"
     * : "hello world" -> update(0, 5, "inserted ") -> "inserted world"
     *
     * @param node            Node to update (must be a file).
     * @param from            Beginning index of the text to update.
     * @param to              Last index of the text to update (Not included).
     * @param insertedContent Content to insert.
     * @return The node that has been updated.
     * @throws Exception upon update failure.
     */
    @Override
    public Node update(Node node, int from, int to, byte[] insertedContent) {
        return Exceptions.mayThrow(() -> {
            String text = Files.readString(node.getPath(), StandardCharsets.UTF_8);
            text = text.substring(0, from) + new String(insertedContent, StandardCharsets.UTF_8) + text.substring(to);
            Files.writeString(node.getPath(), text, StandardCharsets.UTF_8);
            return node;
        });
    }

    /**
     * Delete the node given as parameter.
     *
     * @param node Node to remove.
     * @return True if the node has been deleted, false otherwise.
     */
    @Override
    public boolean delete(Node node) {
        for (Node child : node.getChildren())
            delete(child);
        return node.getPath().toFile().delete();
    }

    /**
     * Create a new node and the associated file/directory.
     *
     * @param folder Parent node of the new node.
     * @param name   Name of the new node.
     * @param type   Type of the new node.
     * @return Node that has been created.
     * @throws Exception upon creation failure.
     */
    @Override
    public Node create(Node folder, String name, Node.Type type) {
        Path path = folder.getPath().resolve(name);
        if (type == Node.Types.FOLDER)
            Exceptions.mayThrow(() -> path.toFile().mkdirs());
        else if (type == Node.Types.FILE)
            Exceptions.mayThrow(() -> path.toFile().createNewFile());

        return new NodeClass(path, type);
    }

    /**
     * Move node from source to destination.
     *
     * @param nodeToMove        Node to move.
     * @param destinationFolder Destination of the node.
     * @return The node that has been moved.
     * @throws Exception upon move failure.
     */
    @Override
    public Node move(Node nodeToMove, Node destinationFolder) {
        Path source = nodeToMove.getPath();
        Exceptions.mayThrow(() -> Files.move(source, destinationFolder.getPath().resolve(source.getFileName()), StandardCopyOption.REPLACE_EXISTING));
        // Est-ce que faut-il mettre à jour source.getPath() où ça se fait tout seul avec Files.move ?
        // -> c'est un getter, je sais meme pas si je peux le modifier de toute façon
        return nodeToMove;
    }
}
