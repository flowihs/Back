package ru.bookcrossing.BookcrossingServer.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.bookcrossing.BookcrossingServer.chat.model.Correspondence;
import ru.bookcrossing.BookcrossingServer.chat.model.UsersCorrKey;
import ru.bookcrossing.BookcrossingServer.chat.repository.CorrespondenceRepository;
import ru.bookcrossing.BookcrossingServer.exception.ChatAlreadyCreatedException;
import ru.bookcrossing.BookcrossingServer.exception.UserNotFoundException;
import ru.bookcrossing.BookcrossingServer.user.model.User;
import ru.bookcrossing.BookcrossingServer.user.repository.UserRepository;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CorrespondenceService {

    private final CorrespondenceRepository correspondenceRepository;
    private final UserRepository userRepository;

    public Optional<Correspondence> createChat(int userId, String login){
        User fUser = userRepository.findByLogin(login).orElseThrow();
        Optional<User> sUser = userRepository.findById(userId);
        if(sUser.isPresent()){
            if(sUser.get().isEnabled() && sUser.get().isAccountNonLocked()){
                UsersCorrKey usersCorrKey = new UsersCorrKey();
                usersCorrKey.setFirstUser(fUser);
                usersCorrKey.setSecondUser(sUser.get());
                if(correspondenceRepository.findById(usersCorrKey).isPresent()){
                    throw new ChatAlreadyCreatedException();
                }
                else{
                    Correspondence correspondence = new Correspondence();
                    correspondence.setUsersCorrKey(usersCorrKey);
                    correspondence = correspondenceRepository.save(correspondence);
                    return Optional.of(correspondence);
                }
            }
            else{
                return Optional.empty();
            }
        }
        else{
            throw new UserNotFoundException();
        }
    }

    public boolean deleteChat(int userId, String login){
        User fUser = userRepository.findByLogin(login).orElseThrow();
        User sUser = userRepository.findById(userId).orElseThrow();
        UsersCorrKey usersCorrKey = new UsersCorrKey();
        usersCorrKey.setFirstUser(fUser);
        usersCorrKey.setSecondUser(sUser);
        Optional<Correspondence> correspondence = correspondenceRepository.findById(usersCorrKey);
        if(correspondence.isPresent()){
            correspondenceRepository.delete(correspondence.get());
            return true;
        }
        else return false;
    }
}
