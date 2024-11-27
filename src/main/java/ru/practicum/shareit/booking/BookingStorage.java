package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.enums.BookingStatus;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

public interface BookingStorage extends JpaRepository<Booking, BigInteger> {
    List<Booking> findAllByTenantId(Integer userId);

    List<Booking> findAllByTenantIdAndEndBefore(Integer userId, LocalDateTime dateTime);

    List<Booking> findAllByTenantIdAndStartAfter(Integer userId, LocalDateTime dateTime);

    @Query(value = "select bk from Booking as bk where bk.tenant.id = ?1 and bk.start < ?2 and bk.end > ?2")
    List<Booking> findAllByTenantIdAndBetweenStartAndEnd(Integer userId, LocalDateTime dateTime);

    List<Booking> findAllByTenantIdAndStatus(Integer userId, BookingStatus status);

    List<Booking> findAllByItemOwnerId(Integer userId);

    List<Booking> findAllByItemOwnerIdAndEndBefore(Integer userId, LocalDateTime dateTime);

    List<Booking> findAllByItemOwnerIdAndStartAfter(Integer userId, LocalDateTime dateTime);

    @Query(value = "select bk from Booking as bk where bk.item.owner.id = ?1 and bk.start < ?2 and bk.end > ?2")
    List<Booking> findAllByItemOwnerIdAndBetweenStartAndEnd(Integer userId, LocalDateTime dateTime);

    List<Booking> findAllByItemOwnerIdAndStatus(Integer userId, BookingStatus status);

    Booking findByTenantIdAndItemIdAndStatusAndEndBefore(Integer userId, BigInteger itemId,
                                                         BookingStatus status, LocalDateTime dateTime);

    @Query(value = "select bk from Booking as bk " +
            "where bk.item.id = ?1 and bk.end < ?2 order by bk.end desc limit 1")
    List<Booking> findAllByItemIdBeforeTime(BigInteger itemId, LocalDateTime dateTime);

    @Query(value = "select bk from Booking as bk " +
            "where bk.item.id = ?1 and bk.start > ?2 order by bk.start limit 1")
    List<Booking> findAllByItemIdAfterTime(BigInteger itemId, LocalDateTime dateTime);
}
