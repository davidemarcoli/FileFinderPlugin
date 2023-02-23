package dev.davidemarcoli.filechangerplugin.associatedFiles;

import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerAdapter;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.MessageBusConnection;
import org.jetbrains.annotations.NotNull;

public class FileChangeListener implements FileEditorManagerListener {
    @Override
    public void fileOpened(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
        System.out.println("File opened: " + file.getName());
    }

    @Override
    public void fileClosed(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
        System.out.println("File closed: " + file.getName());
    }

    @Override
    public void selectionChanged(@NotNull FileEditorManagerEvent event) {
        System.out.println("File selected: " + event.getNewFile().getName());
        MessageBus messageBus = event.getManager().getProject().getMessageBus();
        messageBus.syncPublisher(FileChangeNotifier.FILE_CHANGE_NOTIFIER_TOPIC).fileChanged(event.getNewFile().getName());
    }
}