package app.mendnook.hub.member;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MemberAccountRepository extends JpaRepository<MemberAccount, UUID> {

    Optional<MemberAccount> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);
}
