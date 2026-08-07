package app.mendnook.hub.member;

import jakarta.validation.constraints.NotEmpty;

import java.util.HashSet;
import java.util.Set;

public class RoleChangeForm {

    @NotEmpty(message = "Select at least one role")
    private Set<AppRole> roles = new HashSet<>();

    public Set<AppRole> getRoles() {
        return roles;
    }

    public void setRoles(Set<AppRole> roles) {
        this.roles = roles;
    }
}
