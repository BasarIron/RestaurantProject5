package com.grup7.Service;

import com.grup7.Entity.OrderLog;
import com.grup7.Entity.User;
import com.grup7.Exception.OrderLogException;
import com.grup7.Repository.IOrderLogRepository;
import com.grup7.Repository.IUserRepository;
import com.grup7.Util.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class OrderLogService {

    @Autowired
    private IOrderLogRepository orderLogRepository;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private UserService userService;

    private void validateUser(User user) {
        if (user.getReservedTable() == null) {
            throw new OrderLogException("Kullanıcının rezerve edilmiş masası bulunmamaktadır");
        }
        if (user.getReservationCode() == null || user.getReservationCode().isEmpty()) {
            throw new OrderLogException("Rezervasyon kodu bulunamadı");
        }
        if (user.getDate() == null) {
            throw new OrderLogException("Rezervasyon tarihi bulunamadı");
        }
    }

    @Transactional
    public OrderLog closeOrder(Long orderId) {
        try {
            // Kullanıcıyı bul
            User user = userRepository.findById(orderId)
                    .orElseThrow(() -> new OrderLogException("Rezervasyon bulunamadı: ID = " + orderId));

            // Kullanıcı bilgilerini doğrula
            validateUser(user);

            // Log kaydı oluştur
            OrderLog orderLog = new OrderLog();
            try {
                orderLog.setCustomerName(user.getName());
                orderLog.setCustomerSurname(user.getSurname());
                orderLog.setTableNumber(user.getReservedTable().getTableNumber());
                orderLog.setReservationCode(user.getReservationCode());
                orderLog.setReservationDate(user.getDate().atStartOfDay());
                orderLog.setClosedAt(LocalDateTime.now());
            } catch (Exception e) {
                throw new OrderLogException("Log kaydı oluşturulurken hata oluştu: " + e.getMessage());
            }

            // Dosyaya log yaz
            try {
                LogUtil.logToFile(
                        user.getName(),
                        user.getSurname(),
                        user.getReservedTable().getTableNumber(),
                        user.getReservationCode()
                );
            } catch (Exception e) {
                throw new OrderLogException("Dosyaya log yazılırken hata oluştu: " + e.getMessage());
            }

            // Log'u veritabanına kaydet
            try {
                return orderLogRepository.save(orderLog);
            } catch (Exception e) {
                throw new OrderLogException("Log veritabanına kaydedilirken hata oluştu: " + e.getMessage());
            }
        } catch (OrderLogException e) {
            throw e; // Özel exception'ları tekrar fırlat
        } catch (Exception e) {
            throw new OrderLogException("Beklenmeyen bir hata oluştu: " + e.getMessage());
        }
    }
}