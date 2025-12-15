package com.opsmonsters.quick_bite.repositories;
import com.opsmonsters.quick_bite.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
public interface UserRepo extends JpaRepository<Users, Long> {
    Optional<Users> findByEmail(String email);
    Long findUserIdByEmail(@Param("email") String email);
    Optional<Users> findByResetToken(String resetToken);
    Optional<Users> findByUserId(Long userId);
}