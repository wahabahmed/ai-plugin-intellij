package com.turbo.dev.plugin.utils;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vcs.changes.ContentRevision;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.ChangeListManager;
import com.turbo.dev.plugin.dto.scan.ScanRequest;

import java.util.*;

public class FilesScanner {
    public static String scanAllFiles() {
        Project project =
                ProjectManager.getInstance().getOpenProjects()[0];
        VirtualFile[] roots =
                ProjectRootManager.getInstance(project)
                        .getContentRoots();
        StringBuilder combinedData = new StringBuilder();
        for (VirtualFile root : roots) {
            VfsUtilCore.iterateChildrenRecursively(root, null, file -> {
                if (!file.isDirectory()) {
                    String path = file.getPath();
                    System.out.println("FILE: " + path);

                    // Read content
                    try {
                        if (FileFilter.isAllowedFile(path)) {
                            String code = new String(file.contentsToByteArray());
                             combinedData.append(DataFormatter.buildDataFormat(path, CodeSanitizer.sanitizeCode(code)));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                return true;
            });
        }

        return combinedData.toString();
    }

    public static String scanChangedFiles() throws VcsException {
        StringBuilder combinedData = new StringBuilder();
        Project project =
                ProjectManager.getInstance().getOpenProjects()[0];
        ChangeListManager manager =
                ChangeListManager.getInstance(project);

        List<Change> changes = new ArrayList<>(manager.getAllChanges());

        for (Change change : changes) {
            ContentRevision afterRevision = change.getAfterRevision();
            if (null != afterRevision) {
                String path =
                        change.getAfterRevision()
                                .getFile()
                                .getPath();
                if (FileFilter.isAllowedFile(path)) {
                    String code = afterRevision.getContent();
                    combinedData.append(DataFormatter.buildDataFormat(path, CodeSanitizer.sanitizeCode(code)));
                }
                //System.out.println("CHANGED FILE: " + path);
            }
        }
        return combinedData.toString();
    }

    public static ScanRequest getScanRequest() {
        try {
            String combinedData = FilesScanner.scanChangedFiles();

            if (combinedData != null && combinedData.length() <= 0) {
                combinedData = FilesScanner.scanAllFiles();
            }

            ScanRequest request = new ScanRequest();
            String batchId = String.format("batch-%s", UUID.randomUUID());
            String compressedCode = CodeCompressor.getCompressedData(combinedData, batchId);
            request.setBatchId(batchId);
            request.setData(compressedCode);
            request.setCompressedSize(SizeUtil.getSizeInMB(compressedCode));
            request.setOriginalSize(SizeUtil.getSizeInMB(combinedData));

            String target = "<<<CLASS_START";
            int index = combinedData.indexOf(target);
            int count = 0;

            while (index >= 0) {
                count++;
                // Find the next occurrence starting from the index after the current match
                index = combinedData.indexOf(target, index + target.length());
            }

            request.setTotalFiles(count);

            return request;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
