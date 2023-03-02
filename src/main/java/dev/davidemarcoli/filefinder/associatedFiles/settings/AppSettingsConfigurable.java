package dev.davidemarcoli.filefinder.associatedFiles.settings;

import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Arrays;

/**
 * Provides controller functionality for application settings.
 */
public class AppSettingsConfigurable implements Configurable {

    private AppSettingsComponent mySettingsComponent;

    // A default constructor with no arguments is required because this implementation
    // is registered as an applicationConfigurable EP

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "File Finder";
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return mySettingsComponent.getPreferredFocusedComponent();
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        mySettingsComponent = new AppSettingsComponent();
        return mySettingsComponent.getPanel();
    }

    @Override
    public boolean isModified() {
        AppSettingsState settings = AppSettingsState.getInstance();
        boolean modified = !Arrays.equals(mySettingsComponent.getSearchedFolders(), settings.searchedFolders);
        modified |= !Arrays.equals(mySettingsComponent.getSearchedFiles(), settings.searchedFiles);
        modified |= !Arrays.equals(mySettingsComponent.getFileKeywords(), settings.fileKeywords);
        return modified;
    }

    @Override
    public void apply() {
        AppSettingsState settings = AppSettingsState.getInstance();
        settings.searchedFolders = mySettingsComponent.getSearchedFolders();
        settings.searchedFiles = mySettingsComponent.getSearchedFiles();
        settings.fileKeywords = mySettingsComponent.getFileKeywords();
    }

    @Override
    public void reset() {
        AppSettingsState settings = AppSettingsState.getInstance();
        mySettingsComponent.setSearchedFolders(settings.searchedFolders);
        mySettingsComponent.setSearchedFiles(settings.searchedFiles);
        mySettingsComponent.setFileKeywords(settings.fileKeywords);
    }

    @Override
    public void disposeUIResources() {
        mySettingsComponent = null;
    }

}
