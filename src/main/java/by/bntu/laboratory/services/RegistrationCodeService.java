package by.bntu.laboratory.services;

import by.bntu.laboratory.models.RegistrationCode;
import by.bntu.laboratory.models.User;
import by.bntu.laboratory.repo.RegistrationCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegistrationCodeService {
    @Autowired
    private RegistrationCodeRepository registrationCodeRepository;

    // Метод для проверки существования кода регистрации
    public boolean isRegistrationCodeValid(String code) {
        return registrationCodeRepository.existsByCode(code);
    }
    public List<RegistrationCode> list() {
        return (List<RegistrationCode>) registrationCodeRepository.findAll();
    }

    // Дополнительные методы для регистрации пользователей...
}
