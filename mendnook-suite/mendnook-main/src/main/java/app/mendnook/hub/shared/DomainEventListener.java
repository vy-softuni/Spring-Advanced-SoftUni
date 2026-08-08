package app.mendnook.hub.shared;

import app.mendnook.hub.mend.MendSubmittedEvent;
import app.mendnook.hub.pledge.PledgeAcceptedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class DomainEventListener {

    private static final Logger log = LoggerFactory.getLogger(DomainEventListener.class);

    @EventListener
    void noteMendSubmission(MendSubmittedEvent event) {
        log.info("event=mend-submitted mendRequestId={} owner={}", event.mendRequestId(), event.ownerEmail());
    }

    @EventListener
    void notePledgeAcceptance(PledgeAcceptedEvent event) {
        log.info("event=pledge-accepted pledgeId={} mendRequestId={}", event.pledgeId(), event.mendRequestId());
    }
}
