package com.grup7.Service;

import com.grup7.Dto.UserDto;
import com.grup7.Dto.TableDto;
import com.grup7.Entity.User;
import com.grup7.Entity.Table;
import com.grup7.Exception.ValidationException;
import com.grup7.Repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private TableService tableService;

    // İsim ve soyisim validasyonu için yeni metod
    private void validateNameAndSurname(String name, String surname) {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("İsim alanı boş bırakılamaz");
        }
        if (surname == null || surname.trim().isEmpty()) {
            throw new ValidationException("Soyisim alanı boş bırakılamaz");
        }

        // İsim ve soyisimde sadece harf kontrolü
        if (!name.matches("^[a-zA-ZçÇğĞıİöÖşŞüÜ\\s]+$")) {
            throw new ValidationException("İsim sadece harflerden oluşmalıdır");
        }
        if (!surname.matches("^[a-zA-ZçÇğĞıİöÖşŞüÜ\\s]+$")) {
            throw new ValidationException("Soyisim sadece harflerden oluşmalıdır");
        }
    }

    // Table entity'sini TableDto'ya dönüştüren yardımcı metod
    private TableDto convertToTableDto(Table table, LocalDate reservationDate) {
        TableDto dto = new TableDto();
        dto.setId(table.getId());
        dto.setTableNumber(table.getTableNumber());
        dto.setReservationDate(reservationDate);
        return dto;
    }

    // UserDto'yu User entity'sine dönüştüren yardımcı metod
    private User convertToUser(UserDto userDto) {
        // Validasyon kontrolü
        validateNameAndSurname(userDto.getName(), userDto.getSurname());

        User user = new User();
        user.setName(userDto.getName().trim()); // Baştaki ve sondaki boşlukları temizle
        user.setSurname(userDto.getSurname().trim()); // Baştaki ve sondaki boşlukları temizle
        user.setDate(userDto.getDate());
        return user;
    }

    // User entity'sini UserDto'ya dönüştüren yardımcı metod
    private UserDto convertToUserDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setSurname(user.getSurname());
        dto.setDate(user.getDate());
        dto.setReservationCode(user.getReservationCode());
        if (user.getReservedTable() != null) {
            dto.setReservedTable(convertToTableDto(user.getReservedTable(), user.getDate()));
        }
        return dto;
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToUserDto)
                .collect(Collectors.toList());
    }

    public UserDto addUser(UserDto userDto) {
        // Validasyon kontrolü burada yapılacak
        validateNameAndSurname(userDto.getName(), userDto.getSurname());

        List<Table> availableTables = tableService.getAvailableTables(userDto.getDate());

        if (availableTables.isEmpty()) {
            throw new ValidationException("Belirtilen tarih için uygun masa bulunmamaktadır");
        }

        Table selectedTable = availableTables.get(0);
        boolean reserved = tableService.reserveTable(selectedTable.getId(), userDto.getDate());

        if (!reserved) {
            throw new ValidationException("Masa rezervasyonu yapılamadı");
        }

        User newUser = convertToUser(userDto);
        newUser.setReservedTable(selectedTable);

        User savedUser = userRepository.save(newUser);
        return convertToUserDto(savedUser);
    }

    public UserDto updateUser(Long id, UserDto userDto) {
        // Validasyon kontrolü
        validateNameAndSurname(userDto.getName(), userDto.getSurname());

        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User updatedUser = userOptional.get();

            if (!updatedUser.getDate().equals(userDto.getDate())) {
                if (updatedUser.getReservedTable() != null) {
                    tableService.cancelReservation(
                            updatedUser.getReservedTable().getId(),
                            updatedUser.getDate()
                    );
                }

                List<Table> availableTables = tableService.getAvailableTables(userDto.getDate());

                if (availableTables.isEmpty()) {
                    throw new ValidationException("Yeni tarih için uygun masa bulunmamaktadır");
                }

                Table newTable = availableTables.get(0);
                tableService.reserveTable(newTable.getId(), userDto.getDate());
                updatedUser.setReservedTable(newTable);
            }

            updatedUser.setName(userDto.getName().trim());
            updatedUser.setSurname(userDto.getSurname().trim());
            updatedUser.setDate(userDto.getDate());

            User savedUser = userRepository.save(updatedUser);
            return convertToUserDto(savedUser);
        }
        throw new ValidationException("Kullanıcı bulunamadı");
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Kullanıcı bulunamadı"));

        if (user.getReservedTable() != null) {
            tableService.cancelReservation(user.getReservedTable().getId(), user.getDate());
        }

        userRepository.deleteById(id);
    }
}