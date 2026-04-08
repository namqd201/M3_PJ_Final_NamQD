package com.tmdt.m3_pj_final_namqd.repository;

import com.tmdt.m3_pj_final_namqd.entity.Role;
import com.tmdt.m3_pj_final_namqd.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsernameAndIsDeletedFalse(String username);

    Optional<User> findByEmailAndIsDeletedFalse(String email);

    boolean existsByUsernameAndIsDeletedFalse(String username);

    boolean existsByEmailAndIsDeletedFalse(String email);

    List<User> findByRoleAndIsDeletedFalse(Role role);

    Optional<User> findByIdAndIsDeletedFalse(Long id);
}
