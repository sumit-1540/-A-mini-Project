package com.smartshelf.repositories;

import com.smartshelf.models.EntityType;
import com.smartshelf.models.Notification;
import com.smartshelf.models.NotificationType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findAllByOrderByCreatedAtDesc();

    List<Notification> findAllByOrderByCreatedAtDesc(Pageable pageable);

    List<Notification> findByIsReadFalseOrderByCreatedAtDesc();

    long countByIsReadFalse();

    boolean existsByTypeAndEntityTypeAndEntityIdAndIsReadFalse(
            NotificationType type,
            EntityType entityType,
            Long entityId
    );

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.isRead = false")
    void markAllAsRead();
}
