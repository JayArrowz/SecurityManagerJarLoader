package com.sandbox;

public class PolicyFile {
    private PolicyPermission[] allowedFiles;
    private PolicyPermission[] socketPermissions;
    private String jarUrl;
    private PolicyPermission[] urlPermissions;
    private String[] mainArgs;
    private PolicyPermission[] propertyPermissions;
    private PolicyPermission[] runtimePermissions;
    public PolicyFile() {

    }

    public PolicyPermission[] getAllowedFiles() {
        return allowedFiles;
    }

    public PolicyPermission[] getSocketPermissions() {
        return socketPermissions;
    }

    public String getJarUrl() {
        return jarUrl;
    }

    public PolicyPermission[] getUrlPermissions() {
        return urlPermissions;
    }

    public String[] getMainArgs() {
        return mainArgs;
    }

    public PolicyPermission[] getPropertyPermissions() {
        return propertyPermissions;
    }

    public PolicyPermission[] getRuntimePermissions() {
        return runtimePermissions;
    }
}
