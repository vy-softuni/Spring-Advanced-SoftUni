package app.mendnook.hub.config;

import app.mendnook.hub.gathering.GatheringState;
import app.mendnook.hub.gathering.WorkshopGathering;
import app.mendnook.hub.gathering.WorkshopGatheringRepository;
import app.mendnook.hub.member.AppRole;
import app.mendnook.hub.member.MemberAccount;
import app.mendnook.hub.member.MemberAccountRepository;
import app.mendnook.hub.mend.ItemKind;
import app.mendnook.hub.mend.MendRequest;
import app.mendnook.hub.mend.MendRequestRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Set;

@Configuration
@Profile("!test")
public class DemoDataInitializer {

    @Bean
    CommandLineRunner seedMainDatabase(MemberAccountRepository memberRepository,
                                       MendRequestRepository mendRepository,
                                       WorkshopGatheringRepository gatheringRepository,
                                       PasswordEncoder passwordEncoder) {
        return arguments -> {
            if (memberRepository.count() == 0) {
                MemberAccount admin = memberRepository.save(new MemberAccount(
                        "steward@mendnook.local", "Mira Steward",
                        passwordEncoder.encode("MendNookAdmin2026!"), Set.of(AppRole.ADMIN)));
                MemberAccount coordinator = memberRepository.save(new MemberAccount(
                        "host@mendnook.local", "Teo Workshop Host",
                        passwordEncoder.encode("MendNookHost2026!"), Set.of(AppRole.COORDINATOR)));
                MemberAccount member = memberRepository.save(new MemberAccount(
                        "member@mendnook.local", "Nadia Neighbour",
                        passwordEncoder.encode("MendNookMember2026!"), Set.of(AppRole.MEMBER)));
                mendRepository.save(new MendRequest(
                        "Walnut bedside lamp", ItemKind.SMALL_APPLIANCE,
                        "The switch clicks normally, but the lamp flickers whenever the cable moves near the base.",
                        3, member));
                mendRepository.save(new MendRequest(
                        "Canvas travel bag", ItemKind.TEXTILE,
                        "The main zipper slider no longer joins the teeth and one carrying strap is beginning to separate.",
                        2, coordinator));
                admin.reviseProfile("Mira Steward", "Electrical safety, careful diagnostics, and workshop planning");
                memberRepository.save(admin);
            }
            if (gatheringRepository.count() == 0) {
                WorkshopGathering gathering = new WorkshopGathering(
                        "Small fixes, long lives", "Riverside Community Room",
                        LocalDateTime.now().plusDays(10).withHour(18).withMinute(30).withSecond(0).withNano(0), 14);
                gathering.changeState(GatheringState.OPEN);
                gatheringRepository.save(gathering);
                gatheringRepository.save(new WorkshopGathering(
                        "Textile rescue table", "North Hall Studio",
                        LocalDateTime.now().plusDays(24).withHour(11).withMinute(0).withSecond(0).withNano(0), 10));
            }
        };
    }
}
