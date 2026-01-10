package com.ead.course.validation;

import com.ead.course.dtos.CourseDto;
import com.ead.course.enums.UserType;
import com.ead.course.models.UserModel;
import com.ead.course.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Optional;
import java.util.UUID;

@Component
public class CourseValidator implements Validator {

    @Autowired
    @Qualifier("defaultValidator") // Evita conflitos quando se tem mais de um validator
    private Validator validator;

    @Autowired
    private UserService userService;

    // Implementação obrigatória da interface Validator
    @Override
    public boolean supports(Class<?> aClass) {
        return false;
    }

    @Override
    public void validate (
            Object o,
            Errors errors
    ) {
        CourseDto courseDto = (CourseDto) o; // Casting para transformar object em CourseDto

        validator.validate(courseDto, errors); // Faz uma validação das anotação em CourseDTO. Ex: @NotBlank, @NotNull

        // Se não existir erro entra na validação do instrutor
        if (!errors.hasErrors()) {
            validateUserInstructor(courseDto.getUserInstructor(), errors);
        }
    }

    private void validateUserInstructor (
            UUID instructorId,
            Errors errors
    ) {
        Optional<UserModel> userModelOptional = userService.findById(instructorId);

        if (!userModelOptional.isPresent()) {
            errors.rejectValue("userInstructor", "UserInstructorError", "Instructor not found");
        }

        if (userModelOptional.get().getUserType().equals(UserType.STUDENT.toString())) {
            errors.rejectValue("userInstructor", "UserInstructorError", "User must be INSTRUCTOR or ADMIN");
        }
    }

}