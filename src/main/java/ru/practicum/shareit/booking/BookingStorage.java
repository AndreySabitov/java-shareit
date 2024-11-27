package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.enums.BookingStatus;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

public interface BookingStorage extends JpaRepository<Booking, BigInteger> {
    List<Booking> findAllByTenantId(BigInteger userId);

    List<Booking> findAllByTenantIdAndEndBefore(BigInteger userId, LocalDateTime dateTime);

    List<Booking> findAllByTenantIdAndStartAfter(BigInteger userId, LocalDateTime dateTime);

    @Query(value = "select bk from Booking as bk where bk.tenant.id = ?1 and bk.start < ?2 and bk.end > ?2")
    List<Booking> findAllByTenantIdAndBetweenStartAndEnd(BigInteger userId, LocalDateTime dateTime);

    List<Booking> findAllByTenantIdAndStatus(BigInteger userId, BookingStatus status);

    List<Booking> findAllByItemOwnerId(BigInteger userId);

    List<Booking> findAllByItemOwnerIdAndEndBefore(BigInteger userId, LocalDateTime dateTime);

    List<Booking> findAllByItemOwnerIdAndStartAfter(BigInteger userId, LocalDateTime dateTime);

    @Query(value = "select bk from Booking as bk where bk.item.owner.id = ?1 and bk.start < ?2 and bk.end > ?2")
    List<Booking> findAllByItemOwnerIdAndBetweenStartAndEnd(BigInteger userId, LocalDateTime dateTime);

    List<Booking> findAllByItemOwnerIdAndStatus(BigInteger userId, BookingStatus status);

    Booking findByTenantIdAndItemIdAndStatusAndEndBefore(BigInteger userId, BigInteger itemId,
                                                         BookingStatus status, LocalDateTime dateTime);

    @Query(value = "select bk from Booking as bk " +
            "where bk.item.id = ?1 and bk.end < ?2 order by bk.end desc limit 1")
    List<Booking> findAllByItemIdBeforeTime(BigInteger itemId, LocalDateTime dateTime);

    @Query(value = "select bk from Booking as bk " +
            "where bk.item.id = ?1 and bk.start > ?2 order by bk.start limit 1")
    List<Booking> findAllByItemIdAfterTime(BigInteger itemId, LocalDateTime dateTime);
}
