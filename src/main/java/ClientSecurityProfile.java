import com.github.marceloaguiarr.valkyrie.Valkyrie;
import com.github.marceloaguiarr.valkyrie.profiles.SecurityProfile;

import java.io.FilePermission;
import java.net.InetAddress;
import java.net.SocketPermission;
import java.net.URLPermission;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.util.PropertyPermission;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientSecurityProfile implements SecurityProfile {

    static final Pattern pattern = Pattern.compile("\\{(.*?)\\}");
    private final PolicyFile policyFile;

    public ClientSecurityProfile(PolicyFile policyFile) {
        this.policyFile = policyFile;
    }

    private static String parsePath(String path) {
        Matcher matcher = pattern.matcher(path);
        while(matcher.find()) {
            String group = matcher.group();
            String sanatisedGroup = group.replace("{", "").replace("}", "");
            String properties = System.getProperty(sanatisedGroup);
            path = path.replace(group, properties);
        }
        return path;
    }

    @Override
    public PermissionCollection getPermissions() {
        Permissions permissions = new Permissions();

        for(PolicyPermission propPerms : policyFile.getPropertyPermissions()) {
            permissions.add(new PropertyPermission(propPerms.getKey(), propPerms.getValue()));
        }

        for(PolicyPermission propPerms : policyFile.getRuntimePermissions()) {
            permissions.add(new RuntimePermission(propPerms.getKey(), propPerms.getValue()));
        }

        for(PolicyPermission file : policyFile.getAllowedFiles()) {
            Valkyrie.doPrivileged(() -> {
                String filePath = parsePath(file.getKey());
                permissions.add(new FilePermission(filePath, file.getValue()));
                return null;
            });
        }
        for(PolicyPermission socketPerm : policyFile.getSocketPermissions()) {
            permissions.add(new SocketPermission(socketPerm.getKey(), socketPerm.getValue()));
        }

        for(PolicyPermission urlPerms : policyFile.getUrlPermissions()) {
            permissions.add(new URLPermission(urlPerms.getKey(), urlPerms.getValue()));
        }
        return permissions;
    }
}
