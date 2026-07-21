package com.adityachandel.booklore.service.audit;

import com.adityachandel.booklore.model.dto.BookLoreUser;
import com.adityachandel.booklore.model.entity.AuditLogEntity;
import com.adityachandel.booklore.model.enums.AuditAction;
import com.adityachandel.booklore.repository.AuditLogRepository;
import com.adityachandel.booklore.util.RequestUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for recording and querying audit trail events.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;
    private final GeoIpService geoIpService;

    public void log(AuditAction action, String description) {
        log(action, null, null, description);
    }

    private static final int MAX_DESCRIPTION_LENGTH = 1024;

    public void log(AuditAction action, String entityType, Long entityId, String description) {
        try {
            Long userId = null;
            String username = "system";

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof BookLoreUser user) {
                userId = user.getId();
                username = user.getUsername();
            }

            String ipAddress = null;
            try {
                ipAddress = RequestUtils.getCurrentRequest().getRemoteAddr();
            } catch (Exception ignored) {
                // Non-HTTP context (scheduled tasks, etc.)
            }

            String countryCode = geoIpService.resolveCountryCode(ipAddress);

            String safeDescription = description;
            if (safeDescription != null && safeDescription.length() > MAX_DESCRIPTION_LENGTH) {
                safeDescription = safeDescription.substring(0, MAX_DESCRIPTION_LENGTH - 3) + "...";
            }

            AuditLogEntity entity = AuditLogEntity.builder()
                    .userId(userId)
                    .username(username)
                    .action(action)
                    .entityType(entityType)
                    .entityId(entityId)
                    .description(safeDescription)
                    .ipAddress(ipAddress)
                    .countryCode(countryCode)
                    .build();

            auditLogRepository.save(entity);
        } catch (Exception e) {
            log.warn("Failed to write audit log: action={}, description={}", action, description, e);
        }
    }

    public Page<AuditLogEntity> getAuditLogs(Pageable pageable) {
        return auditLogRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    public Page<AuditLogEntity> getAuditLogs(AuditAction action, Long userId, String username, LocalDateTime from, LocalDateTime to, Pageable pageable) {
        return auditLogRepository.findFiltered(action, userId, username, from, to, pageable);
    }

    public List<String> getDistinctUsernames() {
        return auditLogRepository.findDistinctUsernames();
    }
}
