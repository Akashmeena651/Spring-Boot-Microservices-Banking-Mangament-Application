package org.training.user.service.service.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;
import org.training.user.service.exception.ResourceConflictException;
import org.training.user.service.model.Status;
import org.training.user.service.model.dto.UserDto;
import org.training.user.service.model.entity.User;
import org.training.user.service.model.mapper.UserMapper;
import org.training.user.service.repository.UserRepository;
import org.training.user.service.service.KeycloakService;
import org.training.user.service.service.UserService;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final KeycloakService keycloakService;

    private UserMapper userMapper = new UserMapper();

    @Override
    public UserDto createUser(UserDto userDto) {

        List<UserRepresentation> userRepresentations = keycloakService.readUserByEmail(userDto.getEmailId());
        if(userRepresentations.size() > 0) {
            log.error("This emailId is already registered as a user");
            throw new ResourceConflictException("This emailId is already registered as a user");
        }

        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setUsername(userDto.getEmailId());
        userRepresentation.setEmailVerified(false);
        userRepresentation.setEnabled(false);
        userRepresentation.setEmail(userDto.getEmailId());

        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setValue(userDto.getPassword());
        credentialRepresentation.setTemporary(false);
        userRepresentation.setCredentials(Collections.singletonList(credentialRepresentation));

        Integer userCreationResponse = keycloakService.createUser(userRepresentation);

        if (userCreationResponse.equals(201)) {

            List<UserRepresentation> representations = keycloakService.readUserByEmail(userDto.getEmailId());
            userDto.setAuthId(representations.get(0).getId());
            userDto.setStatus(Status.PENDING);
            userDto.setIdentificationNumber(UUID.randomUUID().toString());
            User user = userMapper.convertToEntity(userDto);
            return userMapper.convertToDto(userRepository.save(user));
        }
        throw new RuntimeException("User with identification number not found");
    }


}
