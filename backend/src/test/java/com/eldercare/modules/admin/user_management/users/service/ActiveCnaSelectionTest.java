package com.eldercare.modules.admin.user_management.users.service;

import com.eldercare.modules.admin.user_management.RoleEntity;
import com.eldercare.modules.admin.user_management.UserEntity;
import com.eldercare.modules.admin.user_management.UserRepository;
import com.eldercare.modules.admin.user_management.role.repository.RoleRepository;
import com.eldercare.modules.admin.user_management.users.mapper.UserMapper;
import com.eldercare.modules.admin.user_management.users.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ActiveCnaSelectionTest {
    private final UserRepository users = mock(UserRepository.class);
    private final UserServiceImpl service = new UserServiceImpl(
            users, mock(RoleRepository.class), new UserMapper());

    @Test
    void includesBothCnaRoleNamesAndExcludesOtherRolesAndInactiveAccounts() {
        when(users.findAllByIsDeletedFalse()).thenReturn(List.of(
                user(23L, "CNA", "ACTIVE"), user(9L, "CNA_(Caregiver)", "ACTIVE"),
                user(10L, "CNA_(Caregiver)", "INACTIVE"), user(2L, "System_Administrator", "ACTIVE"),
                user(5L, "NURSE", "ACTIVE")));

        assertEquals(List.of(23L, 9L), service.getActiveCnas().stream().map(u -> u.id()).toList());
    }

    @Test
    void neverOffersNonCnaStaffWhenNoActiveCnaExists() {
        when(users.findAllByIsDeletedFalse()).thenReturn(List.of(
                user(2L, "System_Administrator", "ACTIVE"), user(5L, "NURSE", "ACTIVE")));

        assertTrue(service.getActiveCnas().isEmpty());
    }

    private UserEntity user(Long id, String roleName, String status) {
        RoleEntity role = new RoleEntity();
        role.setRoleName(roleName);
        UserEntity user = new UserEntity();
        user.setId(id);
        user.setFirstName("Staff");
        user.setLastName(String.valueOf(id));
        user.setRole(role);
        user.setStatus(status);
        return user;
    }
}
