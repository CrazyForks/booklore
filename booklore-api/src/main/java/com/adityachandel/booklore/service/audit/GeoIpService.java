package com.adityachandel.booklore.service.audit;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CountryResponse;
import com.maxmind.geoip2.record.Country;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for resolving geographic information from IP addresses.
 * Uses MaxMind GeoLite2 database for local, fast, free lookups.
 */
@Slf4j
@Service
public class GeoIpService {

    @Value("${booklore.geoip.database-path:./data/GeoLite2-Country.mmdb}")
    private String databasePath;

    @Value("${booklore.geoip.enabled:false}")
    private boolean geoIpEnabled;

    private DatabaseReader databaseReader;
    private final Map<String, String> cache = new ConcurrentHashMap<>();

    private static final String[] PRIVATE_PREFIXES = {
        "10.", "172.16.", "172.17.", "172.18.", "172.19.",
        "172.20.", "172.21.", "172.22.", "172.23.", "172.24.",
        "172.25.", "172.26.", "172.27.", "172.28.", "172.29.",
        "172.30.", "172.31.", "192.168."
    };

    @PostConstruct
    public void init() {
        if (!geoIpEnabled) {
            log.info("GeoIP service is disabled");
            return;
        }

        try {
            File database = new File(databasePath);
            if (database.exists()) {
                databaseReader = new DatabaseReader.Builder(database).build();
                log.info("GeoIP database loaded from: {}", databasePath);
            } else {
                log.warn("GeoIP database not found at: {}. GeoIP resolution will be disabled.", databasePath);
            }
        } catch (IOException e) {
            log.error("Failed to load GeoIP database: {}", e.getMessage());
        }
    }

    @PreDestroy
    public void shutdown() {
        if (databaseReader != null) {
            try {
                databaseReader.close();
            } catch (IOException e) {
                log.warn("Error closing GeoIP database: {}", e.getMessage());
            }
        }
    }

    /**
     * Resolve country code from IP address.
     * Returns null for private/local IPs or if resolution fails.
     */
    public String resolveCountryCode(String ipAddress) {
        if (!geoIpEnabled || databaseReader == null) {
            return null;
        }

        if (ipAddress == null || ipAddress.isBlank()) {
            return null;
        }

        if (isPrivateOrLocalIp(ipAddress)) {
            return null;
        }

        return cache.computeIfAbsent(ipAddress, this::lookupCountryCode);
    }

    private String lookupCountryCode(String ip) {
        try {
            InetAddress address = InetAddress.getByName(ip);
            CountryResponse response = databaseReader.country(address);
            Country country = response.getCountry();
            String countryCode = country.getIsoCode();
            log.debug("GeoIP lookup for IP: {} -> Country: {}", ip, countryCode);
            return countryCode;
        } catch (IOException | GeoIp2Exception e) {
            log.debug("Failed to resolve country code for IP: {}", ip);
            return null;
        }
    }

    private boolean isPrivateOrLocalIp(String ip) {
        try {
            InetAddress addr = InetAddress.getByName(ip);
            if (addr.isLoopbackAddress() || addr.isAnyLocalAddress()) {
                return true;
            }

            String ipStr = addr.getHostAddress();
            for (String prefix : PRIVATE_PREFIXES) {
                if (ipStr.startsWith(prefix)) {
                    return true;
                }
            }

            return false;
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * Clear the location cache.
     */
    public void clearCache() {
        cache.clear();
    }

    /**
     * Check if GeoIP service is enabled and available.
     */
    public boolean isAvailable() {
        return geoIpEnabled && databaseReader != null;
    }
}
