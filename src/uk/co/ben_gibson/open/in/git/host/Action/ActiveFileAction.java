package uk.co.ben_gibson.open.in.git.host.Action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import git4idea.GitUtil;
import git4idea.commands.GitImpl;
import git4idea.repo.GitRepository;
import uk.co.ben_gibson.open.in.git.host.Action.Exception.ActionException;
import uk.co.ben_gibson.open.in.git.host.Git.Branch;
import uk.co.ben_gibson.open.in.git.host.Git.File;
import uk.co.ben_gibson.open.in.git.host.Git.Repository;
import uk.co.ben_gibson.open.in.git.host.OpenInGitHostException;

import java.net.URL;

/**
 * An action triggered from an active file in the editor.
 */
public class ActiveFileAction extends Action
{
    public URL handleAction(Project project, AnActionEvent event) throws OpenInGitHostException
    {
        VirtualFile file = event.getData(CommonDataKeys.VIRTUAL_FILE);

        if (file == null) {
            throw ActionException.fileNotFound();
        }

        GitRepository repository = GitUtil.getRepositoryManager(project).getRepositoryForFile(file);

        if (repository == null) {
            this.logger.warning("Git repository not found, make sure you have registered your version control root: Preferences → Version Control");
            return null;
        }

        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();

        Integer caretPosition = (editor != null) ? editor.getCaretModel().getLogicalPosition().line + 1 : null;

        Repository repo = new Repository(new GitImpl(), repository, Branch.master());

        return this.remoteUrlFactory.createUrlToRemotePath(
            repo.origin(),
            repo.currentBranch(),
            repo.relativePath(new File(file)),
            caretPosition,
            this.settings.getForceSSL()
        );
    }

    protected boolean shouldActionBeEnabled(AnActionEvent event)
    {
        return (event.getData(CommonDataKeys.VIRTUAL_FILE) != null);
    }
}
