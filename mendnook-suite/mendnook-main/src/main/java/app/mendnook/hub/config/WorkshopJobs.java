package app.mendnook.hub.config;

import app.mendnook.hub.gathering.WorkshopGatheringService;
import app.mendnook.hub.mend.MendRequestService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class WorkshopJobs {

    private final WorkshopGatheringService gatheringService;
    private final MendRequestService mendRequestService;

    public WorkshopJobs(WorkshopGatheringService gatheringService, MendRequestService mendRequestService) {
        this.gatheringService = gatheringService;
        this.mendRequestService = mendRequestService;
    }

    @Scheduled(cron = "${mendnook.jobs.finish-gatherings-cron:0 15 2 * * *}")
    void finishPastGatherings() {
        gatheringService.finishPastGatherings();
    }

    @Scheduled(fixedDelayString = "${mendnook.jobs.expire-mends-delay-ms:900000}", initialDelayString = "60000")
    void expireStaleMends() {
        mendRequestService.expireStaleSubmissions(Duration.ofDays(45));
    }
}
