package app.mendnook.hub.mend;

import java.util.UUID;

public record MendSubmittedEvent(UUID mendRequestId, String ownerEmail) {
}
