package com.smartshelf.services;

import com.smartshelf.models.*;
import com.smartshelf.repositories.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class StockAlertService {

    private static final Logger log = LoggerFactory.getLogger(StockAlertService.class);

    private final NotificationRepository notificationRepository;

    public StockAlertService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Transactional
    public void checkAndTriggerStockAlert(Product product) {
        if (product == null || !product.isActive()) return;

        int stock = product.getCurrentStock() != null ? product.getCurrentStock() : 0;
        int minStock = product.getMinimumStock() != null ? product.getMinimumStock() : 5;

        if (stock <= 0) {
            boolean exists = notificationRepository.existsByTypeAndEntityTypeAndEntityIdAndIsReadFalse(
                    NotificationType.CRITICAL_STOCK, EntityType.PRODUCT, product.getId());
            if (!exists) {
                Notification notif = Notification.builder()
                        .type(NotificationType.CRITICAL_STOCK)
                        .title("Out of Stock: " + product.getName())
                        .message(product.getName() + " is completely OUT OF STOCK (0 " + product.getUnit() + "). Immediate restocking required.")
                        .entityType(EntityType.PRODUCT)
                        .entityId(product.getId())
                        .isRead(false)
                        .build();
                notificationRepository.save(notif);
            }
        } else if (stock <= minStock) {
            boolean exists = notificationRepository.existsByTypeAndEntityTypeAndEntityIdAndIsReadFalse(
                    NotificationType.LOW_STOCK, EntityType.PRODUCT, product.getId());
            if (!exists) {
                Notification notif = Notification.builder()
                        .type(NotificationType.LOW_STOCK)
                        .title("Low Stock: " + product.getName())
                        .message(product.getName() + " is below minimum stock threshold. Current Stock: " + stock + " " + product.getUnit() + " (Min: " + minStock + ").")
                        .entityType(EntityType.PRODUCT)
                        .entityId(product.getId())
                        .isRead(false)
                        .build();
                notificationRepository.save(notif);
            }
        }
    }

    @Transactional
    public void checkAndTriggerExpiryAlert(Product product) {
        if (product == null || !product.isActive() || product.getExpiryDate() == null) return;

        LocalDate today = LocalDate.now();
        long daysUntil = ChronoUnit.DAYS.between(today, product.getExpiryDate());

        if (daysUntil < 0) {
            boolean exists = notificationRepository.existsByTypeAndEntityTypeAndEntityIdAndIsReadFalse(
                    NotificationType.EXPIRED_PRODUCT, EntityType.PRODUCT, product.getId());
            if (!exists) {
                Notification notif = Notification.builder()
                        .type(NotificationType.EXPIRED_PRODUCT)
                        .title("Expired Product: " + product.getName())
                        .message(product.getName() + " expired on " + product.getExpiryDate() + ". Remove from active inventory.")
                        .entityType(EntityType.PRODUCT)
                        .entityId(product.getId())
                        .isRead(false)
                        .build();
                notificationRepository.save(notif);
            }
        } else if (daysUntil <= 7) {
            boolean exists = notificationRepository.existsByTypeAndEntityTypeAndEntityIdAndIsReadFalse(
                    NotificationType.EXPIRING_SOON, EntityType.PRODUCT, product.getId());
            if (!exists) {
                Notification notif = Notification.builder()
                        .type(NotificationType.EXPIRING_SOON)
                        .title("Expiring Soon (" + daysUntil + " days): " + product.getName())
                        .message(product.getName() + " expires in " + daysUntil + " days (" + product.getExpiryDate() + "). Consider applying smart discount.")
                        .entityType(EntityType.PRODUCT)
                        .entityId(product.getId())
                        .isRead(false)
                        .build();
                notificationRepository.save(notif);
            }
        }
    }
}
