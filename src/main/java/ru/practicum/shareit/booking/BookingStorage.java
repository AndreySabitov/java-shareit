package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.enums.BookingStatus;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

public interface BookingStorage extends JpaRepository<Booking, BigInteger> {
    @Query(value = "select bk from Booking as bk where bk.tenant.id = ?1")
    List<Booking> findAllByTenantId(Integer userId);

    @Query(value = "select bk from Booking as bk where bk.item.owner.id = ?1")
    List<Booking> findAllBookingsOfItemsOfOwner(Integer userId);

    @Query(value = "select bk from Booking as bk " +
            "where bk.tenant.id = ?1 and bk.item.id = ?2 and bk.end < ?3 and bk.status = ?4")
    Booking findByUserIdAndItemId(Integer userId, BigInteger itemId, LocalDateTime dateTime, BookingStatus status);

    @Query(value = "select bk from Booking as bk " +
            "where bk.item.id = ?1 and bk.end < ?2 order by bk.end desc limit 1")
    List<Booking> findAllByItemIdBeforeTime(BigInteger itemId, LocalDateTime dateTime);

    @Query(value = "select bk from Booking as bk " +
            "where bk.item.id = ?1 and bk.start > ?2 order by bk.start limit 1")
    List<Booking> findAllByItemIdAfterTime(BigInteger itemId, LocalDateTime dateTime);
}
