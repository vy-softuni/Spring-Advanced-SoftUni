package app.mendnook.hub.pledge;

import java.util.UUID;

public record PledgeAcceptedEvent(UUID pledgeId, UUID mendRequestId) {
}
