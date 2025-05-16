package com.grup7.Service;

import com.grup7.Dto.OrderDto;
import com.grup7.Entity.Category;
import com.grup7.Entity.Order;
import com.grup7.Exception.OrderException;
import com.grup7.Repository.IOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private IOrderRepository orderRepository;

    @Autowired
    private ExternalMenuService externalMenuService;

    private void validateOrderDto(OrderDto orderDto) {
        if (orderDto == null) {
            throw new OrderException("Sipariş bilgileri boş olamaz");
        }
        if (orderDto.getReservationCode() == null || orderDto.getReservationCode().trim().isEmpty()) {
            throw new OrderException("Rezervasyon kodu boş olamaz");
        }
        if (orderDto.getCategoryIds() == null || orderDto.getCategoryIds().isEmpty()) {
            throw new OrderException("En az bir kategori seçilmelidir");
        }
    }

    public Order saveOrderWithCategories(OrderDto orderDto) {
        try {
            // Sipariş bilgilerini doğrula
            validateOrderDto(orderDto);

            // Kategorilerin geçerliliğini kontrol et
            List<Category> allCategories = externalMenuService.getCategories();
            List<String> validCategoryIds = allCategories.stream()
                    .map(Category::getIdCategory)
                    .collect(Collectors.toList());

            boolean allCategoriesValid = orderDto.getCategoryIds().stream()
                    .allMatch(validCategoryIds::contains);

            if (!allCategoriesValid) {
                throw new OrderException("Bir veya daha fazla geçersiz kategori ID'si");
            }

            // Siparişi oluştur ve kaydet
            Order order = new Order();
            order.setReservationCode(orderDto.getReservationCode().trim());
            order.setCategoryIds(orderDto.getCategoryIds());

            try {
                return orderRepository.save(order);
            } catch (Exception e) {
                throw new OrderException("Sipariş kaydedilirken bir hata oluştu: " + e.getMessage());
            }

        } catch (OrderException e) {
            throw e;
        } catch (Exception e) {
            throw new OrderException("Sipariş işlemi sırasında beklenmeyen bir hata oluştu: " + e.getMessage());
        }
    }

    public List<String> getCategoryNamesByReservationCode(String reservationCode) {
        try {
            // Rezervasyon kodunu doğrula
            if (reservationCode == null || reservationCode.trim().isEmpty()) {
                throw new OrderException("Rezervasyon kodu boş olamaz");
            }

            // Siparişi bul
            Order order = orderRepository.findByReservationCode(reservationCode)
                    .orElseThrow(() -> new OrderException("Bu rezervasyon koduna ait sipariş bulunamadı: " + reservationCode));

            // Tüm kategorileri getir
            List<Category> allCategories;
            try {
                allCategories = externalMenuService.getCategories();
            } catch (Exception e) {
                throw new OrderException("Kategori bilgileri alınırken hata oluştu: " + e.getMessage());
            }

            // Kategori isimlerini eşleştir ve döndür
            return order.getCategoryIds().stream()
                    .map(categoryId -> allCategories.stream()
                            .filter(category -> category.getIdCategory().equals(categoryId))
                            .map(Category::getStrCategory)
                            .findFirst()
                            .orElseThrow(() -> new OrderException("Geçersiz kategori ID'si: " + categoryId)))
                    .collect(Collectors.toList());

        } catch (OrderException e) {
            throw e;
        } catch (Exception e) {
            throw new OrderException("Kategori isimleri alınırken beklenmeyen bir hata oluştu: " + e.getMessage());
        }
    }
}