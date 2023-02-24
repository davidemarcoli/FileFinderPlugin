package dev.davidemarcoli.filefinder.associatedFiles;

import com.intellij.util.messages.Topic;

public interface FileChangeNotifier {
    Topic<FileChangeNotifier> FILE_CHANGE_NOTIFIER_TOPIC = Topic.create("File Change Notifier", FileChangeNotifier.class);

    void fileChanged(String fileName);
}
