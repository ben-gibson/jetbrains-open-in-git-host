package uk.co.ben_gibson.repositorymapper.Context;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vfs.VirtualFile;
import git4idea.GitUtil;
import git4idea.GitVcs;
import git4idea.actions.GitBranch;
import git4idea.commands.GitCommand;
import org.jetbrains.annotations.Nullable;
import uk.co.ben_gibson.repositorymapper.Settings.Mapping;
import uk.co.ben_gibson.repositorymapper.Settings.Settings;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Context Factory
 */
public class ContextFactory
{

    /**
     * Create context.
     *
     * @param project   The active project.
     * @param settings  The settings.
     *
     * @return Context
     */
    public Context create(Project project, Settings settings) throws MalformedURLException, VcsException {

        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();

        Integer caretPosition = null;

        if (editor != null) {
            caretPosition = editor.getCaretModel().getVisualPosition().line;
        }

        VirtualFile file = this.getCurrentFile(project);

        if (file == null || file.getCanonicalPath() == null) {
            return null;
        }

        Mapping mapping = this.getMappingForFile(settings.getMappingList(), file);

        if (mapping == null) {
            return null;
        }

        GitVcs vcs = GitVcs.getInstance(project);

        GitCommand command = new GitCommand(project, vcs.getSettings(), GitUtil.getVcsRoot(project, file));

        List<GitBranch> branchList = command.branchList();

        GitBranch activeBranch = null;

        for(GitBranch branch : branchList){
            if (branch.isActive()) {
                activeBranch = branch;
            }
        }

        String path = file.getCanonicalPath().replace(mapping.getBaseDirectoryPath()+"/", "");

        return new Context(
            settings.getHostAsURL(),
            mapping.getProject(),
            mapping.getRepository(),
            path,
            activeBranch,
            caretPosition
        );
    }


    /**
     * Get the active file.
     *
     * @param project The project which we want to get the active file from.
     *
     * @return VirtualFile
     */
    @Nullable
    private VirtualFile getCurrentFile(Project project)
    {
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();

        if (editor == null) {
            return null;
        }

        final Document document = editor.getDocument();

        return FileDocumentManager.getInstance().getFile(document);
    }


    /**
     * Get the mapping for a file.
     *
     * @param mappings A collection of mappings.
     * @param file     The file to get a mapping for.
     *
     * @return Mapping
     */
    @Nullable
    private Mapping getMappingForFile(ArrayList<Mapping> mappings, VirtualFile file)
    {
        if (file.getCanonicalPath() == null) {
            return null;
        }

        for(Mapping mapping : mappings){
            if (file.getCanonicalPath().startsWith(mapping.getBaseDirectoryPath(), 0)) {
                return mapping;
            }
        }

        return null;
    }
}
