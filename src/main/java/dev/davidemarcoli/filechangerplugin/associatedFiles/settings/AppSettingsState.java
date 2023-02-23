package dev.davidemarcoli.filechangerplugin.associatedFiles.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Supports storing the application settings in a persistent way.
 * The {@link State} and {@link Storage} annotations define the name of the data and the file name where
 * these persistent application settings are stored.
 */
@State(
        name = "dev.davidemarcoli.filechangerplugin.associatedFiles.settings.AppSettingsState",
        storages = @Storage("SdkSettingsPlugin.xml")
)
public class AppSettingsState implements PersistentStateComponent<AppSettingsState> {

    public String[] searchedFolders = {"backend/src/main/java", "frontend/src/app"};
    public String[] searchedFiles = {"%Service.java", "%ServiceImpl.java", "%Controller.java", "%Repository.java", "%DTO.java", "%Mapper.java",
            "%.ts", "%.service.ts", "%-component.ts", "%-component.html", "%-component.scss", "%-component.spec.ts"};
    public String[] fileKeywords = {"Controller", "Service", "Repository", "Component", "Module", "Model", "DTO", "Mapper", "Interface", "Enum", "Class", "Directive", "Pipe", "Guard", "Resolver", "Interceptor", "Service", "Component", "Module", "Model", "Interface", "Enum", "Class", "Directive", "Pipe", "Guard", "Resolver", "Interceptor"};

    public static AppSettingsState getInstance() {
        return ApplicationManager.getApplication().getService(AppSettingsState.class);
    }

    @Nullable
    @Override
    public AppSettingsState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull AppSettingsState state) {
        XmlSerializerUtil.copyBean(state, this);
    }

}
