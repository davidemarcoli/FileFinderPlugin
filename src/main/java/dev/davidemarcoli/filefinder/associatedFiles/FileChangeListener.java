package dev.davidemarcoli.filefinder.associatedFiles;

import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.messages.MessageBus;
import org.jetbrains.annotations.NotNull;

public class FileChangeListener implements FileEditorManagerListener {
    @Override
    public void fileOpened(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
        System.out.println("File opened: " + file.getName());
    }

    @Override
    public void fileClosed(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
        System.out.println("File closed: " + file.getName());
        MessageBus messageBus = source.getProject().getMessageBus();
        messageBus.syncPublisher(FileChangeNotifier.FILE_CHANGE_NOTIFIER_TOPIC).fileChanged(null);
    }

    @Override
    public void selectionChanged(@NotNull FileEditorManagerEvent event) {
        if (event.getNewFile() == null) return;
        System.out.println("File selected: " + event.getNewFile().getName());
        MessageBus messageBus = event.getManager().getProject().getMessageBus();
        messageBus.syncPublisher(FileChangeNotifier.FILE_CHANGE_NOTIFIER_TOPIC).fileChanged(event.getNewFile().getName());
    }
}