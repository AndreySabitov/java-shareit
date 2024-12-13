package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.enums.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingStorage extends JpaRepository<Booking, Long> {
    List<Booking> findAllByTenantId(Long userId, Sort sort);

    List<Booking> findAllByTenantIdAndEndBefore(Long userId, LocalDateTime dateTime, Sort sort);

    List<Booking> findAllByTenantIdAndStartAfter(Long userId, LocalDateTime dateTime, Sort sort);

    @Query(value = "select bk from Booking as bk where bk.tenant.id = ?1 and bk.start < ?2 and bk.end > ?2")
    List<Booking> findAllByTenantIdAndBetweenStartAndEnd(Long userId, LocalDateTime dateTime, Sort sort);

    List<Booking> findAllByTenantIdAndStatus(Long userId, BookingStatus status, Sort sort);

    List<Booking> findAllByItemOwnerId(Long userId, Sort sort);

    List<Booking> findAllByItemOwnerIdAndEndBefore(Long userId, LocalDateTime dateTime, Sort sort);

    List<Booking> findAllByItemOwnerIdAndStartAfter(Long userId, LocalDateTime dateTime, Sort sort);

    @Query(value = "select bk from Booking as bk where bk.item.owner.id = ?1 and bk.start < ?2 and bk.end > ?2")
    List<Booking> findAllByItemOwnerIdAndBetweenStartAndEnd(Long userId, LocalDateTime dateTime, Sort sort);

    List<Booking> findAllByItemOwnerIdAndStatus(Long userId, BookingStatus status, Sort sort);

    Booking findByTenantIdAndItemIdAndStatusAndEndBefore(Long userId, Long itemId,
                                                         BookingStatus status, LocalDateTime dateTime);

    @Query(value = "select bk from Booking as bk " +
            "where bk.item.id = ?1 and bk.end < ?2 order by bk.end desc limit 1")
    List<Booking> findAllByItemIdBeforeTime(Long itemId, LocalDateTime dateTime);

    @Query(value = "select bk from Booking as bk " +
            "where bk.item.id = ?1 and bk.start > ?2 order by bk.start limit 1")
    List<Booking> findAllByItemIdAfterTime(Long itemId, LocalDateTime dateTime);
}
