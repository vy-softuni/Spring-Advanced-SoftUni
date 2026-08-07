package app.mendnook.hub.member;

import java.util.Set;

public enum AppRole {
    MEMBER(Set.of(AppPermission.SUBMIT_MEND, AppPermission.PLEDGE_HELP)),
    COORDINATOR(Set.of(
            AppPermission.SUBMIT_MEND,
            AppPermission.PLEDGE_HELP,
            AppPermission.ORGANIZE_GATHERING,
            AppPermission.REVIEW_PLEDGE,
            AppPermission.MANAGE_MATERIALS,
            AppPermission.VIEW_REPORTS
    )),
    ADMIN(Set.of(AppPermission.values()));

    private final Set<AppPermission> permissions;

    AppRole(Set<AppPermission> permissions) {
        this.permissions = Set.copyOf(permissions);
    }

    public Set<AppPermission> getPermissions() {
        return permissions;
    }
}
