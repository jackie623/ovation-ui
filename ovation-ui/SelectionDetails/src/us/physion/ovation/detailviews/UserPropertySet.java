/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package us.physion.ovation.detailviews;

import java.util.Map;

/**
 *
 * @author huecotanks
 */
class UserPropertySet {
    String username;
    String uuid;
    boolean isOwner;
    boolean current;
    Map<String, Object> properties;
    private final TreeWithTableRenderer outer;

    UserPropertySet(String username, String userUuid, boolean owner, boolean currentUser, Map<String, Object> props, final TreeWithTableRenderer outer) {
        this.outer = outer;
        this.username = username;
        this.uuid = userUuid;
        this.isOwner = owner;
        this.properties = props;
        this.current = currentUser;
    }

    String getDisplayName() {
        String s = username + "'s Properties";
        if (isOwner) {
            return s + " (owner)";
        }
        return s;
    }

    boolean isCurrentUser() {
        return current;
    }

    Map<String, Object> getProperties() {
        return properties;
    }
    
}
